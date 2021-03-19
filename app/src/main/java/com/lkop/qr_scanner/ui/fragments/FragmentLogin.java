package com.lkop.qr_scanner.ui.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lkop.qr_scanner.network.AsyncPostDataToURL;
import com.lkop.qr_scanner.network.AsyncResults;
import com.lkop.qr_scanner.constants.DefineURLS;
import com.example.lkop.qr_scanner.R;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FragmentLogin extends Fragment {

    private Button login_btn;
    private EditText email_editText, password_editText;
    private TextView message_box;

    private String next_fragment_name;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        next_fragment_name = args.getString("next_fragment_name");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        //AdjustResize Programmatically
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        email_editText = (EditText)view.findViewById(R.id.login_email);
        password_editText = (EditText)view.findViewById(R.id.login_password);
        message_box = (TextView)view.findViewById(R.id.message_box);


        //Create Button Event
        login_btn = (Button)view.findViewById(R.id.login_btn);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email_string = email_editText.getText().toString();
                String password_string = password_editText.getText().toString();

                if(email_string.isEmpty() || password_string.isEmpty()){

                    showErrorMessage("Γράψτε Email και Κωδικό");
                }else{

                    //Hide messsage
                    message_box.setVisibility(View.GONE);

                    //Disable Button, Email and Password
                    disableUIComponents();

                    //Pass parameters to OkHttp via Map (Same as Pair Class)
                    Map<String, String> user_data = new HashMap<>();
                    user_data.put("email", email_string + "@uop.gr");
                    user_data.put("password", password_string);

//                    new AsyncPostDataToURL(DefineURLS.VERIFY_USER, user_data).run(
//                        new AsyncResults() {
//                            @Override
//                            public void taskResultsObject(Object results) {
//
//                                String r = (String)results;
//
//                                if (results == null || r.isEmpty()) {
//
//                                    showErrorMessage("Λάθος Στοιχεία");
//                                    enableUIComponents();
//                                }else{
//
//                                    try {
//                                        JSONObject obj = new JSONObject(r);
//                                        String email = obj.getString("email");
//
//                                        //FIX THIS BLOCK (NO NEED)
//                                        //////////////////////////////////////////////////////
//                                        //////////////////////////////////////////////////////
//                                        if(email.equals(email_string)){
//
//                                            Fragment NextFragment = null;
//
//                                            if(next_fragment_name.equals("FragmentCreateClassroom")){
//                                                NextFragment = new FragmentCreateClassroom();
//                                            }else if(next_fragment_name.equals("FragmentAllClassrooms")){
//                                                NextFragment = new FragmentAllClassrooms();
//                                            }
//
//                                            try {
//                                                getActivity().getSupportFragmentManager().popBackStack();
//
//                                                getActivity().getSupportFragmentManager().beginTransaction()
//                                                        //.replace(R.id.fragment_create_classroom, FragmentCreateClassroom)
//                                                        .replace(((ViewGroup)getView().getParent()).getId(), NextFragment)
//                                                        .addToBackStack(null)
//                                                        .commit();
//
//                                            }catch (NullPointerException e){
//                                                e.printStackTrace();
//                                            }
//                                        }
//                                        //////////////////////////////////////////////////////
//                                        //////////////////////////////////////////////////////
//
//                                    }catch(JSONException e){
//                                        enableUIComponents();
//                                    }
//                                }
//                            }
//                        });

                    new AsyncPostDataToURL(DefineURLS.UOP_VERIFY_USER, user_data).run(
                        new AsyncResults() {
                            @Override
                            public void taskResultsObject(Object results) {

                                String r = (String)results;

                                if (results == null || r.isEmpty()) {

                                    showErrorMessage("Λάθος Στοιχεία");
                                    enableUIComponents();
                                }else{

                                    String response_status = "", response_message = "";

                                    try {
                                        ObjectMapper mapper = new ObjectMapper();

                                        JsonNode jsonNode = mapper.readTree(r);

                                        response_status = jsonNode.get("status").asText();
                                        response_message = jsonNode.get("message").asText();

                                    }catch(IOException e){ }

                                    if(response_status.equals("success") && response_message.equals("verified OK")){

                                        Fragment NextFragment = null;

                                        if(next_fragment_name.equals("FragmentCreateClassroom")){
                                            NextFragment = new FragmentCreateClassroom();
                                        }else if(next_fragment_name.equals("FragmentAllClassrooms")){
                                            NextFragment = new FragmentAllClassrooms();
                                        }

                                        //open fragment (code run in background)
                                        openFragmentMainThread(NextFragment);

                                    }else{
                                        showErrorMessage("Λάθος Στοιχεία");
                                        enableUIComponents();
                                    }
                                }
                            }
                        });
                }
            }
        });
        return view;
    }

    private void enableUIComponents(){

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                email_editText.setEnabled(true);
                password_editText.setEnabled(true);
                login_btn.setEnabled(true);
            }
        });
    }

    private void disableUIComponents(){
        email_editText.setEnabled(false);
        password_editText.setEnabled(false);
        login_btn.setEnabled(false);

    }

    private void showErrorMessage(String msg){

        final String tmp_msg = msg;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                message_box.setText(tmp_msg);
                message_box.setVisibility(View.VISIBLE);
            }
        });
    }

    private void openFragmentMainThread(final Fragment fragment){

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                getActivity().getSupportFragmentManager().popBackStack();

                getActivity().getSupportFragmentManager().beginTransaction()
                        //.replace(R.id.fragment_create_classroom, FragmentCreateClassroom)
                        .replace(((ViewGroup)getView().getParent()).getId(), fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }
}
