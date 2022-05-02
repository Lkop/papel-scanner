package com.lkop.qr_scanner.ui.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;
import com.example.lkop.qr_scanner.R;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.lkop.qr_scanner.adapters.StudentsListAdapter;
import com.lkop.qr_scanner.constants.URLs;
import com.lkop.qr_scanner.models.Classroom;
import com.lkop.qr_scanner.models.Student;
import com.lkop.qr_scanner.network.AsyncHttp;
import com.lkop.qr_scanner.network.AsyncResultsCallbackInterface;
import com.lkop.qr_scanner.ui.fragments.AddStudentFragment;
import com.lkop.qr_scanner.ui.fragments.PreviewClassroomQRFragment;
import com.lkop.qr_scanner.ui.fragments.QRScannerFragment;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClassroomActivity extends AppCompatActivity {

    private ListView students_listview;
    private TextView subject_name_textview, current_classroom_date_view, students_counter_textview;
    private TextView empty_message_textview;
    private SharedPreferences preferences;
    private Classroom classroom;
    private ArrayList<Student> students_list;
    private ImageView qr_preview_imageview;
    private ImageButton exit_classroom_imagebutton;
    private Button add_student_button;
    private int classroom_timer;
    private CountDownTimer changeClassroomTimer;
    private ObjectMapper mapper = new ObjectMapper();
    private StudentsListAdapter students_adapter;
    private Student student_to_add;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom);
        
        gson = new Gson();
        classroom = gson.fromJson(getIntent().getStringExtra("ClassroomClass"), Classroom.class);
        if(classroom == null) {
            preferences = PreferenceManager.getDefaultSharedPreferences(this);
            classroom = gson.fromJson(preferences.getString("ClassroomClass", ""), Classroom.class);
        }

        students_list = new ArrayList<>();
        students_adapter = new StudentsListAdapter(students_list, getApplicationContext());
        students_listview = (ListView) findViewById(R.id.students_listview_classroom_activity);
        students_listview.setAdapter(students_adapter);

        //TODO
        changeClassroomTimer = assignClassroomTimer(classroom.getTimer());
