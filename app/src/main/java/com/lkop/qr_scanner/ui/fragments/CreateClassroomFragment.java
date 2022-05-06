package com.lkop.qr_scanner.ui.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import com.example.lkop.qr_scanner.R;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lkop.qr_scanner.adapters.CustomSpinnerAdapter;
import com.lkop.qr_scanner.constants.URLs;
import com.lkop.qr_scanner.dialogs.DatePickerFragment;
import com.lkop.qr_scanner.dialogs.TimePickerFragment;
import com.lkop.qr_scanner.models.CustomSpinnerItem;
import com.lkop.qr_scanner.network.AsyncHttp;
import com.lkop.qr_scanner.network.AsyncResultsCallbackInterface;
import java.util.ArrayList;

public class CreateClassroomFragment extends Fragment implements View.OnClickListener {

    private View view;
    private Button create_classroom_button;
    private CustomSpinnerAdapter subjects_adapter, types_adapter;
    private ArrayList<CustomSpinnerItem> subjects_list, types_list;
    private String classroom_token = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        subjects_list = new ArrayList<>();
        subjects_list.add(new CustomSpinnerItem(-1, "Επιλέξτε Μάθημα"));

        types_list = new ArrayList<>();
        types_list.add(new CustomSpinnerItem(-1, "Επιλέξτε Τύπο"));

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
                    CustomSpinnerItem spinner_item = new CustomSpinnerItem(json_node.get("id").asInt(), json_node.get("name").asText());
                    subjects_list.add(spinner_item);
                }
                requireActivity().runOnUiThread(() -> subjects_adapter.notifyDataSetChanged());

                JsonNode subject_types_node = root_node.get("subject_types");
                for (int i = 0; i < subject_types_node.size(); i++) {
                    JsonNode json_node = subject_types_node.get(i);
                    CustomSpinnerItem spinner_item = new CustomSpinnerItem(json_node.get("id").asInt(), json_node.get("type_text").asText());
                    types_list.add(spinner_item);
                }
                requireActivity().runOnUiThread(() -> types_adapter.notifyDataSetChanged());
            }

            @Override
            public void onFailure() {

            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_create_classroom, container, false);

        Spinner subjects_spinner = (Spinner)view.findViewById(R.id.subjects_spinner_create_classroom_fragment);
        subjects_adapter = new CustomSpinnerAdapter(subjects_list, getContext());
        subjects_spinner.setAdapter(subjects_adapter);
//        subjects_spinner.setOnItemSelectedListener(this);

        Spinner types_spinner = (Spinner)view.findViewById(R.id.types_spinner_create_classroom_fragment);
        types_adapter = new CustomSpinnerAdapter(types_list, getContext());
        types_spinner.setAdapter(types_adapter);
//        types_spinner.setOnItemSelectedListener(this);

        TextView date_textview = (TextView) view.findViewById(R.id.date_textview_create_classroom_fragment);
        date_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment fragment = new DatePickerFragment();
                fragment.show(getParentFragmentManager(), "date_picker");
            }
        });

        TextView time_textview = (TextView) view.findViewById(R.id.time_textview_create_classroom_fragment);
        time_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerFragment fragment = new TimePickerFragment();
                fragment.show(getParentFragmentManager(), "time_picker");
            }
        });

        getParentFragmentManager().setFragmentResultListener("date_picker_response", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                date_textview.setTextColor(Color.BLACK);
                date_textview.setText(result.getInt("day") + "." + result.getInt("month") + "." + result.getInt("year"));
            }
        });

        getParentFragmentManager().setFragmentResultListener("time_picker_response", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                time_textview.setTextColor(Color.BLACK);
                time_textview.setText(result.getInt("hour")+"");
            }
        });

        create_classroom_button = (Button)view.findViewById(R.id.create_classroom_button_create_classroom_fragment);
        create_classroom_button.setOnClickListener(this);

        return view;
    }

    @Override


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
