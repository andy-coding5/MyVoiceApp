package com.rohan.myvoice;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rohan.myvoice.Retrofit.ApiService;
import com.rohan.myvoice.Retrofit.RetroClient;
import com.rohan.myvoice.pojo.SignIn.Login;
import com.rohan.myvoice.pojo.country_details.Country;
import com.rohan.myvoice.pojo.country_details.CountryList;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.rohan.myvoice.MainActivity.Build_alert_dialog;

public class personal_info_1 extends AppCompatActivity {

    private TextView textview_country_info, textview_state_info;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private static String[] country_name;
    private ArrayList<String> country_name_list, state_name_list;
    private ListView listview_country, listview_state;
    private Dialog dialog;
    private Typeface mTypeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info_1);
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = pref.edit();

        textview_country_info = findViewById(R.id.country);
        textview_state_info = findViewById(R.id.state);

        country_name_list = new ArrayList<>();
        state_name_list = new ArrayList<>();

        //call_api_coutry();

        ApiService api = RetroClient.getApiService();

        /**
         * Calling JSON
         */
        //   String t = Token.token_string;
        //Call<Country> call = api.getCountryJson("6815ab00be4c46b597b1567db6cb3def", Token.token_string);
        String api_key = getResources().getString(R.string.APIKEY);

        //CALL
        Call<Country> call = api.getCountryJson(api_key, "Token " + pref.getString("token", null));
        final ProgressDialog progressDoalog;
         progressDoalog = new ProgressDialog(personal_info_1.this);

         progressDoalog.setMessage("Its loading...");
         progressDoalog.setTitle("Fetching the response");
         progressDoalog.setCancelable(true);
         progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
         // show it
         progressDoalog.show();



        //Toast.makeText(this, "Token " + pref.getString("token", null), Toast.LENGTH_LONG).show();
       /* try {
            Response<Country> response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        //WHEN WE RECEIVE RESPONSE

        call.enqueue(new Callback<Country>() {
            @Override
            public void onResponse(Call<Country> call, Response<Country> response) {
                  progressDoalog.dismiss();
                if (response.isSuccessful()) {


                    List<CountryList> country_obj_list = response.body().getData().getCountryList();
                    //Toast.makeText(personal_info_1.this, country_string, Toast.LENGTH_LONG).show();
                    Map<String, String> country_map = new HashMap<>();


                    country_name_list = new ArrayList<>();

                    for (CountryList c : country_obj_list) {
                        country_map.put(c.getCode(), c.getName());

                        //making an array from MAP's values
                        country_name_list.add(c.getName());


                    }
                    //printing -- LOG for testing

                    country_name = new String[country_name_list.size()];

                    for (int i = 0; i < country_name_list.size(); i++) {
                        country_name[i] = country_name_list.get(i);
                        Log.i("TAG", country_name[i]);
                    }


                    //String[] c = {"orhan", "vachhani", "sdadas", "asd"};


                } else {
                    //first chk for TOKEN EXPIRE??
                    //calling a function
                    update_token();


                    Toast.makeText(personal_info_1.this, "response not received", Toast.LENGTH_SHORT).show();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        /* String status = jObjError.getString("detail");
                         */
                        Toast.makeText(getApplicationContext(), jObjError.toString(), Toast.LENGTH_LONG).show();

                        //Build_alert_dialog(getApplicationContext(), "Error", status);


                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }


                }

            }

            @Override
            public void onFailure(Call<Country> call, Throwable t) {

            }
        });


        //FOR TESTING PURPOSE -- CHANGING THE TOKEN KEY
        /*pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = pref.edit();
        editor.putString("token", "12312");
        editor.commit();*/
    }

    public void showDialogListView(View view) {


        dialog = new Dialog(personal_info_1.this);
        dialog.setContentView(R.layout.list_view);
        dialog.setTitle("Select Country");
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;


        //prepare a list view in dialog
        listview_country = dialog.findViewById(R.id.dialogList);


        //String[] aray = {"rohn0", "fdad", "aqwe"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_item, R.id.textViewStyle, country_name);
        listview_country.setAdapter(adapter);
        listview_country.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(personal_info_1.this, "Clicked Item: " + parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                textview_country_info.setText(parent.getItemAtPosition(position).toString());
                dialog.dismiss();
            }
        });
        View view1 = dialog.findViewById(R.id.cancel_btn);
        Button cancel_btn = view1.findViewById(R.id.cancel_btn);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();
    }

    //FOR CALLING API WHENEVER REQUIRED


    public void update_token() {
        //pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Toast.makeText(this, "email from pref: " + pref.getString("email", "not fatched from pref"), Toast.LENGTH_SHORT).show();
        ApiService api = RetroClient.getApiService();

        Call<Login> call = api.getLoginJason(pref.getString("email", null), pref.getString("password", null));

        call.enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if (response.isSuccessful()) {
                    //editor = pref.edit();
                    editor.putString("token", response.body().getData().getToken());
                    editor.commit();

                    Map<String, ?> allEntries = pref.getAll();
                    for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                        Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
                    }

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

            }
        });


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();       //to completely close the entire application
    }
}
