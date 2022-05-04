package com.lkop.qr_scanner.ui.fragments;

import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lkop.qr_scanner.R;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lkop.qr_scanner.constants.URLs;
import com.lkop.qr_scanner.models.Classroom;
import com.lkop.qr_scanner.network.AsyncHttp;
import com.lkop.qr_scanner.network.AsyncResultsCallbackInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateClassroomFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private View view;
    private Button create_classroom_button;
    private ArrayList<String> subjects_list, types_list;
    private ArrayList<String> list_subjects_id, list_types_id;
    private String tmp_subjects_id, tmp_types_id;
    private String classroom_token = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        list_subjects_id = new ArrayList<>();
        list_subjects_id.add("");

        //create list and the default item
        subjects_list = new ArrayList<>();
        subjects_list.add("Επιλέξτε Μάθημα");

        new AsyncHttp().get(URLs.GET_SUBJECTS_AND_TYPES, null, new AsyncResultsCallbackInterface() {
            @Override
            public void onSuccess(String json_string) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root_node = null;
                try {
                    root_node = mapper.readTree(json_string);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

                JsonNode subjects_node = root_node.get("subjects");
                for (int i = 0; i < subjects_node.size(); i++) {
                    JsonNode json_node = subjects_node.get(i);
                    json_node.get("id").asInt();
                    subjects_list.add(json_node.get("name").asText());
                    //json_node.get("token").asText();

                }

                JsonNode subject_types_node = root_node.get("subject_types");
                for (int i = 0; i < subject_types_node.size(); i++) {
                    JsonNode json_node = subject_types_node.get(i);
                    json_node.get("id").asInt();
                    types_list.add(json_node.get("type_text").asText());
                    //json_node.get("token").asText();

                }

//                subjects_list.add(obj.getString("subject_name").toUpperCase());
//                list_subjects_id.add(obj.getString("subject_id"));
            }

            @Override
            public void onFailure() {

            }
        });

        list_types_id = new ArrayList<>();
        list_types_id.add("");

        //create list and the default item
        types_list = new ArrayList<>();
        types_list.add("Επιλέξτε Τύπο");