//        changeClassroomTimer.start();

        subject_name_textview = (TextView) findViewById(R.id.current_classroom);
        subject_name_textview.setText(classroom.getSubjectName());

        current_classroom_date_view = (TextView) findViewById(R.id.current_classroom_date);
        current_classroom_date_view.setText(classroom.getSubjectProfessor());

        students_counter_textview = (TextView) findViewById(R.id.students_counter);

        empty_message_textview = (TextView) findViewById(R.id.empty_message_textview);

        qr_preview_imageview = (ImageView) findViewById(R.id.qr_preview_imageview_classroom_activity);
        qr_preview_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               previewQR();
            }
        });

        exit_classroom_imagebutton = (ImageButton) findViewById(R.id.exit_classroom_imagebutton);
        exit_classroom_imagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeClassroomAlertDialog();
            }
        });

            @Override
            public void onClick(View v) {
                startBarcodeScannerFragment("Σκανάρετε το QR από το πάσο");
        add_student_button = (Button) findViewById(R.id.add_student_button_classroom_activity);
        add_student_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startQRScannerFragment("Σκανάρετε το QR από το πάσο");
            }
        });

        getSupportFragmentManager().setFragmentResultListener("qr_scanner_response", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                getSupportFragmentManager().popBackStack();
                parseQR(bundle.getString("qr_text"));
            }
        });

        getSupportFragmentManager().setFragmentResultListener("add_student_to_classroom_response", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                getSupportFragmentManager().popBackStack();
                addStudentΤοClassroom();
            }
        });
    }

    //Because Activity is already opened we re-setting Activity's Intent
    @Override
    protected void onNewIntent (Intent intent){
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onResume(){
        super.onResume();

        Map<String, String> parameters = new HashMap<>();
        parameters.put("classroom_token", classroom.getClassroomToken());

        new AsyncHttp().get(URLs.GET_CLASSROOM_STUDENTS, parameters, new AsyncResultsCallbackInterface() {
            @Override
            public void onSuccess(String json_string) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root_node;
                try {
                    root_node = mapper.readTree(json_string);
                } catch(JsonProcessingException e) {
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Κάτι πήγε στραβά", Toast.LENGTH_SHORT).show());
                    return;
                }

                int students_counter = root_node.get("students_counter").asInt();
                if (students_counter == 0) {
                    runOnUiThread(() -> empty_message_textview.setVisibility(View.VISIBLE));
                    return;
                }

                JsonNode json_node = root_node.get("classroom_students");
                students_list = createStudentsList(json_node);

                runOnUiThread(() -> {
                    students_counter_textview.setText(students_counter + "");
                    empty_message_textview.setVisibility(View.GONE);
                    students_adapter.clear();
                    students_adapter.addAll(students_list);
                });
            }

            @Override
            public void onFailure() {

            }
        });

//        String open_fragment = getIntent().getStringExtra("openFragment");
//
//        if (open_fragment != null && open_fragment.equals("FragmentMatchPassId")) {
//            new AsyncMatchPassId(getIntent().getStringExtra("pass_id_arg"), getIntent().getStringExtra("am_arg"), new AsyncResults() {
//                @Override
//                public void taskResultsObject(Object results) {
//                    new AsyncSearchStudentFromUOP(getIntent().getStringExtra("am_arg"), new AsyncResults() {
//                        @Override
//                        public void taskResultsObject(Object results) {
//                            String student_name = "";
//                            String student_lastname = "";
//                            try {
//                                JSONObject obj = new JSONObject((String) results);
//
//                                student_name = obj.getString("firstname");
//                                student_lastname = obj.getString("lastname");
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                            try {
//                                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//
//                                // Vibrate for 500 milliseconds
//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                                    v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
//                                } else {
//                                    //deprecated in API 26
//                                    v.vibrate(500);
//                                }
//                            }catch (NullPointerException e){
//
//                            }
//
//                            Bundle bdl = new Bundle();
//                            bdl.putString("name_arg", student_name);
//                            bdl.putString("lastname_arg", student_lastname);
//                            bdl.putString("am_arg", getIntent().getStringExtra("am_arg"));
//                            //bdl.putString("pass_id_arg", getIntent().getStringExtra("pass_id_arg"));
//                            Fragment fr = new SearchStudentFragment();
//                            fr.setArguments(bdl);
//                            startFragment(fr);
//                        }
//                    }).execute();
//                }
//            }).execute();
//
//            //Clear Intent's passing args from re loading them
//            getIntent().putExtra("openFragment", (String)null);
//        }
    }

    private void showChangeClassroomAlertDialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        exitClassroom();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        //Show Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Αλλαγή τμήματος;")
                .setPositiveButton("Ναι", dialogClickListener)
                .setNegativeButton("Όχι", dialogClickListener)
                .show();
    }

    private void startBarcodeScannerFragment(String screen_msg){
        ScannerFragment fragment = new ScannerFragment(screen_msg);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container_classroom_activity, fragment);
        transaction.addToBackStack(null);

        //commit() not working in this case see documentation
        transaction.commitAllowingStateLoss();
    }

    private void startFragment(Fragment fragment){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);

        //commit() not working in this case see documentation
        transaction.commitAllowingStateLoss();
    }

    private void parseQR(String qr_text) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("pass_id", qr_text);

        new AsyncHttp().get(URLs.GET_STUDENT, parameters, new AsyncResultsCallbackInterface() {
            @Override
            public void onSuccess(String json_string) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root_node;
                try {
                    root_node = mapper.readTree(json_string).get("student");
                } catch(JsonProcessingException e) {
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Κάτι πήγε στραβά", Toast.LENGTH_SHORT).show());
                    return;
                }

                if (root_node.isNull() || root_node.isEmpty()) {
                    System.out.println("aaa");
                    //todo open rear scanner
                    return;
                }

                student_to_add = new Student(
                        root_node.get("id").asInt(),
                        root_node.get("name").asText(),
                        root_node.get("lastname").asText(),
                        root_node.get("am").asLong(),
                        root_node.get("pass_id").asLong());

                if (students_list.contains(student_to_add)) {
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Ο/H φοιτητής/τρια υπάρχει ήδη", Toast.LENGTH_SHORT).show());
                    return;
                }

                String student_json = gson.toJson(student_to_add);

                Bundle bdl = new Bundle();
                bdl.putString("StudentClass", student_json);

                AddStudentFragment add_student_fragment = new AddStudentFragment();
                add_student_fragment.setArguments(bdl);

                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.container_classroom_activity, add_student_fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }

            @Override
            public void onFailure() {
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Κάτι πήγε στραβά", Toast.LENGTH_SHORT).show());
            }
        });

//        boolean isDigit = true;
//        for (char c : result.getContents().toCharArray()) {
//            if (!Character.isDigit(c)) {
//                isDigit = false;
//            }
//        }

