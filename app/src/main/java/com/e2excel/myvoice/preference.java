package com.e2excel.myvoice;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.preference.Preference;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.e2excel.myvoice.CustomDialogs.DeleteAccountNotificationErrorDialogFragment;
import com.e2excel.myvoice.Retrofit.ApiService;
import com.e2excel.myvoice.Retrofit.RetroClient;
import com.e2excel.myvoice.pojo.SignIn.Login;
import com.e2excel.myvoice.pojo.update_profile.UpdateProfile;

import org.json.JSONObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.e2excel.myvoice.MainActivity.Build_alert_dialog;


public class preference extends AppCompatActivity {
    private static final int NOTIFICATION_PERMISSION_CODE = 123;
    private static final int RECORD_PERMISSION_CODE = 122;
    private SharedPreferences pref, pref2;
    private SharedPreferences.Editor editor;
    ApiService api;
    String api_key;
    private ProgressDialog progressDialog;
    String is_notification_allowed = "false";

    private ImageView tick_mark;
    String i_country_code, i_state_code, i_city_name, i_zip_code, i_education_code, i_gender_code, i_dob, i_income, devide_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_screen);

        //getting coustom layout for toolbar-
        //have to change the menifest file too - change theme of this activity to cutomeTheme
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar_layout);
        View view = getSupportActionBar().getCustomView();


        ImageButton imageButton = (ImageButton) view.findViewById(R.id.action_bar_back);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        // Set up progress before call
        progressDialog = new ProgressDialog(preference.this);
        progressDialog.setMax(100);
        progressDialog.setMessage("Loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);


        pref = getSharedPreferences("MYVOICEAPP_PREF", MODE_PRIVATE);
        editor = pref.edit();
        pref2 = getSharedPreferences("FCM_PREF", Context.MODE_PRIVATE);

        api = RetroClient.getApiService();

        /**
         * Calling JSON
         */
        //   String t = Token.token_string;
        //update_token();
        api_key = getResources().getString(R.string.APIKEY);
        tick_mark = findViewById(R.id.tickmark);
        //tick_mark.setBackgroundTintList(ColorStateList.valueOf(Color.argb(255, 255, 255, 255)));
        // tick_mark.setVisibility(View.INVISIBLE);

        Intent i = getIntent();

        i_country_code = i.getStringExtra("country_code");
        i_state_code = i.getStringExtra("state_code");
        i_city_name = i.getStringExtra("city_name");
        i_zip_code = i.getStringExtra("zip_code");
        i_education_code = i.getStringExtra("education_code");
        i_gender_code = i.getStringExtra("gender_code");
        i_dob = i.getStringExtra("dob");
        i_income = i.getStringExtra("income");
        devide_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public void update_token(final View view) {
        //pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Toast.makeText(this, "email from pref: " + pref.getString("email", "not fatched from pref"), Toast.LENGTH_SHORT).show();
        ApiService api = RetroClient.getApiService();

        //if fcm token is null then do not write in shared pref!
        /*if (PublicClass.FCM_TOKEN != null) {
            editor.putString("fcm_token", PublicClass.FCM_TOKEN);
            editor.commit();
        }*/

        Call<Login> call = api.getLoginJason(pref.getString("email", null), pref.getString("password", null),
                pref2.getString("fcm_token", null), "Android", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
        Log.d("update_token", "login called");
        progressDialog.show();

        call.enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                progressDialog.dismiss();

                if (response.isSuccessful()) {
                    //editor = pref.edit();
                    editor.putString("token", response.body().getData().getToken());

                    editor.commit();
                    Log.d("update_token", "update token response success : " + response.body().getData().getToken());
                    Map<String, ?> allEntries = pref.getAll();
                    for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                        Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
                    }

                    set_my_pref(view);
                    //call_api_coutry();
                } else {
                    //but but i can access the error body here.,
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        String status = jObjError.getString("message");
                        String error_msg = jObjError.getJSONObject("data").getString("errors");
                        Build_alert_dialog(getApplicationContext(), status, error_msg);

                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                progressDialog.dismiss();
                Build_alert_dialog(preference.this, "Connection Error", "Please Check You Internet Connection");
            }
        });

    }

    public void allow_record_audio(View view) {
        if (is_recording_allowed()) {
            change_button_format();
            Toast.makeText(this, "You already have the permission of recording Audio", Toast.LENGTH_SHORT).show();
            is_notification_allowed = "true";
            return;

        }
        request_recording_permission();
    }

    private void change_button_format() {
        //chnage the button style if permisison is granted
        Button b = findViewById(R.id.button4);
        b.setBackground(getDrawable(R.drawable.edit_text_notification_allowed));
        b.setTextColor(Color.WHITE);

       /* Drawable drawable = getResources().getDrawable(R.drawable.checksymbol);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 50, 50, true));*/


        //show drawable on right side of button with this call (in your onclick method)
        //b.setCompoundDrawablesWithIntrinsicBounds(null, null, d, null);
        //b.setCompoundDrawablePadding(3);


        tick_mark.setVisibility(View.VISIBLE);
        //tick_mark.bringToFront();

        tick_mark.setImageDrawable(getResources().getDrawable(R.drawable.checksymbol));

        //tick_mark.setBackgroundTintList(ColorStateList.valueOf(Color.argb(255, 255, 255, 255)));        //white


    }

    private boolean is_recording_allowed() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);

        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void request_recording_permission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {

        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        if (requestCode == RECORD_PERMISSION_CODE) {
            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                change_button_format();
                //Displaying a toast
                Toast.makeText(this, "Permission granted. now app can record audio.", Toast.LENGTH_LONG).show();
                is_notification_allowed = "true";
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission for recording the audio", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void set_my_pref(final View view) {


        //call
        Call<UpdateProfile> call = api.setMyPrefJSON(api_key, "Token " + pref.getString("token", null),
                pref.getString("username", " "), pref.getString("lastname", " "),
                i_zip_code, i_country_code, i_state_code, i_city_name,
                i_education_code, i_gender_code, i_dob,
                "3", i_income, pref2.getString("fcm_token", null), "Android",
                devide_id, 1, "1", "Yes");

        progressDialog.show();

        //call enque
        call.enqueue(new Callback<UpdateProfile>() {
            @Override
            public void onResponse(Call<UpdateProfile> call, Response<UpdateProfile> response) {
                progressDialog.dismiss();

                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals("Success")) {
                        //Toast.makeText(preference.this, "SUCCESS>>redirecting to dashboard..", Toast.LENGTH_SHORT).show();

                        Log.d("chk", "country code: " + i_country_code);
                        Log.d("chk", "state code: " + i_state_code);
                        Log.d("chk", "city name: " + i_city_name);
                        Log.d("chk", "zip code: " + i_zip_code);
                        Log.d("chk", "education code: " + i_education_code);
                        Log.d("chk", "gender code: " + i_gender_code);
                        Log.d("chk", "dob: " + i_dob);
                        Log.d("chk", "income: " + i_income);

                        Log.d("chk", "device Id: " + devide_id);

                        //writing in database (shared pref)
                        editor.putString("country_code", i_country_code);
                        editor.putString("state_code", i_state_code);
                        editor.putString("city_name", i_city_name);
                        editor.putString("zip_code", i_zip_code);
                        editor.putString("education_code", i_education_code);
                        editor.putString("gender_code", i_gender_code);
                        editor.putString("dob", i_dob);
                        editor.putString("income", i_income);
                        editor.putString("IsComplete", "true");
                        editor.commit();

                        //redirect user to dash board
                        startActivity(new Intent(preference.this, Verification.class));
                    }
                } else {
                    //but but i can access the error body here.,
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());

                        if (jObjError.has("detail")) {
                            if (jObjError.getString("detail").equals("Invalid Token")) {
                                update_token(view);

                            } else if (jObjError.getString("detail").equals("AccountDeleted")) {
                                DeleteAccountNotificationErrorDialogFragment deleteAccountNotificationErrorDialogFragment = new DeleteAccountNotificationErrorDialogFragment();
                                deleteAccountNotificationErrorDialogFragment.show(getSupportFragmentManager(), "DeleteNotificationDialogFragment");
                            }
                        } else if (jObjError.has("message")) {
                            Build_alert_dialog(preference.this, jObjError.getString("message"));
                        }

                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<UpdateProfile> call, Throwable t) {

            }
        });


    }
}
