package com.rohan.myvoice;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rohan.myvoice.Retrofit.ApiService;
import com.rohan.myvoice.Retrofit.RetroClient;
import com.rohan.myvoice.pojo.SignIn.Login;
import com.rohan.myvoice.pojo.citi_details.Cities;
import com.rohan.myvoice.pojo.citi_details.CityList;
import com.rohan.myvoice.pojo.country_details.Country;
import com.rohan.myvoice.pojo.country_details.CountryList;
import com.rohan.myvoice.pojo.state_details.StateList;
import com.rohan.myvoice.pojo.state_details.States;
import com.rohan.myvoice.pojo.zip_details.Zip;

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

    private TextView textview_country_info, textview_state_info, textview_city_info;
    private EditText edittext_zip_info;
    private SharedPreferences pref, pref2;
    private SharedPreferences.Editor editor;
    private static String[] country_name, state_name, city_name;
    private ArrayList<String> country_name_list, state_name_list, city_name_list;
    private ListView listview_country, listview_state, listview_city;
    private Dialog dialog;


    public String selected_country = "not_selected", selected_state = "not_selected", selected_city = "not_selected", selected_zip = "not_selected", country_code, state_code;
    public String prev_selected_country = "not_selected", prev_selected_state = "not_selected", prev_selected_city = "not_selected";
    ApiService api;
    String api_key;
    Map<String, String> country_map, state_map;
    private String city_for_validation;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info_1);
        //getting coustom layout for toolbar-
        //have to change the menifest file too - change theme of this activity to cutomeTheme
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.dots_custom_action_bar);
        View view = getSupportActionBar().getCustomView();


        ImageButton imageButton = (ImageButton) view.findViewById(R.id.action_bar_back);
        imageButton.setVisibility(View.INVISIBLE);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        pref = getSharedPreferences("MYVOICEAPP_PREF", MODE_PRIVATE);
        editor = pref.edit();
        pref2 = getSharedPreferences("FCM_PREF", Context.MODE_PRIVATE);

        textview_country_info = findViewById(R.id.country);
        textview_state_info = findViewById(R.id.state);
        textview_city_info = findViewById(R.id.city);
        edittext_zip_info = findViewById(R.id.zipcode);

        country_name_list = new ArrayList<>();
        state_name_list = new ArrayList<>();
        city_name_list = new ArrayList<>();

        // Set up progress before call
        progressDialog = new ProgressDialog(personal_info_1.this);
        progressDialog.setMax(100);
        progressDialog.setMessage("Fetching Data");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);


        //call_api_coutry();

        api = RetroClient.getApiService();

        /**
         * Calling JSON
         */
        //   String t = Token.token_string;
        //update_token();
        api_key = getResources().getString(R.string.APIKEY);


        //FOR TESTING PURPOSE -- CHANGING THE TOKEN KEY
        /*pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = pref.edit();
        editor.putString("token", "12312");
        editor.commit();*/
    }

    public void update_token() {
        //pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Toast.makeText(this, "email from pref: " + pref.getString("email", "not fatched from pref"), Toast.LENGTH_SHORT).show();
        ApiService api = RetroClient.getApiService();

        //if fcm token is null then do not write in shared pref!
      /*  if (PublicClass.FCM_TOKEN != null) {
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
                Build_alert_dialog(personal_info_1.this, "Connection Error", "Please Check You Internet Connection");
            }
        });


    }

    public void country_selection(final View view) {             //dialog of country selection
        //CALL

        Call<Country> call = api.getCountryJson(api_key, "Token " + pref.getString("token", null));
        Log.d("token_detail", "used for country: " + pref.getString("token", null));
        // show it
        progressDialog.show();


        call.enqueue(new Callback<Country>() {


            @Override
            public void onResponse(Call<Country> call, Response<Country> response) {

                progressDialog.dismiss();
                if (response.isSuccessful()) {

                    List<CountryList> country_obj_list = response.body().getData().getCountryList();
                    //Toast.makeText(personal_info_1.this, country_string, Toast.LENGTH_LONG).show();

                    Log.d("country", response.toString());
                    country_map = new HashMap<>();


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


                    if (country_name_list.size() != 0) {            //if not fatched any data than coutry filed should not be clicked; Otherwise it will be crashed.! __Rv__

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
                                view.setSelected(true);
                                selected_country = parent.getItemAtPosition(position).toString();

                                if (!prev_selected_country.equals(selected_country)) {
                                    prev_selected_country = selected_country;
                                    textview_state_info.setText("Select State");

                                }
                                textview_country_info.setText(selected_country);

                                dialog.dismiss();

                                //remove these if not works
                                for (Map.Entry entry : country_map.entrySet()) {
                                    if (selected_country.equals(entry.getValue())) {
                                        country_code = entry.getKey().toString();
                                        break;
                                    }
                                }

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
                    //String[] c = {"orhan", "vachhani", "sdadas", "asd"};


                } else {
                    //first chk for TOKEN EXPIRE??
                    //calling a function


                    Toast.makeText(personal_info_1.this, "response not received", Toast.LENGTH_SHORT).show();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        /* String status = jObjError.getString("detail");
                         */
                        if (jObjError.getString("detail").equals("Invalid Token")) {
                            update_token();
                            country_selection(view);
                        }
                        Toast.makeText(getApplicationContext(), jObjError.toString(), Toast.LENGTH_LONG).show();

                        //Build_alert_dialog(getApplicationContext(), "Error", status);


                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }


                }

            }

            @Override
            public void onFailure(Call<Country> call, Throwable t) {
                progressDialog.dismiss();
                Build_alert_dialog(personal_info_1.this, "Connection Error", "Please Check You Internet Connection");
            }
        });


    }

    private void fetch_states() {

        Call<States> call = api.getStateJson(api_key, "Token " + pref.getString("token", null), country_code);
        Log.d("token_detail", "used for state: " + pref.getString("token", null));
        // show it
        progressDialog.show();

        call.enqueue(new Callback<States>() {
            @Override
            public void onResponse(Call<States> call, Response<States> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {


                    List<StateList> state_obj_list = response.body().getData().getStateList();
                    //Toast.makeText(personal_info_1.this, country_string, Toast.LENGTH_LONG).show();
                    Log.d("state", response.body().getData().getStateList().toString());
                    state_map = new HashMap<>();


                    state_name_list = new ArrayList<>();

                    for (StateList c : state_obj_list) {
                        state_map.put(c.getCode(), c.getName());

                        //making an array from MAP's values
                        state_name_list.add(c.getName());


                    }
                    //printing -- LOG for testing

                    state_name = new String[state_name_list.size()];

                    for (int i = 0; i < state_name_list.size(); i++) {
                        state_name[i] = state_name_list.get(i);
                        //Log.i("TAG", state_name[i]);
                    }

                    if (state_name_list.size() != 0) {            //if not fetched any data than state filed should not be clicked; Otherwise it will be crashed.! __Rv__

                        dialog = new Dialog(personal_info_1.this);
                        dialog.setContentView(R.layout.list_view);
                        dialog.setTitle("Select State");
                        dialog.setCancelable(true);
                        dialog.setCanceledOnTouchOutside(true);
                        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

                        //prepare a list view in dialog
                        listview_state = dialog.findViewById(R.id.dialogList);
                        TextView t = dialog.findViewById(R.id.title_textView);
                        t.setText("Please select State");


                        //String[] aray = {"rohn0", "fdad", "aqwe"};
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_item, R.id.textViewStyle, state_name);
                        listview_state.setAdapter(adapter);
                        listview_state.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                //Toast.makeText(personal_info_1.this, "Clicked Item: " + parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                                selected_state = parent.getItemAtPosition(position).toString();
                                if (!prev_selected_state.equals(selected_state)) {
                                    prev_selected_state = selected_state;

                                    textview_city_info.setText("Select City");
                                }
                                textview_state_info.setText(selected_state);

                                dialog.dismiss();

                                //remove these if not works
                                for (Map.Entry entry : state_map.entrySet()) {
                                    if (selected_state.equals(entry.getValue())) {
                                        state_code = entry.getKey().toString();
                                        break;
                                    }
                                }
                                //  fetch_cities();
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


                } else {
                    //first chk for TOKEN EXPIRE??

                    //calling a function


                    Toast.makeText(personal_info_1.this, "response not received", Toast.LENGTH_SHORT).show();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        if (jObjError.getString("detail").equals("Invalid Token")) {
                            update_token();
                            fetch_states();
                        }
                        Toast.makeText(getApplicationContext(), jObjError.toString(), Toast.LENGTH_LONG).show();
                        //Build_alert_dialog(getApplicationContext(), "Error", status);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<States> call, Throwable t) {

                progressDialog.dismiss();
                Build_alert_dialog(personal_info_1.this, "Connection Error", "Please Check You Internet Connection");
            }
        });
    }

    public void state_selection(View view) {
        fetch_states();
       /* if (state_name_list.size() != 0) {            //if not fetched any data than state filed should not be clicked; Otherwise it will be crashed.! __Rv__

            dialog = new Dialog(personal_info_1.this);
            dialog.setContentView(R.layout.list_view);
            dialog.setTitle("Select State");
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;


            //prepare a list view in dialog
            listview_state = dialog.findViewById(R.id.dialogList);
            TextView t = dialog.findViewById(R.id.title_textView);
            t.setText("Please select State");


            //String[] aray = {"rohn0", "fdad", "aqwe"};
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_item, R.id.textViewStyle, state_name);
            listview_state.setAdapter(adapter);
            listview_state.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //Toast.makeText(personal_info_1.this, "Clicked Item: " + parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                    selected_state = parent.getItemAtPosition(position).toString();
                    if (!prev_selected_state.equals(selected_state)) {
                        prev_selected_state = selected_state;

                        textview_city_info.setText("Select City");

                    }
                    textview_state_info.setText(selected_state);

                    dialog.dismiss();

                    //remove these if not works
                    for (Map.Entry entry : state_map.entrySet()) {
                        if (selected_state.equals(entry.getValue())) {
                            state_code = entry.getKey().toString();
                            break;
                        }
                    }
                  //  fetch_cities();

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
        }*/
    }

    private void fetch_cities() {

        if (state_name_list.size() != 0) {
            Call<Cities> call = api.getCityJson(api_key, "Token " + pref.getString("token", null), country_code, state_code);
            Log.d("token_detail", "used for cities: " + pref.getString("token", null));
            progressDialog.show();

            call.enqueue(new Callback<Cities>() {
                @Override
                public void onResponse(Call<Cities> call, Response<Cities> response) {
                    progressDialog.dismiss();
                    if (response.isSuccessful()) {


                        List<CityList> city_obj_list = response.body().getData().getCityList();

                        city_name_list = new ArrayList<>();

                        for (CityList c : city_obj_list) {
                            // city_map.put(null, c.getName());

                            //making an array from MAP's values
                            city_name_list.add(c.getName());
                        }
                        //printing -- LOG for testing

                        city_name = new String[city_name_list.size()];

                        for (int i = 0; i < city_name_list.size(); i++) {
                            city_name[i] = city_name_list.get(i);
                            //Log.i("TAG", state_name[i]);
                        }

                        if (city_name_list.size() != 0) {            //if not fetched any data than state filed should not be clicked; Otherwise it will be crashed.! __Rv__

                            dialog = new Dialog(personal_info_1.this);
                            dialog.setContentView(R.layout.list_view);
                            dialog.setTitle("Select City");
                            dialog.setCancelable(true);
                            dialog.setCanceledOnTouchOutside(true);
                            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;


                            //prepare a list view in dialog
                            listview_city = dialog.findViewById(R.id.dialogList);
                            TextView t = dialog.findViewById(R.id.title_textView);
                            t.setText("Please select City");


                            //String[] aray = {"rohn0", "fdad", "aqwe"};
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_item, R.id.textViewStyle, city_name);
                            listview_city.setAdapter(adapter);
                            listview_city.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    //Toast.makeText(personal_info_1.this, "Clicked Item: " + parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                                    selected_city = parent.getItemAtPosition(position).toString();

                                    if (!prev_selected_city.equals(selected_city)) {
                                        prev_selected_city = selected_city;

                                        edittext_zip_info.setHint("Select Zip Code");

                                    }
                                    textview_city_info.setText(selected_city);
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


                    } else {
                        //first chk for TOKEN EXPIRE??
                        //update_token();
                        //calling a function


                        Toast.makeText(personal_info_1.this, "response not received", Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            if (jObjError.getString("detail").equals("Invalid Token")) {
                                update_token();
                                fetch_cities();
                            }
                            Toast.makeText(getApplicationContext(), jObjError.toString(), Toast.LENGTH_LONG).show();

                            //Build_alert_dialog(getApplicationContext(), "Error", status);


                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }


                    }
                }

                @Override
                public void onFailure(Call<Cities> call, Throwable t) {
                    progressDialog.dismiss();
                    Build_alert_dialog(personal_info_1.this, "Connection Error", "Please Check You Internet Connection");
                }
            });
        }
    }

    public void city_selection(View view) {
        fetch_cities();
        /*if (city_name_list.size() != 0) {            //if not fetched any data than state filed should not be clicked; Otherwise it will be crashed.! __Rv__

            dialog = new Dialog(personal_info_1.this);
            dialog.setContentView(R.layout.list_view);
            dialog.setTitle("Select City");
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;


            //prepare a list view in dialog
            listview_city = dialog.findViewById(R.id.dialogList);
            TextView t = dialog.findViewById(R.id.title_textView);
            t.setText("Please select City");


            //String[] aray = {"rohn0", "fdad", "aqwe"};
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_item, R.id.textViewStyle, city_name);
            listview_city.setAdapter(adapter);
            listview_city.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //Toast.makeText(personal_info_1.this, "Clicked Item: " + parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                    selected_city = parent.getItemAtPosition(position).toString();

                    if (!prev_selected_city.equals(selected_city)) {
                        prev_selected_city = selected_city;

                        edittext_zip_info.setHint("Select Zip Code");

                    }
                    textview_city_info.setText(selected_city);
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
        }*/
    }


    public void next_activity(final View view) {
        //put validation of all fields before submitting
        //send answers in intent to second activity
        if (!selected_city.equals("not_selected")) {

            //taking user input of Zip code
            final String s = edittext_zip_info.getText().toString();

            //call API for validation

            Call<Zip> c = api.getZipJason(api_key, "Token " + pref.getString("token", null), country_code, state_code, selected_city, s);
            Log.d("token_detail", "used for next button in pref 1: " + pref.getString("token", null));
            progressDialog.show();


            c.enqueue(new Callback<Zip>() {
                @Override
                public void onResponse(Call<Zip> call, Response<Zip> response) {
                    progressDialog.dismiss();
                    if (response.isSuccessful()) {

                        if (response.body().getStatus().equals("Success")) {      //if zip is valid

                            selected_zip = s;
                            if ((selected_country.equals("Select Country") ||
                                    selected_state.equals("Select State") ||
                                    selected_city.equals("Select City") ||
                                    selected_zip.equals("Select Zip Code")) ||
                                    selected_zip.equals("") ||
                                    selected_country.equals("not_selected") ||
                                    selected_state.equals("not_selected") ||
                                    selected_city.equals("not_selected") ||
                                    selected_zip.equals("not_selected")) {
                                Build_alert_dialog(personal_info_1.this, "Incomplete Information", "Please Fill the details in all the fields");

                            } else {
                                Intent intent = new Intent(personal_info_1.this, personal_info_2.class);
                                intent.putExtra("country_name", selected_country);
                                intent.putExtra("state_code", state_code);
                                intent.putExtra("city_name", selected_city);
                                intent.putExtra("zip_code", selected_zip);
                                intent.putExtra("country_code", country_code);

                                startActivity(intent);
                            }

                        } else {                       //if zip is not valid
                            Build_alert_dialog(personal_info_1.this, "Invalid Information", "Please Enter a valid Zip Code!");
                        }


                    } else {
                        //first chk for TOKEN EXPIRE??
                        //calling a function
                        Toast.makeText(personal_info_1.this, "response not received", Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            if (jObjError.getString("detail").equals("Invalid Token")) {
                                update_token();
                                next_activity(view);
                            }

                            Toast.makeText(getApplicationContext(), jObjError.toString(), Toast.LENGTH_LONG).show();
                            //Build_alert_dialog(getApplicationContext(), "Error", status);

                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Zip> call, Throwable t) {
                    progressDialog.dismiss();
                }
            });

        } else {
            Build_alert_dialog(personal_info_1.this, "Incomplete Information", "Please Select The City First!");
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();       //to completely close the entire application
    }

    @Override
    protected void onResume() {
        super.onResume();
        //   startActivity(getIntent());
        //    finish();
    }

}
