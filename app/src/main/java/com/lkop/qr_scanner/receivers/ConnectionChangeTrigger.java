package com.lkop.qr_scanner.receivers;

import android.app.Activity;
import android.content.Context;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.util.Log;


public class ConnectionChangeTrigger {

    private Context context;
    private Activity activity = (Activity)context;
    private ConnectivityManager connectivity_manager;

    public ConnectionChangeTrigger(Context context){
        this.context = context;
    }

    public void registerConnectivityNetworkMonitorForAPI21AndUp() {

        connectivity_manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkRequest.Builder builder = new NetworkRequest.Builder();

        connectivity_manager.registerNetworkCallback(builder.build(), new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {

                NetworkInfo netInfo = connectivity_manager.getActiveNetworkInfo();

                if(netInfo != null && netInfo.isConnected()) {

                    Log.d("aaaa", "yes");
                }

                //context.sendBroadcast(getConnectivityIntent(false));
            }
            @Override
            public void onLost(Network network) {

//                FragmentConnectionLostMessage FragmentConnectionLostMessage = new FragmentConnectionLostMessage();
////                activity.findViewById()
//
//                FragmentTransaction transaction = ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction();
//                transaction.add(R.id.container_scan_classroom, FragmentConnectionLostMessage);
//                transaction.addToBackStack(null);
//                transaction.commit();

                NetworkInfo netInfo = connectivity_manager.getActiveNetworkInfo();

                if(netInfo != null && netInfo.isConnected()) {

                    Log.d("aaaa", "no");
                }
                //context.sendBroadcast(getConnectivityIntent(true));
            }
        });

    }

//    private boolean checkNetworkState(Context context){
//
//        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//
//            if
//            Network nw = connectivityManager.getActiveNetwork() ?: return false;
//            NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false;
//            return when {
//                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true;
//                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true;
//            else -> false;
//            }
//        } else {
//            NetworkInfo nwInfo = connectivityManager.getActiveNetworkInfo()?: return false
//            return nwInfo.isConnected();
//        }
//    }


    private IntentFilter getConnectivityIntent(boolean noConnection) {
//        Intent intent = new Intent();
//
//        intent.setAction("mypackage.CONNECTIVITY_CHANGE");
//        intent.putExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, noConnection);

        IntentFilter filter = new IntentFilter();

        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        //filter.addAction("mypackage.CONNECTIVITY_CHANGE");

        //register receiver
        //context.registerReceiver(ccr, filter);

        return filter;
    }

}

