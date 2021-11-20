package com.lkop.qr_scanner.ui.fragments;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.lkop.qr_scanner.R;

public class MatchPassIdFragment extends Fragment {

    private String pass_id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_match_pass_id, container, false);


        TextView fullname_textview = (TextView)view.findViewById(R.id.fullname_match);
        TextView am_textview = (TextView)view.findViewById(R.id.am_match);
        TextView pass_id_textview = (TextView)view.findViewById(R.id.pass_id_match);

        Bundle args = getArguments();

        //String name = args.getString("name_arg");
        //String lastname = args.getString("lastname_arg");
        String am = args.getString("am_arg");

        pass_id = args.getString("pass_id_arg");



        am_textview.setText(am);
        pass_id_textview.setText(pass_id);




        /*
        Button add_in_classroom_button = (Button)view.findViewById(R.id.add_in_classroom_fr);
        add_in_classroom_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                new AsyncAddToClassroom(pass_id, new AsyncResults() {

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
                                if(getActivity() instanceof ActivityClassroom){
                                    ((ActivityClassroom)getActivity()).onResume();
                                }

                                getActivity().onBackPressed();

                            }else{
                                Toast.makeText(getActivity(), "Κάτι πήγε στραβά.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                }).execute();
            }
        });

        */
        return view;
    }
}