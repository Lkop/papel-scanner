package com.lkop.qr_scanner.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lkop.qr_scanner.network.AsyncGetJSONFromURL;
import com.lkop.qr_scanner.network.AsyncPostDataToURL;
import com.lkop.qr_scanner.network.AsyncResults;
import com.lkop.qr_scanner.network.AsyncSearchClassroom;
import com.lkop.qr_scanner.constants.DefineURLS;
import com.example.lkop.qr_scanner.R;
import com.lkop.qr_scanner.ui.activities.ActivityClassroom;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class FragmentCreateClassroom extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private Button create_classroom_btn;

    private ArrayList<String> list_subjects, list_types;
    private ArrayList<String> list_subjects_id, list_types_id;

    private String tmp_subjects_id, tmp_types_id;
    private String classroom_token = "";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        list_subjects_id = new ArrayList<>();
        list_subjects_id.add("");

        //create list and the default item
        list_subjects = new ArrayList<>();
        list_subjects.add("Επιλέξτε Μάθημα");

        new AsyncGetJSONFromURL(DefineURLS.GET_SUBJECTS, null).run(new AsyncResults() {
            @Override
            public void taskResultsObject(Object results) {

                JSONArray jsonArray = (JSONArray)results;

                for (int i = 0; i < jsonArray.length(); i++) {

                    try {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        list_subjects.add(obj.getString("subject_name").toUpperCase());
                        list_subjects_id.add(obj.getString("subject_id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        list_types_id = new ArrayList<>();
        list_types_id.add("");

        //create list and the default item
        list_types = new ArrayList<>();
        list_types.add("Επιλέξτε Τύπο");

        new AsyncGetJSONFromURL(DefineURLS.GET_SUBJECT_TYPES, null).run(new AsyncResults() {
            @Override
            public void taskResultsObject(Object results) {

                JSONArray jsonArray = (JSONArray)results;

                for (int i = 0; i < jsonArray.length(); i++) {

                    try {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        list_types.add(obj.getString("type_text").toUpperCase());
                        list_types_id.add(obj.getString("type_id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //initialize components
        View view = inflater.inflate(R.layout.fragment_create_classroom, container, false);

        //SUBJECTS - SUBJECTS - SUBJECTS - SUBJECTS - SUBJECTS - SUBJECTS
        Spinner spinner_subjects = (Spinner)view.findViewById(R.id.spinner_subjects);

        //event listener
        spinner_subjects.setOnItemSelectedListener(this);

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, list_subjects);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner_subjects.setAdapter(dataAdapter);

        //TYPES - TYPES - TYPES - TYPES - TYPES - TYPES - TYPES - TYPES - TYPES
        Spinner spinner_types = (Spinner)view.findViewById(R.id.spinner_types);

        //event listener
        spinner_types.setOnItemSelectedListener(this);

        // Creating adapter for spinner
        ArrayAdapter<String> arrayAdapter_types = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, list_types);

        // Drop down layout style - list view with radio button
        arrayAdapter_types.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner_types.setAdapter(arrayAdapter_types);

        //Create Button Event
        create_classroom_btn = (Button)view.findViewById(R.id.create_classroom_2);
        //Button event listener
        create_classroom_btn.setOnClickListener(this);



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

            create_classroom_btn.setEnabled(false);

            Map<String, String> classroom_data = new HashMap<>();

            classroom_data.put("post_subject_id", tmp_subjects_id);
            classroom_data.put("post_subject_type_id", tmp_types_id);

            for (Map.Entry<String, String> entry : classroom_data.entrySet()) {
                Toast.makeText(getContext(), entry.getKey() + " == " + entry.getValue(), Toast.LENGTH_SHORT).show();
            }

            new AsyncPostDataToURL(DefineURLS.CREATE_CLASSROOM, classroom_data)
                .run(new AsyncResults() {
                    @Override
                    public void taskResultsObject(Object results) {

                        String r = (String)results;

                        if (results == null || r.isEmpty()) {

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    create_classroom_btn.setEnabled(true);
                                }
                            });

                        }else{

                            try {
                                JSONObject obj = new JSONObject(r);
                                classroom_token = obj.getString("classroom_token");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if(!classroom_token.isEmpty()) {

                                new AsyncSearchClassroom(classroom_token, new AsyncResults() {
                                    @Override
                                    public void taskResultsObject(Object results) {

                                        String results_str = (String) results;

                                        if (results_str.length() <= 2) {

                                            Toast.makeText(getContext(), "Δοκιμάστε ξανά", Toast.LENGTH_SHORT).show();

                                        } else {

                                            String classroom_id = "";
                                            String classroom_subject_name = "";
                                            String classroom_subject_prof = "";

                                            try {

                                                JSONObject j_obj = new JSONObject((String) results);
                                                JSONArray jsonArray = j_obj.getJSONArray("classroom_info");
                                                JSONObject obj = jsonArray.getJSONObject(0);

                                                classroom_id = obj.getString("classroom_id");
                                                classroom_subject_name = obj.getString("classroom_subject_name");
                                                classroom_subject_prof = obj.getString("classroom_subject_prof");

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                                            SharedPreferences.Editor editor = preferences.edit();

                                            editor.putString("classroom_id", classroom_id);
                                            editor.putString("classroom_token", classroom_token);
                                            editor.putString("classroom_subject_name", classroom_subject_name);
                                            editor.putString("classroom_subject_prof", classroom_subject_prof);
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
                        }

                    }
                });
        }
    }

}