//todo
//        new AsyncGetJSONFromURL(URLs.GET_SUBJECT_TYPES, null).run(new AsyncResults() {
//            @Override
//            public void taskResultsObject(Object results) {
//
//                JSONArray jsonArray = (JSONArray)results;
//
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    try {
//                        JSONObject obj = jsonArray.getJSONObject(i);
//                        list_types.add(obj.getString("type_text").toUpperCase());
//                        list_types_id.add(obj.getString("type_id"));
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //initialize components
        view = inflater.inflate(R.layout.fragment_create_classroom, container, false);

        //SUBJECTS - SUBJECTS - SUBJECTS - SUBJECTS - SUBJECTS - SUBJECTS
        Spinner spinner_subjects = (Spinner)view.findViewById(R.id.spinner_subjects);

        //event listener
        spinner_subjects.setOnItemSelectedListener(this);

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, subjects_list);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner_subjects.setAdapter(dataAdapter);

        //TYPES - TYPES - TYPES - TYPES - TYPES - TYPES - TYPES - TYPES - TYPES
        Spinner spinner_types = (Spinner)view.findViewById(R.id.spinner_types);

        //event listener
        spinner_types.setOnItemSelectedListener(this);

        // Creating adapter for spinner
        ArrayAdapter<String> arrayAdapter_types = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, types_list);

        // Drop down layout style - list view with radio button
        arrayAdapter_types.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner_types.setAdapter(arrayAdapter_types);

        //Create Button Event
        create_classroom_button = (Button)view.findViewById(R.id.create_classroom_button_create_classroom_fragment);
        //Button event listener
        create_classroom_button.setOnClickListener(this);

        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(position == 0)
            ((TextView)parent.getChildAt(0)).setTextColor(Color.parseColor("#808080"));
        else
            ((TextView)parent.getChildAt(0)).setTextColor(Color.parseColor("#000000"));

        if(parent.getId() == R.id.spinner_subjects){
            tmp_subjects_id = list_subjects_id.get(position);

        }else if(parent.getId() == R.id.spinner_types){
            tmp_types_id = list_types_id.get(position);
        }

        // On selecting a spinner item
        //String item = parent.getItemAtPosition(position).toString();

        //list_subjects_id.get(position);

        // Showing selected spinner item
        //Toast.makeText(parent.getContext(), "Selected: " + item +"  " +position+"    "+list_types_id.get(position), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent){

    }

    @Override
    public void onClick(View view) {
        if(tmp_subjects_id.isEmpty() || tmp_types_id.isEmpty()){
            Toast.makeText(getContext(), "Εισάγετε όλα τα στοιχεία", Toast.LENGTH_SHORT).show();
        }else{
            create_classroom_button.setEnabled(false);

            Map<String, String> classroom_data = new HashMap<>();
            classroom_data.put("post_subject_id", tmp_subjects_id);
            classroom_data.put("post_subject_type_id", tmp_types_id);

            for (Map.Entry<String, String> entry : classroom_data.entrySet()) {
                Toast.makeText(getContext(), entry.getKey() + " == " + entry.getValue(), Toast.LENGTH_SHORT).show();
            }

//            new AsyncPostDataToURL(URLs.POST_CREATE_CLASSROOM, classroom_data)
//                .run(new AsyncResults() {
//                    @Override
//                    public void taskResultsObject(Object results) {
//
//                        String r = (String)results;
//
//                        if (results == null || r.isEmpty()) {
//
//                            getActivity().runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    create_classroom_btn.setEnabled(true);
//                                }
//                            });
//
//                        }else{
//
//                            try {
//                                JSONObject obj = new JSONObject(r);
//                                classroom_token = obj.getString("classroom_token");
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//
//                            if(!classroom_token.isEmpty()) {
//
//                                new AsyncSearchClassroom(classroom_token, new AsyncResults() {
//                                    @Override
//                                    public void taskResultsObject(Object results) {
//
//                                        String results_str = (String) results;
//
//                                        if (results_str.length() <= 2) {
//
//                                            Toast.makeText(getContext(), "Δοκιμάστε ξανά", Toast.LENGTH_SHORT).show();
//
//                                        } else {
//
//                                            String classroom_id = "";
//                                            String classroom_subject_name = "";
//                                            String classroom_subject_prof = "";
//
//                                            try {
//
//                                                JSONObject j_obj = new JSONObject((String) results);
//                                                JSONArray jsonArray = j_obj.getJSONArray("classroom_info");
//                                                JSONObject obj = jsonArray.getJSONObject(0);
//
//                                                classroom_id = obj.getString("classroom_id");
//                                                classroom_subject_name = obj.getString("classroom_subject_name");
//                                                classroom_subject_prof = obj.getString("classroom_subject_prof");
//
//                                            } catch (JSONException e) {
//                                                e.printStackTrace();
//                                            }
//
//                                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
//                                            SharedPreferences.Editor editor = preferences.edit();
//
//                                            editor.putString("classroom_id", classroom_id);
//                                            editor.putString("classroom_token", classroom_token);
//                                            editor.putString("classroom_subject_name", classroom_subject_name);
//                                            editor.putString("classroom_subject_prof", classroom_subject_prof);
//                                            editor.putInt("classroom_timer", 10 * 60 * 1000);
//                                            editor.apply();
//
//                                            Intent intent = new Intent(getContext(), ClassroomActivity.class);
//                                            getContext().startActivity(intent);
//
//                                            Activity ac = (Activity) getContext();
//                                            ac.finish();
//                                        }
//                                    }
//                                }).execute();
//                            }
//                        }
//
//                    }
//                });
        }
    }

}
