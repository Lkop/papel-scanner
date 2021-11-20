package com.lkop.qr_scanner.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.lkop.qr_scanner.R;

public class MatchPassIdActivity extends AppCompatActivity {

    private String pass_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_match_pass_id);

        TextView fullname_textview = (TextView)findViewById(R.id.fullname_match);
        TextView am_textview = (TextView)findViewById(R.id.am_match);
        TextView pass_id_textview = (TextView)findViewById(R.id.pass_id_match);


        //String name = args.getString("name_arg");
        //String lastname = args.getString("lastname_arg");

        final String am =  getIntent().getStringExtra("am_arg");

        pass_id =  getIntent().getStringExtra("pass_id_arg");



        am_textview.setText(am);
        pass_id_textview.setText(pass_id);


        Button match = (Button)findViewById(R.id.match);
        match.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                //new AsyncMatchPassId(pass_id, am).execute();
                //finish();
            }
        });

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
    }
}