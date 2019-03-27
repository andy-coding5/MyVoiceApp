package com.rohan.myvoice.GlobalValues;

import android.app.Application;
import android.provider.Settings;

public class PublicClass extends Application {
    public static String FCM_TOKEN = null;
    public static String IP = "http://192.168.0.110:8000";

    public static String q_id = null, c_logo = null, q_title = null;        //for passing data from one fragment to another

    public static String MainParentID = null;
    public static int CURRENT_FRAG = 0;



    public static String survey_id = null;
    public static String survey_text = null;
    public static String survey_logo = null;


}
