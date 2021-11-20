package com.lkop.qr_scanner.ui.fragments;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.lkop.qr_scanner.network.AsyncAddToClassroom;
import com.lkop.qr_scanner.network.AsyncResults;
import com.example.lkop.qr_scanner.R;
import com.lkop.qr_scanner.ui.activities.ClassroomActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class SearchStudentFragment extends Fragment {

    private String am, classroom_id, classroom_token;
    private SharedPreferences preferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_search_user, container, false);

        preferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        classroom_id = preferences.getString("classroom_id", "");
        classroom_token = preferences.getString("classroom_token", "");

        TextView fullname_textview = (TextView)view.findViewById(R.id.fullname_fr);
        TextView am_textview = (TextView)view.findViewById(R.id.am_fr);


        Bundle args = getArguments();

        String name = args.getString("name_arg");
        String lastname = args.getString("lastname_arg");
        am = args.getString("am_arg");

        String pass_id = args.getString("pass_id_arg");


        fullname_textview.setText(name + " " + lastname);
        am_textview.setText(am);


        final Button add_in_classroom_button = (Button)view.findViewById(R.id.add_in_classroom_fr);
        add_in_classroom_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                add_in_classroom_button.setEnabled(false);

                new AsyncAddToClassroom(am, classroom_token, new AsyncResults() {

                    @Override
                    public void taskResultsObject(Object results) {

                        String results_str = (String)results;

                        if (results_str.length() == 2){

                            Toast.makeText(getActivity(), "Κάτι πήγε στραβά.", Toast.LENGTH_SHORT).show();
                        }else {

                            int success = 0;

                            try{

                                JSONObject obj = new JSONObject(results_str);

                                success = Character.getNumericValue(obj.getString("success").charAt(0));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if(success == 1 ){

                                //debugging
                                //if(getActivity() instanceof ActivityClassroom){

                                    //Need of typecast becuse onResume() is protected method
                                    ((ClassroomActivity)getActivity()).onResume();
                                //}

                                getActivity().onBackPressed();

                            }else{
                                Toast.makeText(getActivity(), "Κάτι πήγε στραβά.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                }).execute();
            }
        });

        return view;
    }
}
