package com.rohan.myvoice.GlobalValues;

import android.app.Application;
import android.provider.Settings;

public class PublicClass extends Application {
    public static String FCM_TOKEN = null;
    public static String IP = "http://192.168.1.5:8000";

    public static String q_id = null, c_logo = null, q_title = null;        //for passing data from one fragment to another


}
