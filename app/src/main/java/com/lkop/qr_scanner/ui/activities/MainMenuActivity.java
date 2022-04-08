package com.lkop.qr_scanner.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;
import com.example.lkop.qr_scanner.R;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.lkop.qr_scanner.constants.URLs;
import com.lkop.qr_scanner.models.Classroom;
import com.lkop.qr_scanner.network.AsyncGetMinVersion;
import com.lkop.qr_scanner.network.AsyncHttp;
import com.lkop.qr_scanner.network.AsyncResults;
import com.lkop.qr_scanner.network.AsyncResultsCallbackInterface;
import com.lkop.qr_scanner.network.AsyncVerifyVersion;
import com.lkop.qr_scanner.receivers.ConnectionChangeTrigger;
import com.lkop.qr_scanner.ui.fragments.LoginFragment;
import com.lkop.qr_scanner.ui.fragments.RearScannerFragment;
import com.lkop.qr_scanner.ui.fragments.ScannerFragment;
import com.lkop.qr_scanner.ui.fragments.UpdateMessageFragment;
import java.util.HashMap;
import java.util.Map;

public class MainMenuActivity extends AppCompatActivity {

    static boolean IS_UPDATED = true;
    public static boolean IS_ONLINE = false;
    private Context context = this;
    private SharedPreferences preferences;
    private LinearLayout scan_class_layout;
    private ImageButton scan_class_button;
    private Button create_classroom_btn, all_classrooms_btn;
    private TextView center_textview;
    private final Activity activity = this;
    private int scan_type;
    private AsyncVerifyVersion checkForUpdates = null;
    private UpdateMessageFragment UpdateMessageFragment;
    private RelativeLayout footer_layout;
    int a=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);


        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.container_main_menu_activity, new RearScannerFragment());
        transaction.addToBackStack(null);
        transaction.commit();




        footer_layout = (RelativeLayout)findViewById(R.id.footer);
        //setContentView(R.layout.activity_main_menu);

        //////////////

        //API 14 - 28
        ConnectionChangeTrigger cct = new ConnectionChangeTrigger(this);
        cct.registerConnectivityNetworkMonitorForAPI21AndUp();

        //API 14 - 21
//        ConnectionChangeReceiver ccr = new ConnectionChangeReceiver();
//
//        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
//        registerReceiver(ccr, intentFilter);




//        FragmentUpdateMessage = new FragmentUpdateMessage();
//
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.add(R.id.container_scan_classroom, FragmentUpdateMessage);
//        transaction.addToBackStack(null);
//        transaction.commit();


//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"YOUR_CHANNEL_ID")
//                .setSmallIcon(R.drawable.people_64)
//                .setContentTitle("My notification")
//                .setContentText("Much longer text that cannot fit one line...")
//                .setStyle(new NotificationCompat.BigTextStyle().bigText("Much longer text that cannot fit one line..."))
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND | Notification.FLAG_SHOW_LIGHTS)
//                .setLights(Color.WHITE, 300, 100);
//
//        Intent notificationIntent = new Intent(this, ActivityMainMenu.class);
//        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://papel.zabenia.com"));
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, browserIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        builder.setContentIntent(contentIntent);
//
//
//        // Add as notification
//        NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
//        manager.notify(0, builder.build());
        ///////////////

        center_textview = (TextView)findViewById(R.id.center_textview);

        preferences = PreferenceManager.getDefaultSharedPreferences(context);

        if (getSavedClassroom() != "") {
            Intent intent = new Intent(context, ClassroomActivity.class);
            startActivity(intent);
            finish();
        }

        //checkEverySecond();

        scan_class_layout = (LinearLayout) findViewById(R.id.scan_class_layout_mainmenu_activity);
        scan_class_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scan_type = 1;
                ScannerFragment fragment = new ScannerFragment("Σκανάρετε το QR του τμήματος");

                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.container_main_menu_activity, fragment);
                transaction.addToBackStack(null);

                //commit() not working in this case see documentation
                //transaction.commitAllowingStateLoss();
                transaction.commit();
                //startScanner("Σκανάρετε το QR του τμήματος");
            }
        });

        scan_class_button = (ImageButton) findViewById(R.id.scan_class_button_mainmenu_activity);
//        scan_class_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                scan_type = 1;
//                startScanner("Σκανάρετε το QR του τμήματος");
////                FragmentManager manager = getSupportFragmentManager();
////                FragmentTransaction transaction = manager.beginTransaction();
////                transaction.add(R.id.container_scan_classroom, BlankFragment.class, null);
////                transaction.addToBackStack(null);
////                transaction.commit();
//            }
//        });

