package com.rohan.myvoice;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.rohan.myvoice.Retrofit.ApiService;
import com.rohan.myvoice.Retrofit.RetroClient;
import com.rohan.myvoice.pojo.SignIn.Login;
import com.rohan.myvoice.pojo.citi_details.Cities;
import com.rohan.myvoice.pojo.citi_details.CityList;
import com.rohan.myvoice.pojo.country_details.Country;
import com.rohan.myvoice.pojo.country_details.CountryList;
import com.rohan.myvoice.pojo.state_details.StateList;
import com.rohan.myvoice.pojo.state_details.States;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.rohan.myvoice.MainActivity.Build_alert_dialog;

public class personal_info_1 extends AppCompatActivity {

    private TextView textview_country_info, textview_state_info, textview_city_info, textView_zip_info;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private static String[] country_name, state_name, city_name;
    private ArrayList<String> country_name_list, state_name_list, city_name_list;
    private ListView listview_country, listview_state, listview_city;
    private Dialog dialog;


    public String selected_country, selected_state, selected_city = "", country_code, state_code, selected_zip;
    public String prev_selected_country = "not_selected", prev_selected_state = "not_selected", prev_selected_city = "not_selected";
    ApiService api;
    String api_key;
    Map<String, String> country_map, state_map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info_1);
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = pref.edit();

        textview_country_info = findViewById(R.id.country);
        textview_state_info = findViewById(R.id.state);
        textview_city_info = findViewById(R.id.city);
        textView_zip_info = findViewById(R.id.zipcode);

        country_name_list = new ArrayList<>();
        state_name_list = new ArrayList<>();
        city_name_list = new ArrayList<>();

        //call_api_coutry();

        api = RetroClient.getApiService();

        /**
         * Calling JSON
         */
        //   String t = Token.token_string;
        //Call<Country> call = api.getCountryJson("6815ab00be4c46b597b1567db6cb3def", Token.token_string);
        update_token();
        api_key = getResources().getString(R.string.APIKEY);

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
                /*progressDoalog.dismiss();*/


            }
        });


        //FOR TESTING PURPOSE -- CHANGING THE TOKEN KEY
        /*pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = pref.edit();
        editor.putString("token", "12312");
        editor.commit();*/
    }

    public void country_selection(View view) {             //dialog of country selection
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
                    fetch_states();
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

    private void fetch_states() {

        Call<States> call = api.getStateJson(api_key, "Token " + pref.getString("token", null), country_code);

        call.enqueue(new Callback<States>() {
            @Override
            public void onResponse(Call<States> call, Response<States> response) {
                if (response.isSuccessful()) {


                    List<StateList> state_obj_list = response.body().getData().getStateList();
                    //Toast.makeText(personal_info_1.this, country_string, Toast.LENGTH_LONG).show();
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


                } else {
                    //first chk for TOKEN EXPIRE??
                    update_token();
                    //calling a function


                    Toast.makeText(personal_info_1.this, "response not received", Toast.LENGTH_SHORT).show();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(getApplicationContext(), jObjError.toString(), Toast.LENGTH_LONG).show();

                        //Build_alert_dialog(getApplicationContext(), "Error", status);


                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }


                }
            }

            @Override
            public void onFailure(Call<States> call, Throwable t) {

            }
        });
    }

    public void state_selection(View view) {

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
                    fetch_cities();

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
    }

    private void fetch_cities() {

        if (state_name_list.size() != 0) {
            Call<Cities> call = api.getCityJson(api_key, "Token " + pref.getString("token", null), country_code, state_code);

            call.enqueue(new Callback<Cities>() {
                @Override
                public void onResponse(Call<Cities> call, Response<Cities> response) {
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


                    } else {
                        //first chk for TOKEN EXPIRE??
                        update_token();
                        //calling a function


                        Toast.makeText(personal_info_1.this, "response not received", Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            Toast.makeText(getApplicationContext(), jObjError.toString(), Toast.LENGTH_LONG).show();

                            //Build_alert_dialog(getApplicationContext(), "Error", status);


                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }


                    }
                }

                @Override
                public void onFailure(Call<Cities> call, Throwable t) {

                }
            });
        }
    }

    public void city_selection(View view) {
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

                        textView_zip_info.setText("Select Zip Code");

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
    }

    public void zip_selection(View view) {
        if (!selected_city.equals("")) {
            try {
                Intent i = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(this);
                startActivityForResult(i, 2);


            } catch (
                    GooglePlayServicesRepairableException e) {
                // TODO: Handle the error.
            } catch (
                    GooglePlayServicesNotAvailableException e) {
                // TODO: Handle the error.
            }

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getApplicationContext(), data);

                try {
                    getPlaceInfo(place.getLatLng().latitude, place.getLatLng().longitude);
                    textView_zip_info.setText(selected_zip);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                Log.i("TAG", "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("TAG", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

    }

    private void getPlaceInfo(double lat, double lon) {
        Geocoder mGeocoder = new Geocoder(this);
        List<Address> addresses = null;
        try {
            addresses = mGeocoder.getFromLocation(lat, lon, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses.get(0).getPostalCode() != null) {
            selected_zip = addresses.get(0).getPostalCode();
            Log.d("ZIP CODE", selected_zip);
        }

        if (addresses.get(0).getLocality() != null) {
            String city = addresses.get(0).getLocality();
            Log.d("CITY", city);
        }

        if (addresses.get(0).getAdminArea() != null) {
            String state = addresses.get(0).getAdminArea();
            Log.d("STATE", state);
        }

        if (addresses.get(0).getCountryName() != null) {
            String country = addresses.get(0).getCountryName();
            Log.d("COUNTRY", country);
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

