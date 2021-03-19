package com.lkop.qr_scanner.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.lkop.qr_scanner.network.AsyncGetJSONFromURL;
import com.lkop.qr_scanner.network.AsyncResults;
import com.example.lkop.qr_scanner.BuildConfig;
import com.lkop.qr_scanner.constants.DefineURLS;
import com.example.lkop.qr_scanner.R;

import org.json.JSONException;
import org.json.JSONObject;

public class ServiceUpdater extends Service {

    public static int SERVICE_UPDATER_SEMAPHORE = 1;

    private final int REPEAT_SECONDS = 15;
    private final Handler handler = new Handler();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {



        handler.post(new Runnable() {
            @Override
            public void run() {

                new AsyncGetJSONFromURL(DefineURLS.GET_VERSION, null).run(new AsyncResults() {
                    @Override
                    public void taskResultsObject(Object results) {
                        String r = (String)results;

                        try {
                            JSONObject obj = new JSONObject(r);

                            int min_version = obj.getInt("min_version");

                            if(BuildConfig.VERSION_CODE < min_version){

                                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),"YOUR_CHANNEL_ID")
                                        .setSmallIcon(R.drawable.people_64)
                                        .setContentTitle("My notification")
                                        .setContentText("Much longer text that cannot fit one line...")
                                        .setStyle(new NotificationCompat.BigTextStyle().bigText("Much longer text that cannot fit one line..."))
                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                        .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND | Notification.FLAG_SHOW_LIGHTS)
                                        .setLights(Color.WHITE, 300, 100);

                                //Intent notificationIntent = new Intent(this, ActivityMainMenu.class);
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://papel.zabenia.com"));
                                PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, browserIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                builder.setContentIntent(contentIntent);


                                // Add as notification
                                NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                                manager.notify(0, builder.build());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                handler.postDelayed(this, REPEAT_SECONDS*1000);
            }
        });
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "Service created!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show();
        SERVICE_UPDATER_SEMAPHORE = 0;
    }

    @Override
    public void onStart(Intent intent, int startid) {
        Toast.makeText(this, "Service started by user.", Toast.LENGTH_LONG).show();
    }
}
