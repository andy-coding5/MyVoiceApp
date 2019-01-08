package com.rohan.myvoice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.rohan.myvoice.pojo.SignIn.Login;
import com.rohan.myvoice.pojo.country_details.Country;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.rohan.myvoice.MainActivity.Build_alert_dialog;

public class personal_info_1 extends AppCompatActivity {

    private Spinner country_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info_1);


        country_info = findViewById(R.id.country);

        String[] country = {"India", "USA", "China", "Japan", "Other"};
        String[] states = {"sadasd", "UasdsadSA", "asdsadChina", "Jafdspan", "Otsdfsdher"};


        Spinner spinner_country = findViewById(R.id.country);
        Spinner spinner_state = findViewById(R.id.state);
        ArrayAdapter countryArrayAdapter = new ArrayAdapter(this, R.layout.spinner_dropdown_layout, country);
        ArrayAdapter stateArrayAdapter = new ArrayAdapter(this, R.layout.spinner_dropdown_layout, states);

        countryArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        stateArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);

        spinner_country.setAdapter(countryArrayAdapter);
        spinner_state.setAdapter(stateArrayAdapter);


        //fetching the country from DB
        //Creating an object of our api interface
        ApiService api = RetroClient.getApiService();

        /**
         * Calling JSON
         */
        String t = Token.token_string;
        //Call<Country> call = api.getCountryJson("6815ab00be4c46b597b1567db6cb3def", Token.token_string);
        String api_key =  getResources().getString(R.string.APIKEY);;
        Call<Country> call = api.getCountryJson(api_key, "Token "+t);
        Toast.makeText(this, "Token " + Token.token_string, Toast.LENGTH_LONG).show();

        call.enqueue(new Callback<Country>() {
            @Override
            public void onResponse(Call<Country> call, Response<Country> response) {
                if (response.isSuccessful()) {


                    String country_string = response.body().getData().getCountryList().toString();
                    Toast.makeText(personal_info_1.this, country_string, Toast.LENGTH_LONG).show();


                } else {


                    Toast.makeText(personal_info_1.this, "responce not received", Toast.LENGTH_SHORT).show();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                       /* String status = jObjError.getString("detail");
                            */
                        Toast.makeText(getApplicationContext(),jObjError.toString(), Toast.LENGTH_LONG).show();

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


    }

    public void next_page(View view) {

    }
}