//        scan_match_btn = (Button) findViewById(R.id.scan_match_pass_id_btn);
//        scan_match_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//
//                scan_type = 2;
//                startScanner("Σκανάρετε το QR στην πίσω όψη από το πάσο");
//            }
//        });

        all_classrooms_btn = (Button)findViewById(R.id.all_classrooms_btn);
        all_classrooms_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LoginFragment FragmentLogin = new LoginFragment();

                Bundle bundle = new Bundle();
                bundle.putString("next_fragment_name", "FragmentAllClassrooms");

                //Passing parameters to fragment
                FragmentLogin.setArguments(bundle);

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.container_main_menu_activity, FragmentLogin);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        create_classroom_btn = (Button)findViewById(R.id.create_classroom_btn);
        create_classroom_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LoginFragment FragmentLogin = new LoginFragment();

                Bundle bundle = new Bundle();
                bundle.putString("next_fragment_name", "FragmentCreateClassroom");

                //Passing parameters to fragment
                FragmentLogin.setArguments(bundle);

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.container_main_menu_activity, FragmentLogin);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        new AsyncGetMinVersion().run();
//        BlankFragment newFragment = new BlankFragment();
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        // Replace whatever is in the fragment_container view with this fragment,
//        // and add the transaction to the back stack so the user can navigate back
//        transaction.add(R.id.container_scan_classroom, newFragment);
//        transaction.addToBackStack(null);
//
//        // Commit the transaction
//        //transaction.commit();

        getSupportFragmentManager().setFragmentResultListener("qr_scanner_response", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                getSupportFragmentManager().popBackStack();
                String classroom_token = bundle.getString("qr_text");
                openClassroomActivity(classroom_token);
            }
        });

    }

    @Override
    public void onBackPressed() {
        //Handle back requests from activity's child fragment
        if((UpdateMessageFragment !=null && UpdateMessageFragment.isVisible()) )
            finish();
        else
            super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();



        checkEverySecond();
    }

