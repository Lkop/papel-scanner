package com.lkop.qr_scanner.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.lkop.qr_scanner.network.AsyncGetJSONFromURL;
import com.lkop.qr_scanner.network.AsyncResults;
import com.lkop.qr_scanner.network.AsyncSearchClassroom;
import com.lkop.qr_scanner.models.ClassroomInfo;
import com.lkop.qr_scanner.adapters.CustomListAdapter;
import com.lkop.qr_scanner.constants.DefineURLS;
import com.example.lkop.qr_scanner.R;
import com.lkop.qr_scanner.ui.activities.ActivityClassroom;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class FragmentAllClassrooms extends Fragment implements AdapterView.OnItemClickListener{

    private ListView list_view;
    private ArrayList<ClassroomInfo> list_classrooms;
    private CustomListAdapter adapter;

    private boolean list_clicked = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        list_classrooms = new ArrayList<>();

        new AsyncGetJSONFromURL(DefineURLS.GET_CLASSROOMS, null).run(new AsyncResults() {
            @Override
            public void taskResultsObject(Object results) {

                JSONArray jsonArray = (JSONArray)results;

                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        list_classrooms.add(new ClassroomInfo(obj.getString("id"),
                                                              obj.getString("classroom_token"),
                                                              obj.getString("subject_name").toUpperCase(),
                                                              obj.getString("subject_professor").toUpperCase(),
                                                              obj.getString("datetime"),
                                                              obj.getString("type_text")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

//        try {
//            AsyncGetJSONFromURL.getThread().join();
//        }catch (InterruptedException e){}

        View view = inflater.inflate(R.layout.fragment_all_classrooms, container, false);

        list_view = (ListView)view.findViewById(R.id.listview_all_classrooms);

        list_view.setOnItemClickListener(this);

        //adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, R.id.custom_list_item, list_classrooms);
        adapter = new CustomListAdapter(list_classrooms, getContext());

        list_view.setAdapter(adapter);

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


        if(!list_clicked) {

            //Clicke only one time
            list_clicked = true;

            final ClassroomInfo classroom_info = list_classrooms.get(position);

            new AsyncSearchClassroom(classroom_info.getClassroomToken(), new AsyncResults() {

                @Override
                public void taskResultsObject(Object results) {

                    String results_str = (String) results;

                    if (results_str.length() <= 2) {

                        Toast.makeText(getContext(), "Δεν βρέθηκε το τμήμα.", Toast.LENGTH_SHORT).show();

                    } else {

                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                        SharedPreferences.Editor editor = preferences.edit();

                        editor.putString("classroom_id", classroom_info.getID());
                        editor.putString("classroom_token", classroom_info.getClassroomToken());
                        editor.putString("classroom_subject_name", classroom_info.getSubjectName());
                        editor.putString("classroom_subject_prof", classroom_info.getSubjectProfessor());
                        editor.putInt("classroom_timer", 10 * 60 * 1000);
                        editor.apply();

                        Intent intent = new Intent(getContext(), ActivityClassroom.class);
                        getContext().startActivity(intent);

                        Activity ac = (Activity) getContext();
                        ac.finish();
                    }
                }
            }).execute();

        }
//        Toast.makeText(getActivity(), classroom_info.getClassroomToken(), Toast.LENGTH_SHORT).show();

//        // TODO Auto-generated method stub
//        String value = parent.getItemAtPosition(position).toString();
//        Toast.makeText(getActivity(), value, Toast.LENGTH_SHORT).show();
    }
}