//        barcode_launcher = registerForActivityResult(new ScanContract(), result -> {
//            if(result != null){
//                if(result.getContents() == null){
//                    Toast.makeText(this, "Ακύρωση", Toast.LENGTH_LONG).show();
//                }else{
//                    add_student_btn.setEnabled(false);
//                    // qr_results.getResultsQR(result.getContents());
//                    //fetch students in this classroom
//
//                    if(isDigit) {
//                        new AsyncSearchStudent(result.getContents(), new AsyncResults() {
//                            @Override
//                            public void taskResultsObject(Object results) {
//                                String results_str = (String)results;
//                                if(results_str.length() == 2){
//
//                                    Intent intent = new Intent(getApplicationContext(), ScanOCRActivity.class);
//                                    intent.putExtra("pass_id_arg", result.getContents());
//                                    startActivity(intent);
//                                }else {
//                                    final Student student_data = new Student();
//                                    String get_student_name = "";
//                                    String get_student_lastname = "";
//                                    String get_student_am = "";
//                                    try {
//                                        JSONObject jobj = new JSONObject((String) results);
//                                        JSONArray jsonArray = jobj.getJSONArray("student_info");
//                                        JSONObject obj = jsonArray.getJSONObject(0);
//                                        //get_student_name = obj.getString("student_name");
//                                        //get_student_lastname = obj.getString("student_lastname");
//                                        get_student_am = obj.getString("student_am");
//                                        student_data.setAM(get_student_am);
//
//                                        new AsyncSearchStudentFromUOP(get_student_am, new AsyncResults() {
//                                            @Override
//                                            public void taskResultsObject(Object results) {
//                                                try {
//                                                    JSONObject obj = new JSONObject((String) results);
//                                                    student_data.setName(obj.getString("firstname"));
//                                                    student_data.setLastname(obj.getString("lastname"));
//
//                                                } catch (JSONException e) {
//                                                    e.printStackTrace();
//                                                }
//                                                boolean studentFound = false;
//                                                if (students_list != null && !students_list.isEmpty()) {
//                                                    for (Student sd : students_list) {
//                                                        if (sd.getAM().equals(student_data.getAM())) {
//                                                            studentFound = true;
//                                                            break;
//                                                        }
//                                                    }
//                                                }
//                                                //if student is already in list
//                                                if (studentFound) {
//                                                    Toast.makeText(getApplicationContext(), "Ο/H φοιτητής/τρια υπάρχει ήδη.", Toast.LENGTH_SHORT).show();
//                                                } else {
//                                                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//                                                    // Vibrate for 500 milliseconds
//                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                                                        v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
//                                                    } else {
//                                                        //deprecated in API 26
//                                                        v.vibrate(500);
//                                                    }
//                                                    Fragment fr = new SearchStudentFragment();
//                                                    Bundle bdl = new Bundle();
//                                                    bdl.putString("name_arg", student_data.getName());
//                                                    bdl.putString("lastname_arg", student_data.getLastname());
//                                                    bdl.putString("am_arg", student_data.getAM());
//                                                    bdl.putString("pass_id_arg", result.getContents());
//                                                    fr.setArguments(bdl);
//                                                    startFragment(fr);
//                                                }
//                                            }
//                                        }).execute();
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                                //UI
//                                enableAddButton();
//                            }
//                        }).execute();
//                    }else{
//                        Toast.makeText(this,"Σκανάρετε το πάσο.", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//        });
    }

    public void addStudentΤοClassroom() {
//        if (!what) {
//            return;
//        }
        Map<String, String> parameters = new HashMap<>();
        parameters.put("student_id", student_to_add.getId() + "");
        parameters.put("classroom_token", classroom.getClassroomToken());

        new AsyncHttp().post(URLs.POST_ADD_STUDENT_TO_CLASSROOM, parameters, new AsyncResultsCallbackInterface() {
            @Override
            public void onSuccess(String json_string) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root_node;
                try {
                    root_node = mapper.readTree(json_string);
                } catch(JsonProcessingException e) {
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Κάτι πήγε στραβά", Toast.LENGTH_SHORT).show());
                    return;
                }

                int success = root_node.get("success").asInt();
                if (success != 1) {
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Κάτι πήγε στραβά", Toast.LENGTH_SHORT).show());
                    return;
                }
                students_list.add(0, student_to_add);
                runOnUiThread(() -> {
                    students_adapter.clear();
                    students_adapter.addAll(students_list);
                    students_counter_textview.setText(students_list.size() + "");
                });
            }

            @Override
            public void onFailure() {
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Κάτι πήγε στραβά", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private ArrayList<Student> createStudentsList(JsonNode students_node){
        ArrayList<Student> students_list = new ArrayList<>();
        for (JsonNode student_node : students_node) {
            int id = student_node.get("id").asInt();
            String name = student_node.get("name_el").asText();
            String lastname = student_node.get("lastname_el").asText();
            long am = student_node.get("am").asLong();
            long pass_id = student_node.get("id").asLong();

            Student student = new Student(id, name, lastname, am, pass_id);
            students_list.add(student);
        }
        return students_list;
    }

    private CountDownTimer assignClassroomTimer(int millis) {
        return new CountDownTimer(millis, 1000) {
            public void onTick(long millisUntilFinished) {
                //exit_classroom_button.setText("Αλλαγή\n" + millisUntilFinished / 1000);
                preferences.edit().putInt("classroom_timer", (int) millisUntilFinished).commit();
            }
            public void onFinish() {
                exitClassroom();
                Toast.makeText(getApplicationContext(), "Ο χρόνος παραμονής στο τμήμα έληξε", Toast.LENGTH_LONG).show();
            }
        };
    }

    private void exitClassroom(){
        //changeClassroomTimer.cancel();

        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("ClassroomClass");

        if (editor.commit()) {
            Intent intent = new Intent(this, MainMenuActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void previewQR(){
        Bundle bundle = new Bundle();
        bundle.putString("classroom_token", classroom.getClassroomToken());

        PreviewClassroomQRFragment fragmentPreviewClassroomQR = new PreviewClassroomQRFragment();
        fragmentPreviewClassroomQR.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.container_classroom_activity, fragmentPreviewClassroomQR);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void notifyAdapter() {

    }

    private void enableAddButton(){
        runOnUiThread(() -> add_student_button.setEnabled(true));
    }
}