//    private void startScanner(String screen_msg){
//        ScanOptions options = new ScanOptions();
//        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
//        options.setOrientationLocked(false);
//        options.setPrompt(screen_msg);
//        options.setCameraId(0);
//        options.setBeepEnabled(false);
//        options.setBarcodeImageEnabled(true);
//        barcodeLauncher.launch(options);
//
////        IntentIntegrator integrator = new IntentIntegrator(activity);
////        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
////        //integrator.setCaptureActivity(CustomScannerActivity.class);
////
////        integrator.setPrompt(screen_msg);
////        integrator.setCameraId(0);
////        integrator.setBeepEnabled(false);
////        integrator.setBarcodeImageEnabled(true);
////        integrator.initiateScan();
////        //new IntentIntegrator((Activity)getApplicationContext()).setCaptureActivity(CustomScannerActivity.class).initiateScan();
//    }

    public void checkEverySecond(){

        //do something every 5 seconds
        final Handler ha = new Handler();

        ha.postDelayed(new Runnable() {
            @Override
            public void run() {
                isOnline();
                ha.postDelayed(this, 3000);
            }
        }, 0);
    }

    public void isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(MainMenuActivity.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        //AsyncVerifyVersion checkForUpdates = null;

        if(netInfo != null && netInfo.isConnectedOrConnecting()) {

            //IS_ONLINE = true;
            checkForUpdates = assignUpdater();
            checkForUpdates.execute();

        } else {
            if(checkForUpdates != null) {
                checkForUpdates.cancel(true);
            }
            hideButtons();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

//    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(), result -> {
//        scan_class_button.setEnabled(false);
//        //scan_class_btn.setText("Φορτωση...");
//        scan_class_button.setBackgroundColor(Color.parseColor("#D3D3D3"));
//
//        if(result != null){
//            if(result.getContents() == null){
//                //Reset to default
//                scan_class_button.setEnabled(true);
//                //scan_class_btn.setText("Σκαναρετε τμημα");
//                scan_class_button.setBackgroundColor(Color.parseColor("#4dc3ff"));
//            }else{
//                if(scan_type == 1) {
//                    final String classroom_token = result.getContents();
//                    new AsyncSearchClassroom(classroom_token, new AsyncResults() {
//                        @Override
//                        public void taskResultsObject(Object results) {
//                            String results_str = (String) results;
//                            if (results_str.length() <= 2) {
//                                Toast.makeText(context, "Δεν βρέθηκε το τμήμα.", Toast.LENGTH_SHORT).show();
//                                //Reset to default
//                                scan_class_button.setEnabled(true);
//                                //scan_class_btn.setText("Σκαναρετε τμημα");
//                                scan_class_button.setBackgroundColor(Color.parseColor("#4dc3ff"));
//                            } else {
//                                String classroom_id = "";
//                                String classroom_subject_name = "";
//                                String classroom_subject_prof = "";
//                                try {
//                                    JSONObject j_obj = new JSONObject((String) results);
//                                    JSONArray jsonArray = j_obj.getJSONArray("classroom_info");
//                                    JSONObject obj = jsonArray.getJSONObject(0);
//
//                                    classroom_id = obj.getString("classroom_id");
//                                    classroom_subject_name = obj.getString("classroom_subject_name");
//                                    classroom_subject_prof = obj.getString("classroom_subject_prof");
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
//                                SharedPreferences.Editor editor = preferences.edit();
//
//                                editor.putString("classroom_id", classroom_id);
//                                editor.putString("classroom_token", classroom_token);
//                                editor.putString("classroom_subject_name", classroom_subject_name);
//                                editor.putString("classroom_subject_prof", classroom_subject_prof);
//                                editor.putInt("classroom_timer", 10*60*1000);
//                                editor.apply();
//
//                                Intent intent = new Intent(context, ClassroomActivity.class);
//                                context.startActivity(intent);
//
//                                Activity ac = (Activity) context;
//                                ac.finish();
//                            }
//                        }
//                    }).execute();
//                }else if(scan_type == 2){
//                    final String pass_id = result.getContents();
//                    Intent intent = new Intent(context, ScanOCRActivity.class);
//                    intent.putExtra("pass_id_arg", pass_id);
//                    startActivity(intent);
//                }
//            }
//        }
//    });

    public void openClassroomActivity(String classroom_token) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("token", classroom_token);

        new AsyncHttp().get(URLs.GET_CLASSROOM, parameters, new AsyncResultsCallbackInterface() {
            @Override
            public void onSuccess(String json_string) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root_node = null;
                try {
                    root_node = mapper.readTree(json_string);
                } catch(JsonProcessingException e) {
                    Toast.makeText(context, "Δεν βρέθηκε το τμήμα.", Toast.LENGTH_SHORT).show();
                }

                JsonNode json_node = root_node.get("classroom");
                Classroom classroom = new Classroom(
                    json_node.get("id").asInt(),
                    json_node.get("creator").asInt(),
                    json_node.get("token").asText(),
                    json_node.get("subject").get("name").asText().toUpperCase(),
                    json_node.get("subject").get("professor").asText().toUpperCase(),
                    json_node.get("datetime").asText(),
                    json_node.get("type").asText());

                Gson gson = new Gson();
                String classroom_json = gson.toJson(classroom);

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("ClassroomClass", classroom_json);
                editor.apply();

                Intent intent = new Intent(context, ClassroomActivity.class);
                intent.putExtra("ClassroomClass", classroom_json);
                startActivity(intent);
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(context, "Δεν βρέθηκε το τμήμα.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String getSavedClassroom() {
        String classroom_token = preferences.getString("classroom_token", "");
        return classroom_token;
    }

    private AsyncVerifyVersion assignUpdater(){
        return new AsyncVerifyVersion(new AsyncResults() {
            @Override
            public void taskResultsObject(Object results) {
                IS_UPDATED = (boolean) results;
                if(!IS_UPDATED){

                    center_textview.setText(getString(R.string.TextView_center_older_version));
                    hideButtons();
                }else {
                    showButtons();
                    //checkEverySecond();
                }
            }
        });
    }

    public void showFooter(boolean state){
        if (state) {
            footer_layout.setVisibility(View.VISIBLE);
        }else{
            footer_layout.setVisibility(View.GONE);
        }
    }

    public void showButtons(){
        scan_class_button.setVisibility(View.VISIBLE);
        create_classroom_btn.setVisibility(View.VISIBLE);
        all_classrooms_btn.setVisibility(View.VISIBLE);
        center_textview.setVisibility(View.GONE);
    }

    public void hideButtons(){
        scan_class_button.setVisibility(View.GONE);
        create_classroom_btn.setVisibility(View.GONE);
        all_classrooms_btn.setVisibility(View.GONE);
        center_textview.setVisibility(View.VISIBLE);
    }

}