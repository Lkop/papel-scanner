package com.lkop.qr_scanner.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.lkop.qr_scanner.constants.URLs;
import com.lkop.qr_scanner.models.Classroom;
import com.lkop.qr_scanner.network.AsyncGetMinVersion;
import com.lkop.qr_scanner.network.AsyncHttp;
import com.lkop.qr_scanner.network.AsyncResults;
import com.lkop.qr_scanner.network.AsyncResultsCallbackInterface;
import com.lkop.qr_scanner.network.AsyncVerifyVersion;
import com.lkop.qr_scanner.receivers.ConnectionChangeTrigger;
import com.lkop.qr_scanner.ui.fragments.CreateClassroomFragment;
import com.lkop.qr_scanner.ui.fragments.LoginFragment;
import com.lkop.qr_scanner.ui.fragments.MyClassroomsFragment;
import com.lkop.qr_scanner.ui.fragments.ScannerFragment;
import com.lkop.qr_scanner.ui.fragments.UpdateMessageFragment;
import java.util.HashMap;
import java.util.Map;

public class MainMenuActivity extends AppCompatActivity {

    static boolean IS_UPDATED = true;
    public static boolean IS_ONLINE = false;
    private Context context = this;
    private SharedPreferences preferences;
    private LinearLayout scan_classroom_layout, my_classrooms_layout, create_classroom_layout;
    private ImageButton scan_classroom_imagebutton, my_classrooms_imagebutton, create_classroom_imagebutton;
    private TextView center_textview;
    private AsyncVerifyVersion checkForUpdates = null;
    private UpdateMessageFragment UpdateMessageFragment;
    private RelativeLayout footer_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

//todo
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.add(R.id.container_main_menu_activity, new RearScannerFragment());
//        transaction.addToBackStack(null);
//        transaction.commit();




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

        scan_classroom_layout = (LinearLayout) findViewById(R.id.scan_classroom_layout_mainmenu_activity);
        scan_classroom_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

        my_classrooms_layout = (LinearLayout) findViewById(R.id.my_classrooms_layout_mainmenu_activity);
        my_classrooms_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("next_fragment_listener", "my_classrooms_login_response");
                LoginFragment login_fragment = new LoginFragment();
                login_fragment.setArguments(bundle);
                openFragment(login_fragment);
            }
        });

        create_classroom_layout = (LinearLayout) findViewById(R.id.create_classroom_layout_mainmenu_activity);
        create_classroom_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("next_fragment_listener", "create_classroom_login_response");
                LoginFragment login_fragment = new LoginFragment();
                login_fragment.setArguments(bundle);
                openFragment(login_fragment);
            }
        });

        new AsyncGetMinVersion().run();


        getSupportFragmentManager().setFragmentResultListener("qr_scanner_response", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                getSupportFragmentManager().popBackStack();
                String classroom_token = bundle.getString("qr_text");
                openClassroomActivity(classroom_token);
            }
        });

        getSupportFragmentManager().setFragmentResultListener("my_classrooms_login_response", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                getSupportFragmentManager().popBackStack();
                MyClassroomsFragment fragment = new MyClassroomsFragment();
                fragment.setArguments(bundle);
                openFragment(fragment);
            }
        });

        getSupportFragmentManager().setFragmentResultListener("create_classroom_login_response", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                getSupportFragmentManager().popBackStack();
                CreateClassroomFragment fragment = new CreateClassroomFragment();
                fragment.setArguments(bundle);
                openFragment(fragment);
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
            public void onFailure() {
                Toast.makeText(context, "Δεν βρέθηκε το τμήμα.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.container_main_menu_activity, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
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
//        scan_classroom_imagebutton.setVisibility(View.VISIBLE);
//        create_classroom_imagebutton.setVisibility(View.VISIBLE);
//        all_classrooms_imagebutton.setVisibility(View.VISIBLE);
        center_textview.setVisibility(View.GONE);
    }

    public void hideButtons(){
//        scan_classroom_imagebutton.setVisibility(View.GONE);
//        create_classroom_imagebutton.setVisibility(View.GONE);
//        all_classrooms_imagebutton.setVisibility(View.GONE);
        center_textview.setVisibility(View.VISIBLE);
    }

}