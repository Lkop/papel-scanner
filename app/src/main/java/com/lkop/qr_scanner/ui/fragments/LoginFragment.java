package com.lkop.qr_scanner.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.lkop.qr_scanner.R;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lkop.qr_scanner.constants.URLS;
import com.lkop.qr_scanner.network.AsyncHttp;
import com.lkop.qr_scanner.network.AsyncResultsCallbackInterface;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginFragment extends Fragment {

    private View view;
    private Button login_button;
    private EditText email_edittext, password_edittext;
    private TextView message_textview;
    private String next_fragment_name;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        next_fragment_name = args.getString("next_fragment_name");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login, container, false);

        email_edittext = (EditText)view.findViewById(R.id.login_email);
        password_edittext = (EditText)view.findViewById(R.id.login_password);
        message_textview = (TextView)view.findViewById(R.id.message_box);

        login_button = (Button)view.findViewById(R.id.login_btn);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email_string = email_edittext.getText().toString();
                String password_string = password_edittext.getText().toString();

                if(email_string.isEmpty() || password_string.isEmpty()) {
                    showErrorMessage("Γράψτε Email και Κωδικό");
                }else{
                    message_textview.setVisibility(View.GONE);

                    //Disable Button, Email and Password
                    disableUIComponents();

                    //Pass parameters to OkHttp via Map (Same as Pair Class)
                    Map<String, String> user_data = new HashMap<>();
                    user_data.put("email", email_string + "@uop.gr");
                    user_data.put("password", password_string);

                    new AsyncHttp().get(URLS.UOP_VERIFY_USER, user_data, new AsyncResultsCallbackInterface() {
                        @Override
                        public void onSuccess(String json_string) {
                            if (json_string == null || json_string.isEmpty()) {
                                showErrorMessage("Λάθος Στοιχεία");
                                enableUIComponents();
                            }else{
                                String response_status = "", response_message = "";
                                try {
                                    ObjectMapper mapper = new ObjectMapper();
                                    JsonNode jsonNode = mapper.readTree(json_string);
                                    response_status = jsonNode.get("status").asText();
                                    response_message = jsonNode.get("message").asText();
                                }catch(IOException e){
                                    e.printStackTrace();
                                }

                                if(response_status.equals("success") && response_message.equals("verified OK")) {
                                    Fragment NextFragment = null;
                                    if(next_fragment_name.equals("FragmentCreateClassroom")) {
                                        NextFragment = new CreateClassroomFragment();
                                    }else if(next_fragment_name.equals("FragmentAllClassrooms")) {
                                        NextFragment = new AllClassroomsFragment();
                                    }
                                    //open fragment (code run in background)
                                    openFragmentMainThread(NextFragment);
                                }else{
                                    showErrorMessage("Λάθος Στοιχεία");
                                    enableUIComponents();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {

                        }
                    });
                }
            }
        });
        return view;
    }

    private void enableUIComponents() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                email_edittext.setEnabled(true);
                password_edittext.setEnabled(true);
                login_button.setEnabled(true);
            }
        });
    }

    private void disableUIComponents() {
        email_edittext.setEnabled(false);
        password_edittext.setEnabled(false);
        login_button.setEnabled(false);
    }

    private void showErrorMessage(String message) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                message_textview.setText(message);
                message_textview.setVisibility(View.VISIBLE);
            }
        });
    }

    private void openFragmentMainThread(Fragment fragment) {
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
