package com.lkop.qr_scanner.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import com.example.lkop.qr_scanner.R;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.lkop.qr_scanner.adapters.ClassroomsListAdapter;
import com.lkop.qr_scanner.constants.URLs;
import com.lkop.qr_scanner.models.Classroom;
import com.lkop.qr_scanner.models.User;
import com.lkop.qr_scanner.network.AsyncHttp;
import com.lkop.qr_scanner.network.AsyncResultsCallbackInterface;
import com.lkop.qr_scanner.ui.activities.ClassroomActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyClassroomsFragment extends Fragment implements AdapterView.OnItemClickListener{

    private View view;
    private ListView list_view;
    private User user;
    private ArrayList<Classroom> classrooms_list;
    private ClassroomsListAdapter classrooms_adapter;
    private boolean list_clicked = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Gson gson = new Gson();
        user = gson.fromJson(getArguments().getString("UserClass", ""), User.class);
        if(user == null) {
            Toast.makeText(getContext(), "Κάτι πήγε στραβά", Toast.LENGTH_SHORT).show();
            getActivity().getSupportFragmentManager().popBackStack();
        }

        classrooms_list = new ArrayList<>();

        Map<String, String> parameters = new HashMap<>();
        parameters.put("user_id", user.getId() + "");

        new AsyncHttp().get(URLs.GET_MY_CLASSROOMS, parameters, new AsyncResultsCallbackInterface() {
            @Override
            public void onSuccess(String json_string) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root_node = null;
                try {
                    root_node = mapper.readTree(json_string).get("my_classrooms");
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < root_node.size(); i++) {
                    JsonNode json_node = root_node.get(i);
                    classrooms_list.add(new Classroom(
                            json_node.get("id").asInt(),
                            json_node.get("creator").asInt(),
                            json_node.get("token").asText(),
                            json_node.get("description").asText(),
                            json_node.get("subject").get("name").asText(),
                            json_node.get("subject").get("professor").asText(),
                            json_node.get("datetime").asText(),
                            json_node.get("type").asText())
                    );
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        classrooms_adapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onFailure() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "Κάτι πήγε στραβά", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_classrooms, container, false);

        classrooms_adapter = new ClassroomsListAdapter(classrooms_list, getContext());

        list_view = (ListView)view.findViewById(R.id.listview_all_classrooms);
        list_view.setOnItemClickListener(this);
        list_view.setAdapter(classrooms_adapter);

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(!list_clicked) {
            //Click only one time
            list_clicked = true;

            Classroom classroom = classrooms_list.get(position);

            Gson gson = new Gson();
            String classroom_json = gson.toJson(classroom);

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("ClassroomClass", classroom_json);

            if(editor.commit()) {
                Intent intent = new Intent(getContext(), ClassroomActivity.class);
                startActivity(intent);
                ((Activity) getContext()).finish();
            }
        }
    }
}
