package com.lkop.qr_scanner.ui.activities;

import androidx.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.AlertDialog;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.View;

import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.lkop.qr_scanner.network.AsyncMatchPassId;
import com.lkop.qr_scanner.network.AsyncPostDataToURL;
import com.lkop.qr_scanner.network.AsyncResults;
import com.lkop.qr_scanner.network.AsyncSearchStudent;
import com.lkop.qr_scanner.network.AsyncSearchStudentFromUOP;
import com.lkop.qr_scanner.constants.DefineURLS;
import com.lkop.qr_scanner.ui.fragments.FragmentPreviewClassroomQR;
import com.lkop.qr_scanner.ui.fragments.FragmentSearchStudent;
import com.example.lkop.qr_scanner.R;
import com.lkop.qr_scanner.models.StudentData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActivityClassroom extends AppCompatActivity {

    private TableLayout tableLayout;
    private TextView current_classroom_view, current_classroom_date_view, students_counter_text_view;

    private SharedPreferences preferences;
    private Context context = this;

    private ArrayList<StudentData> students_list = null;

    private final Activity activity = this;

    private Button change_classroom_btn, add_student_btn;

    private int classroom_timer;
    private String classroom_token, classroom_id, classroom_subject_name, classroom_subject_prof;

    private CountDownTimer changeClassroomTimer;

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom);

        //disable rotation
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        classroom_id = preferences.getString("classroom_id", "");
        classroom_token = preferences.getString("classroom_token", "");
        classroom_subject_name = preferences.getString("classroom_subject_name", "");
        classroom_subject_prof = preferences.getString("classroom_subject_prof", "");
        classroom_timer = preferences.getInt("classroom_timer", -1);

        changeClassroomTimer = assignClassroomTimer(classroom_timer);
        changeClassroomTimer.start();


        current_classroom_view = (TextView) findViewById(R.id.current_classroom);
        current_classroom_view.setText(classroom_subject_name);

        current_classroom_date_view = (TextView) findViewById(R.id.current_classroom_date);
        current_classroom_date_view.setText(classroom_subject_prof);

        students_counter_text_view = (TextView) findViewById(R.id.students_counter);

        changeClassroomTimer.start();

        current_classroom_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               previewQR();
            }
        });

        change_classroom_btn = (Button) findViewById(R.id.change_classroom);
        change_classroom_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showChangeClassroomAlertDialog();
            }
        });

        add_student_btn = (Button) findViewById(R.id.add_in_classroom);
        add_student_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startScanner("Σκανάρετε το QR στην πίσω όψη από το πάσο");
            }
        });

        //initialize tableLayout
        initView();

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

        String open_fragment = getIntent().getStringExtra("openFragment");

        if(open_fragment != null && open_fragment.equals("FragmentMatchPassId")) {

            new AsyncMatchPassId(getIntent().getStringExtra("pass_id_arg"), getIntent().getStringExtra("am_arg"), new AsyncResults() {

                @Override
                public void taskResultsObject(Object results) {

                    new AsyncSearchStudentFromUOP(getIntent().getStringExtra("am_arg"), new AsyncResults() {

                        @Override
                        public void taskResultsObject(Object results) {

                            String student_name = "";
                            String student_lastname = "";

                            try {
                                JSONObject obj = new JSONObject((String) results);

                                student_name = obj.getString("firstname");
                                student_lastname = obj.getString("lastname");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            try {
                                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                                // Vibrate for 500 milliseconds
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                                } else {
                                    //deprecated in API 26
                                    v.vibrate(500);
                                }
                            }catch (NullPointerException e){

                            }

                            Fragment fr = new FragmentSearchStudent();
                            Bundle bdl = new Bundle();
                            bdl.putString("name_arg", student_name);
                            bdl.putString("lastname_arg", student_lastname);
                            bdl.putString("am_arg", getIntent().getStringExtra("am_arg"));
                            //bdl.putString("pass_id_arg", getIntent().getStringExtra("pass_id_arg"));
                            fr.setArguments(bdl);
                            startFragment(fr);

                        }
                    }).execute();
                }
            }).execute();

            //Clear Intent's passing args from re loading them
            getIntent().putExtra("openFragment", (String)null);
        }else {
            //Send OkHttp post parameters
            Map<String, String> classroom_data = new HashMap<>();

            classroom_data.put("post_classroom_token", classroom_token);

            new AsyncPostDataToURL(DefineURLS.FETCH_CLASSROOM_STUDENTS, classroom_data)
                .run(new AsyncResults() {
                @Override
                public void taskResultsObject(Object results) {

                    String r = (String) results;

                    final TextView text_empty_view = (TextView) findViewById(R.id.text_empty);

                    if(results == null || r.isEmpty()) {

                        //clear table view
                        tableLayout.removeAllViews();
                        students_list = null;

                        //Show empty table message
                        text_empty_view.setVisibility(View.VISIBLE);

                    }else{

                        try {
                            JsonNode root_node = mapper.readTree(r);

                            final int students_counter = root_node.get("students_counter").asInt();

                            //Log.d("YYYY", String.valueOf(students_counter));

                            JsonNode students = root_node.path("classroom_students");

                            students_list = createStudentsList(students);

                            //Should run on UI Thread
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    students_counter_text_view.setText(String.valueOf(students_counter));

                                    createRows();

                                    if(students_counter > 0)
                                        text_empty_view.setVisibility(View.GONE);
                                }
                            });

                        }catch (IOException e){}
                    }
                }
            });
