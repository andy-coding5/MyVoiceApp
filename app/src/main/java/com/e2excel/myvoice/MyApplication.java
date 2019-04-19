package com.e2excel.myvoice;

import android.app.Application;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.util.Log;

public class MyApplication extends Application {

    private ConnectivityReceiver connectivityReceiver;

    private ConnectivityReceiver getConnectivityReceiver() {
        if (connectivityReceiver == null)
            connectivityReceiver = new ConnectivityReceiver();

        return connectivityReceiver;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerConnectivityReceiver();
    }

    // register here your filtters
    private void registerConnectivityReceiver() {
        try {
            // if (android.os.Build.VERSION.SDK_INT >= 26) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            //filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            //filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            //filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
            registerReceiver(getConnectivityReceiver(), filter);
        } catch (Exception e) {
            Log.v("internet", e.getMessage());
        }
    }

}