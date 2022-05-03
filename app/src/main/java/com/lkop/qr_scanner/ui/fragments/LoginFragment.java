package com.lkop.qr_scanner.ui.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import com.example.lkop.qr_scanner.R;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.lkop.qr_scanner.constants.URLs;
import com.lkop.qr_scanner.models.User;
import com.lkop.qr_scanner.network.AsyncHttp;
import com.lkop.qr_scanner.network.AsyncResultsCallbackInterface;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginFragment extends Fragment {

    private View view;
    private Button login_button;
    private EditText email_edittext, password_edittext;
    private TextView error_message_textview;
    private CheckBox save_checkbox;
    private String saved_email, saved_password;
    private String next_fragment_listener;
    private SharedPreferences shared_prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        next_fragment_listener = args.getString("next_fragment_listener");

        shared_prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        saved_email = shared_prefs.getString("PREF_KEY_SAVED_EMAIL", null);
        saved_password = shared_prefs.getString("PREF_KEY_SAVED_PASSWORD", null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login, container, false);

        email_edittext = (EditText)view.findViewById(R.id.login_email);
        password_edittext = (EditText)view.findViewById(R.id.login_password);
        error_message_textview = (TextView)view.findViewById(R.id.error_message_textview_login_fragment);
        save_checkbox = (CheckBox)view.findViewById(R.id.save_checkbox);

        save_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked) {
                    shared_prefs.edit().remove("PREF_KEY_SAVED_EMAIL").apply();
                    shared_prefs.edit().remove("PREF_KEY_SAVED_PASSWORD").apply();
                }
            }
        });

        if(saved_email != null && saved_password != null) {
            email_edittext.setText(saved_email);
            password_edittext.setText(saved_password);
            save_checkbox.setChecked(true);
        }

        login_button = (Button)view.findViewById(R.id.login_btn);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email_string = email_edittext.getText().toString();
                String password_string = password_edittext.getText().toString();

                if(email_string.isEmpty() || password_string.isEmpty()) {
                    showErrorMessage("Γράψτε Email και Κωδικό");
                }else{
                    error_message_textview.setVisibility(View.GONE);

                    enableUIComponents(false);

                    //Pass parameters to OkHttp via Map (Same as Pair Class)
                    Map<String, String> user_data = new HashMap<>();
                    user_data.put("email", email_string + "@uop.gr");
                    user_data.put("password", password_string);

                    new AsyncHttp().post(URLs.POST_VERIFY_USER, user_data, new AsyncResultsCallbackInterface() {
                        @Override
                        public void onSuccess(String json_string) {
                            if (json_string == null || json_string.isEmpty()) {
                                showErrorMessage("Λάθος Στοιχεία");
                                enableUIComponents(true);
                            }else{
                                int user_id = 0;
                                String email = "";
                                try {
                                    ObjectMapper mapper = new ObjectMapper();
                                    JsonNode root_node = mapper.readTree(json_string).get("user");
                                    user_id = root_node.get("id").asInt();
                                    email = root_node.get("email").asText();
                                }catch(IOException e){
                                    e.printStackTrace();
                                }

                                if(email.equals(user_data.get("email"))) {
                                    if (save_checkbox.isChecked()) {
                                        shared_prefs.edit().putString("PREF_KEY_SAVED_EMAIL", email_string).apply();
                                        shared_prefs.edit().putString("PREF_KEY_SAVED_PASSWORD", password_string).apply();
                                    }
                                    User user = new User(user_id, email);

                                    Gson gson = new Gson();
                                    String user_json = gson.toJson(user);

                                    Bundle bundle = new Bundle();
                                    bundle.putString("UserClass", user_json);

                                    getParentFragmentManager().setFragmentResult(next_fragment_listener, bundle);
                                }else{
                                    showErrorMessage("Λάθος Στοιχεία");
                                    enableUIComponents(true);
                                }
                            }
                        }

                        @Override
                        public void onFailure() {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getContext(), "Κάτι πήγε στραβά", Toast.LENGTH_SHORT).show();
                                }
                            });
                            enableUIComponents(true);
                        }
                    });
                }
            }
        });
        return view;
    }

    private void enableUIComponents(boolean state) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                email_edittext.setEnabled(state);
                password_edittext.setEnabled(state);
                login_button.setEnabled(state);
            }
        });
    }

    private void showErrorMessage(String message) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                error_message_textview.setText(message);
                error_message_textview.setVisibility(View.VISIBLE);
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
