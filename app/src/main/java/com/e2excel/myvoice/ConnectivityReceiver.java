package com.e2excel.myvoice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetAddress;
import java.util.HashMap;

public class ConnectivityReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.v("internet", "Network connectivity change");
        Snackbar snackbar; //make it as global
        Toast toast = new Toast(context);
        if (intent.getExtras() != null) {
            final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

            if (ni != null && ni.isConnectedOrConnecting()) {


                toast.cancel();

             /*   Toast toast = new Toast(context);
                toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0,20);
                LayoutInflater inflater = LayoutInflater.from(context);



                View toastLayout = inflater.inflate(R.layout.custom_toast,
                        null);

                TextView view1=(TextView)toastLayout.findViewById(R.id.toast_text);
                view1.setText("Connected to Internet");

                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(toastLayout);
                toast.show();*/

                Log.v("internet", "Network " + ni.getTypeName() + " connected");
            } else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                Log.v("internet", "There's no network connectivity");

                toast = new Toast(context);
                toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 20);
                LayoutInflater inflater = LayoutInflater.from(context);


                View toastLayout = inflater.inflate(R.layout.custom_toast,
                        null);

                TextView view1 = (TextView) toastLayout.findViewById(R.id.toast_text);
                view1.setText("No Internet Connection");

                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(toastLayout);
                toast.show();
                //ConnectivityReceiver.snack(null, 0, "Network Connection failed.",context.getApplicationContext());
            }
        }
    }


}

   /* public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            //You can replace it with your name
            Log.v("internet", ipAddr.toString());
            return true;

        } catch (Exception e) {
            return false;
        }
    }*/


