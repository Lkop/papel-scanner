package com.lkop.qr_scanner.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.lkop.qr_scanner.R;
import com.lkop.qr_scanner.services.UpdaterService;

import static com.lkop.qr_scanner.services.UpdaterService.SERVICE_UPDATER_SEMAPHORE;

public class UpdateMessageFragment extends Fragment {

    private TextView update_message_textview, update_url;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

//        new AsyncGetJSONFromURL(DefineURLS.GET_VERSION, null).run(new AsyncResults() {
//            @Override
//            public void taskResultsObject(Object results) {
//
//
//            }
//        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_update_message, container, false);

        update_message_textview = (TextView) view.findViewById(R.id.update_message);
        update_url = (TextView) view.findViewById(R.id.update_url);


//            BroadcastReceiver jjj = new BroadcastReceiver(){
//            @Override
//            public void onReceive(Context context, Intent intent) {
//
//                if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
//                    Log.d("NetworkCheckReceiver", "NetworkCheckReceiver invoked...");
//
//                    boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
//
//                    if (!noConnectivity) {
//                        Log.d("NetworkCheckReceiver", "connected");
//                    }
//                    else
//                    {
//                        Log.d("NetworkCheckReceiver", "disconnected");
//                    }
//                }
//            }
//        };


        update_url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update_url.setText("jjjjjjjjjjjjj");


                if(SERVICE_UPDATER_SEMAPHORE==0)
                    getActivity().startService(new Intent(getContext(), UpdaterService.class));

                //getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return view;
    }

    public static class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            boolean isConnected = wifi != null && wifi.isConnectedOrConnecting() ||
                    mobile != null && mobile.isConnectedOrConnecting();
            if (isConnected) {
                Log.d("Network Available ", "YES");
            } else {
                Log.d("Network Available ", "NO");
            }
        }
    }

}