//            //fetch students in this classroom
//            new AsyncFetchClassroomStudents(this, classroom_token, new AsyncResults() {
//
//                @Override
//                public void taskResultsObject(Object results) {
//
//                    String results_str = (String) results;
//                    TextView text_empty_view = (TextView) findViewById(R.id.text_empty);
//
//                    if (results_str.length() == 2) {
//
//                        //clear table view
//                        tableLayout.removeAllViews();
//                        students_list = null;
//
//                        try {
//                            text_empty_view.setVisibility(View.VISIBLE);
//                        } catch (Exception e) {
//                        }
//
//                    } else {
//
//                        students_list = createStudentsList(results_str);
//                        createRows();
//
//                        try {
//                            text_empty_view.setVisibility(View.GONE);
//                        } catch (Exception e) {
//                        }
//
//                    }
//
//                }
//            }).execute();
        }
    }

    private void showChangeClassroomAlertDialog() {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {

                    case DialogInterface.BUTTON_POSITIVE:
                        resetClassroom();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        //Show Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Αλλαγή τμήματος;")
                .setPositiveButton("Ναι", dialogClickListener)
                .setNegativeButton("Όχι", dialogClickListener)
                .show();
    }

    private void startScanner(String screen_msg){

        IntentIntegrator integrator = new IntentIntegrator(activity);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt(screen_msg);
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }

    private void startFragment(Fragment fragment){

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);

        //commit() not working in this case see documentation
        transaction.commitAllowingStateLoss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        final IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if(result != null){

            if(result.getContents() == null){

                Toast.makeText(this, "Ακύρωση", Toast.LENGTH_LONG).show();
            }else{

                add_student_btn.setEnabled(false);
               // qr_results.getResultsQR(result.getContents());

                //fetch students in this classroom

                boolean isDigit = true;

                //Check if AM is only digits
                for (char c : result.getContents().toCharArray()) {
                    if (!Character.isDigit(c)) {
                        isDigit = false;
                    }
                }

                if(isDigit) {

                    new AsyncSearchStudent(result.getContents(), new AsyncResults() {

                        @Override
                        public void taskResultsObject(Object results) {

                            String results_str = (String)results;

                            if(results_str.length() == 2){

                                Toast.makeText(context, "Σκανάρετε τον Α.Μ.", Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(context, ScanOCR_Activity.class);
                                intent.putExtra("pass_id_arg", result.getContents());
                                startActivity(intent);

                            }else {

                                final StudentData student_data = new StudentData();

                                String get_student_name = "";
                                String get_student_lastname = "";
                                String get_student_am = "";

                                try {
                                    JSONObject jobj = new JSONObject((String) results);
                                    JSONArray jsonArray = jobj.getJSONArray("student_info");
                                    JSONObject obj = jsonArray.getJSONObject(0);

                                    //get_student_name = obj.getString("student_name");
                                    //get_student_lastname = obj.getString("student_lastname");
                                    get_student_am = obj.getString("student_am");

                                    student_data.setAM(get_student_am);


                                    new AsyncSearchStudentFromUOP(get_student_am, new AsyncResults() {

                                        @Override
                                        public void taskResultsObject(Object results) {

                                            try {
                                                JSONObject obj = new JSONObject((String) results);

                                                student_data.setName(obj.getString("firstname"));
                                                student_data.setLastname(obj.getString("lastname"));

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }


                                            boolean studentFound = false;

                                            if (students_list != null && !students_list.isEmpty()) {

                                                for (StudentData sd : students_list) {

                                                    if (sd.getAM().equals(student_data.getAM())) {
                                                        studentFound = true;
                                                        break;
                                                    }
                                                }

                                            }

                                            //if student is already in list
                                            if (studentFound) {

                                                Toast.makeText(context, "Ο/H φοιτητής/τρια υπάρχει ήδη.", Toast.LENGTH_SHORT).show();
                                            } else {

                                                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                                                // Vibrate for 500 milliseconds
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                    v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                                                } else {
                                                    //deprecated in API 26
                                                    v.vibrate(500);
                                                }


                                                Fragment fr = new FragmentSearchStudent();
                                                Bundle bdl = new Bundle();
                                                bdl.putString("name_arg", student_data.getName());
                                                bdl.putString("lastname_arg", student_data.getLastname());
                                                bdl.putString("am_arg", student_data.getAM());
                                                bdl.putString("pass_id_arg", result.getContents());
                                                fr.setArguments(bdl);
                                                startFragment(fr);
                                            }
                                        }

                                    }).execute();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            //UI
                            enableAddButton();
                        }
                    }).execute();

                }else{
                    Toast.makeText(this,"Σκανάρετε το πάσο.", Toast.LENGTH_SHORT).show();
                }
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void initView() {
        tableLayout = (TableLayout) findViewById(R.id.students_table);
    }


//    private ArrayList<StudentData> createStudentsList(String results_string){
//
//        ArrayList<StudentData> students_list = new ArrayList<>();
//
//        try {
//
//            JSONObject j_obj = new JSONObject(results_string);
//            JSONArray jsonArray = j_obj.getJSONArray("classroom_students");
//
//            for (int i = 0; i < jsonArray.length(); i++) {
//
//                String student_am = "", student_name = "", student_lastname = "";
//
//
//                    JSONObject obj = jsonArray.getJSONObject(i);
//
//                    student_am = obj.getString("student_am");
//                    student_name = obj.getString("student_name_el");
//                    student_lastname = obj.getString("student_lastname_el");
//
//                StudentData student_data = new StudentData(student_name, student_lastname, student_am);
//
//                students_list.add(student_data);
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return students_list;
//    }

    private ArrayList<StudentData> createStudentsList(JsonNode students_node){

        ArrayList<StudentData> students_list = new ArrayList<>();

        for (JsonNode jsonNode : students_node) {

            String student_am = jsonNode.get("student_am").textValue();
            String student_name = jsonNode.get("student_name_el").textValue();
            String student_lastname = jsonNode.get("student_lastname_el").textValue();

            StudentData student_data = new StudentData(student_name, student_lastname, student_am);
            students_list.add(student_data);
        }

//        try {
//            JSONObject j_obj = new JSONObject(results_string);
//            JSONArray jsonArray = j_obj.getJSONArray("classroom_students");
//
//            for (int i = 0; i < jsonArray.length(); i++) {
//
//                String student_am = "", student_name = "", student_lastname = "";
//
//                JSONObject obj = jsonArray.getJSONObject(i);
//
//                student_am = obj.getString("student_am");
//                student_name = obj.getString("student_name_el");
//                student_lastname = obj.getString("student_lastname_el");
//
//                StudentData student_data = new StudentData(student_name, student_lastname, student_am);
////
////                students_list.add(student_data);
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        return students_list;
    }

    private void createRows() {

        tableLayout.removeAllViews();

        for (StudentData sd : students_list) {

            TableRow tableRow = new TableRow(this);

            tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT));

            // lastname
            TextView textViewLastname = new TextView(this);
            textViewLastname.setText(sd.getLastname());
            textViewLastname.setTypeface(Typeface.DEFAULT);
            textViewLastname.setPadding(0, 0, (int)(10*(getResources().getDisplayMetrics().density)), 0);
            textViewLastname.setWidth(getResources().getDimensionPixelSize(R.dimen.lastname_width));
            //set one line text and ellipsis at the end
            textViewLastname.setMaxLines(1);
            textViewLastname.setEllipsize(TextUtils.TruncateAt.END);
            textViewLastname.setTextColor(Color.parseColor("#000000"));
            tableRow.addView(textViewLastname);

            // name
            TextView textViewName = new TextView(this);
            textViewName.setText(sd.getName());
            textViewName.setTypeface(Typeface.DEFAULT);
            textViewName.setPadding(0, 0, (int)(10*(getResources().getDisplayMetrics().density)), 0);
            textViewName.setWidth(getResources().getDimensionPixelSize(R.dimen.name_width));
            //set one line text and ellipsis at the end
            textViewName.setMaxLines(1);
            textViewName.setEllipsize(TextUtils.TruncateAt.END);
            textViewName.setTextColor(Color.parseColor("#000000"));
            tableRow.addView(textViewName);

            // am
            TextView textViewAM = new TextView(this);
            textViewAM.setText(sd.getAM());
            textViewAM.setTypeface(Typeface.DEFAULT);
            textViewAM.setTextColor(Color.parseColor("#000000"));
            tableRow.addView(textViewAM);

            tableLayout.addView(tableRow, new TableLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        }
    }

    private CountDownTimer assignClassroomTimer(int millis) {

        return new CountDownTimer(millis, 1000) {

            public void onTick(long millisUntilFinished) {
                change_classroom_btn.setText("Αλλαγή\n" + millisUntilFinished / 1000);
                preferences.edit().putInt("classroom_timer", (int) millisUntilFinished).commit();
            }

            public void onFinish() {
                resetClassroom();
                Toast.makeText(getApplicationContext(), "Ο χρόνος παραμονής στο τμήμα έληξε", Toast.LENGTH_LONG).show();
            }
        };
    }


    private void resetClassroom(){

        //Clear saved classroom
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("classroom_token", "");
        editor.putInt("classroom_timer", 0);

        if (editor.commit()) {
            Intent intent = new Intent(context, ActivityMainMenu.class);
            context.startActivity(intent);

            changeClassroomTimer.cancel();

            Activity ac = (Activity) context;
            ac.finish();
        }

    }

    private void previewQR(){

        FragmentPreviewClassroomQR fragmentPreviewClassroomQR = new FragmentPreviewClassroomQR();

        Bundle bundle = new Bundle();
        bundle.putString("classroom_token", classroom_token);

        //Passing parameters to fragment
        fragmentPreviewClassroomQR.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.container, fragmentPreviewClassroomQR);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    private void enableAddButton(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                add_student_btn.setEnabled(true);
            }
        });
    }
}
