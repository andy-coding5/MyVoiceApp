package com.rohan.myvoice.Fragments;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rohan.myvoice.Dashboard;
import com.rohan.myvoice.GlobalValues.PublicClass;
import com.rohan.myvoice.R;
import com.rohan.myvoice.Retrofit.ApiService;
import com.rohan.myvoice.Retrofit.RetroClient;
import com.rohan.myvoice.personal_info_1;
import com.rohan.myvoice.personal_info_2;
import com.rohan.myvoice.pojo.SignIn.Login;
import com.rohan.myvoice.pojo.citi_details.Cities;
import com.rohan.myvoice.pojo.citi_details.CityList;
import com.rohan.myvoice.pojo.country_details.Country;
import com.rohan.myvoice.pojo.country_details.CountryList;
import com.rohan.myvoice.pojo.education_details.EduList;
import com.rohan.myvoice.pojo.education_details.Education;
import com.rohan.myvoice.pojo.gender_details.Gender;
import com.rohan.myvoice.pojo.gender_details.GenderList;
import com.rohan.myvoice.pojo.salary_details.Salary;
import com.rohan.myvoice.pojo.state_details.StateList;
import com.rohan.myvoice.pojo.state_details.States;
import com.rohan.myvoice.pojo.update_profile.UpdateProfile;
import com.rohan.myvoice.pojo.zip_details.Zip;
import com.rohan.myvoice.preference;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.rohan.myvoice.MainActivity.Build_alert_dialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfileFragment extends Fragment {

    View v;
    ApiService api;
    String api_key;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private ProgressDialog progressDialog;

    private Dialog dialog;

    private EditText first_name_tv, last_name_tv, email_tv, country_tv, state_tv, city_tv, zip_tv, education_tv, gender_tv, dob_tv, income_tv;

    private static String[] country_name, state_name, city_name;
    private ArrayList<String> country_name_list, state_name_list, city_name_list;
    private ListView listview_country, listview_state, listview_city;
    private Map<String, String> country_map, state_map;
    public String selected_country = "not_selected", selected_state = "not_selected", selected_city = "not_selected", selected_zip = "not_selected", country_code, state_code;
    public String prev_selected_country = "not_selected", prev_selected_state = "not_selected", prev_selected_city = "not_selected";

    private Map<String, String> gender_map, education_map, salary_map;
    private ArrayList<String> gender_name_list, education_name_list, salary_name_list;
    private static String[] gender_name, education_name, salary_name;
    private ListView listview_gender, listview_education, listview_salary;
    public String selected_gender = "not_selected", selected_education = "not_selected", selected_salary = "not_selected", selected_dob = "not_selected";
    public String gender_code, education_code;
    private String isValid = "not_initialized";

    private Button save_info_button;


    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        // Set up progress before call
        progressDialog = new ProgressDialog(this.getActivity());
        progressDialog.setMax(100);
        progressDialog.setMessage("Loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        // mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeToRefresh);
        //mSwipeRefreshLayout.setColorSchemeResources(R.color.dark_blue);

        pref = this.getActivity().getSharedPreferences("MYVOICEAPP_PREF", Context.MODE_PRIVATE);
        editor = pref.edit();

        api = RetroClient.getApiService();

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        TextView tt = toolbar.findViewById(R.id.title_text);
        tt.setText("Edit Profile");

        ImageView back = toolbar.findViewById(R.id.back_image);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.framelayout_container, new ProfileFragment()).commit();
            }
        });

        TextView edit_btn = toolbar.findViewById(R.id.logout_textview);
        edit_btn.setVisibility(View.INVISIBLE);

        /*EditText test = v.findViewById(R.id.income);
        test.setText("DEMO");*/

        first_name_tv = v.findViewById(R.id.first_name);
        last_name_tv = v.findViewById(R.id.last_name);

        country_tv = v.findViewById(R.id.country);
        state_tv = v.findViewById(R.id.state);
        city_tv = v.findViewById(R.id.city);
        zip_tv = v.findViewById(R.id.zip);
        education_tv = v.findViewById(R.id.education);
        gender_tv = v.findViewById(R.id.gender);
        dob_tv = v.findViewById(R.id.dob);
        income_tv = v.findViewById(R.id.income);

        save_info_button = v.findViewById(R.id.save_btn);

        api_key = getResources().getString(R.string.APIKEY);

        country_name_list = new ArrayList<>();
        state_name_list = new ArrayList<>();
        city_name_list = new ArrayList<>();
        gender_name_list = new ArrayList<>();
        education_name_list = new ArrayList<>();
        salary_name_list = new ArrayList<>();


        /*
        now we call the update token function but here not for
        only updating the token, also we will get all the information that
        we have to display over the profile fragment fields
        */

        update_token_edited();


        //set onclick listners on country, state, city, zip, education details, dob, gender, income

        country_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                country_selection();

            }
        });

        state_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state_selection();
            }
        });

        city_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                city_selection();
            }
        });

        education_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                education_selection();
            }
        });

        gender_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender_selection();
            }
        });

        income_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                income_selection();
            }
        });

        dob_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dob_selection();
            }
        });

        save_info_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_button();
            }
        });


    }

    private void country_selection() {
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

                        dialog = new Dialog(getActivity());
                        dialog.setContentView(R.layout.list_view);
                        dialog.setTitle("Select Country");
                        dialog.setCancelable(true);
                        dialog.setCanceledOnTouchOutside(true);
                        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;


                        //prepare a list view in dialog
                        listview_country = dialog.findViewById(R.id.dialogList);


                        //String[] aray = {"rohn0", "fdad", "aqwe"};
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item, R.id.textViewStyle, country_name);
                        listview_country.setAdapter(adapter);


                        listview_country.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                //Toast.makeText(personal_info_1.this, "Clicked Item: " + parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                                view.setSelected(true);
                                selected_country = parent.getItemAtPosition(position).toString();

                                if (!prev_selected_country.equals(selected_country)) {
                                    prev_selected_country = selected_country;
                                    state_tv.setText("Select State");

                                }
                                city_tv.setText(selected_country);

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


                    Toast.makeText(getActivity(), "response not received", Toast.LENGTH_SHORT).show();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        /* String status = jObjError.getString("detail");
                         */
                        if (jObjError.getString("detail").equals("Invalid Token")) {
                            update_token();
                            country_selection();
                        }
                        Toast.makeText(getActivity(), jObjError.toString(), Toast.LENGTH_LONG).show();

                        //Build_alert_dialog(getApplicationContext(), "Error", status);


                    } catch (Exception e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }


                }

            }

            @Override
            public void onFailure(Call<Country> call, Throwable t) {
                progressDialog.dismiss();
                Build_alert_dialog(getActivity(), "Connection Error", "Please Check You Internet Connection");
            }
        });
    }

    private void state_selection() {

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

                        dialog = new Dialog(getActivity());
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
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item, R.id.textViewStyle, state_name);
                        listview_state.setAdapter(adapter);
                        listview_state.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                //Toast.makeText(personal_info_1.this, "Clicked Item: " + parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                                selected_state = parent.getItemAtPosition(position).toString();
                                if (!prev_selected_state.equals(selected_state)) {
                                    prev_selected_state = selected_state;

                                    city_tv.setText("Select City");

                                }
                                state_tv.setText(selected_state);

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


                    Toast.makeText(getActivity(), "response not received", Toast.LENGTH_SHORT).show();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        if (jObjError.getString("detail").equals("Invalid Token")) {
                            update_token();
                            state_selection();
                        }
                        Toast.makeText(getActivity(), jObjError.toString(), Toast.LENGTH_LONG).show();

                        //Build_alert_dialog(getApplicationContext(), "Error", status);


                    } catch (Exception e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }


                }
            }

            @Override
            public void onFailure(Call<States> call, Throwable t) {

                progressDialog.dismiss();
                Build_alert_dialog(getActivity(), "Connection Error", "Please Check You Internet Connection");
            }
        });
    }

    private void city_selection() {
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

                            dialog = new Dialog(getActivity());
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
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item, R.id.textViewStyle, city_name);
                            listview_city.setAdapter(adapter);
                            listview_city.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    //Toast.makeText(personal_info_1.this, "Clicked Item: " + parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                                    selected_city = parent.getItemAtPosition(position).toString();

                                    if (!prev_selected_city.equals(selected_city)) {
                                        prev_selected_city = selected_city;

                                        zip_tv.setHint("Select Zip Code");

                                    }
                                    city_tv.setText(selected_city);
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


                        Toast.makeText(getActivity(), "response not received", Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            if (jObjError.getString("detail").equals("Invalid Token")) {
                                update_token();
                                city_selection();
                            }
                            Toast.makeText(getActivity(), jObjError.toString(), Toast.LENGTH_LONG).show();

                            //Build_alert_dialog(getApplicationContext(), "Error", status);


                        } catch (Exception e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }


                    }
                }

                @Override
                public void onFailure(Call<Cities> call, Throwable t) {
                    progressDialog.dismiss();
                    Build_alert_dialog(getActivity(), "Connection Error", "Please Check You Internet Connection");
                }
            });
        }
    }

    private void education_selection() {
        //FOR EDUCATION

        Call<Education> call2 = api.getEducationJson(api_key, "Token " + pref.getString("token", null), country_code);
        Log.d("token_detail", "used for education: " + pref.getString("token", null));
        // show it
        progressDialog.show();
        call2.enqueue(new Callback<Education>() {
            @Override
            public void onResponse(Call<Education> call, Response<Education> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    List<EduList> education_obj_list = response.body().getData().getEduList();
                    //Toast.makeText(personal_info_1.this, country_string, Toast.LENGTH_LONG).show();
                    education_map = new HashMap<>();


                    education_name_list = new ArrayList<>();

                    for (EduList g : education_obj_list) {

                        education_map.put(g.getCode(), g.getName());
                        //making an array from MAP's values
                        education_name_list.add(g.getName());


                    }
                    //printing -- LOG for testing

                    education_name = new String[education_name_list.size()];

                    for (int i = 0; i < education_name_list.size(); i++) {
                        education_name[i] = education_name_list.get(i);
                        Log.i("TAG", education_name[i]);
                    }

                    //loading the data in a view
                    if (education_name_list != null) {            //if not fatched any data than coutry filed should not be clicked; Otherwise it will be crashed.! __Rv__

                        dialog = new Dialog(getActivity());
                        dialog.setContentView(R.layout.list_view);
                        dialog.setTitle("Select Highest Qualification");
                        dialog.setCancelable(true);
                        dialog.setCanceledOnTouchOutside(true);
                        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;


                        //prepare a list view in dialog
                        listview_education = dialog.findViewById(R.id.dialogList);


                        //String[] aray = {"rohn0", "fdad", "aqwe"};
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item, R.id.textViewStyle, education_name);
                        listview_education.setAdapter(adapter);


                        listview_education.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                //Toast.makeText(personal_info_1.this, "Clicked Item: " + parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                                view.setSelected(true);
                                selected_education = parent.getItemAtPosition(position).toString();


                                education_tv.setText(selected_education);

                                dialog.dismiss();

                                //taking the value of education code
                                for (Map.Entry entry : education_map.entrySet()) {
                                    if (selected_education.equals(entry.getValue())) {
                                        education_code = entry.getKey().toString();
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


                } else {
                    //first chk for TOKEN EXPIRE??
                    //calling a function


                    Toast.makeText(getActivity(), "response not received", Toast.LENGTH_SHORT).show();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());

                        if (jObjError.getString("detail").equals("Invalid Token")) {
                            update_token();
                            education_selection();

                        }

                        /* String status = jObjError.getString("detail");
                         */
                        Toast.makeText(getActivity(), jObjError.toString(), Toast.LENGTH_LONG).show();

                        //Build_alert_dialog(getApplicationContext(), "Error", status);

                    } catch (Exception e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Education> call, Throwable t) {
                progressDialog.dismiss();
                Build_alert_dialog(getActivity(), "Connection Error", "Please Check You Internet Connection");
            }
        });
    }

    private void gender_selection() {
        //FOR GENDER
        Call<Gender> call = api.getGenderJson(api_key, "Token " + pref.getString("token", null));
        Log.d("token_detail", "used for gender: " + pref.getString("token", null));
        progressDialog.show();

        call.enqueue(new Callback<Gender>() {
            @Override
            public void onResponse(Call<Gender> call, Response<Gender> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    List<GenderList> gender_obj_list = response.body().getData().getGenderList();
                    //Toast.makeText(personal_info_1.this, country_string, Toast.LENGTH_LONG).show();
                    gender_map = new HashMap<>();


                    gender_name_list = new ArrayList<>();

                    for (GenderList g : gender_obj_list) {

                        gender_map.put(g.getCode(), g.getName());
                        //making an array from MAP's values
                        gender_name_list.add(g.getName());
                    }
                    //printing -- LOG for testing

                    gender_name = new String[gender_name_list.size()];

                    for (int i = 0; i < gender_name_list.size(); i++) {
                        gender_name[i] = gender_name_list.get(i);
                        Log.i("TAG", gender_name[i]);
                    }
                    //loading the data in a view
                    if (gender_name_list != null) {            //if not fatched any data than coutry filed should not be clicked; Otherwise it will be crashed.! __Rv__

                        dialog = new Dialog(getActivity());
                        dialog.setContentView(R.layout.list_view);
                        dialog.setTitle("Select Gender");
                        dialog.setCancelable(true);
                        dialog.setCanceledOnTouchOutside(true);
                        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;


                        //prepare a list view in dialog
                        listview_gender = dialog.findViewById(R.id.dialogList);


                        //String[] aray = {"rohn0", "fdad", "aqwe"};
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item, R.id.textViewStyle, gender_name);
                        listview_gender.setAdapter(adapter);


                        listview_gender.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                //Toast.makeText(personal_info_1.this, "Clicked Item: " + parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                                view.setSelected(true);
                                selected_gender = parent.getItemAtPosition(position).toString();


                                gender_tv.setText(selected_gender);

                                dialog.dismiss();

                                //taking the value of gender code
                                for (Map.Entry entry : gender_map.entrySet()) {
                                    if (selected_gender.equals(entry.getValue())) {
                                        gender_code = entry.getKey().toString();
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

                } else {
                    //first chk for TOKEN EXPIRE??
                    //calling a function


                    Toast.makeText(getActivity(), "response not received", Toast.LENGTH_SHORT).show();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());

                        if (jObjError.getString("detail").equals("Invalid Token")) {
                            update_token();
                            gender_selection();
                        }
                        /* String status = jObjError.getString("detail");
                         */
                        Toast.makeText(getActivity(), jObjError.toString(), Toast.LENGTH_LONG).show();

                        //Build_alert_dialog(getApplicationContext(), "Error", status);


                    } catch (Exception e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Gender> call, Throwable t) {
                progressDialog.dismiss();
                Build_alert_dialog(getActivity(), "Connection Error", "Please Check You Internet Connection");
            }
        });

    }

    private void dob_selection() {
        int mYear, mMonth, mDay;
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        dob_tv.setText((monthOfYear + 1) + "-" + dayOfMonth + "-" + year);       //for showing - format is: m-d-y

                        isValid = age_validation(dayOfMonth, monthOfYear + 1, year);
                        if (Integer.parseInt(isValid) < 13) {
                            selected_dob = "not_selected";
                            Build_alert_dialog(getActivity(), "Age restriction", "you must be 13+");
                        }
                        selected_dob = (String) (year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);        //for database_sending, format is : y-m-d

                    }
                }, mYear, mMonth, mDay);


        datePickerDialog.show();



    }

    private String age_validation(int dayOfMonth, int mon, int year) {
        Calendar today = Calendar.getInstance();
        Calendar dob = Calendar.getInstance();
        dob.set(year, mon - 1, dayOfMonth);
        Toast.makeText(getActivity(), "today's date : " + today.get(Calendar.YEAR) + "-" + today.get(Calendar.MONTH) + "-" + today.get(Calendar.DAY_OF_YEAR), Toast.LENGTH_SHORT).show();

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if ((today.get(Calendar.MONTH) < dob.get(Calendar.MONTH)) ||
                (today.get(Calendar.MONTH) == dob.get(Calendar.MONTH) && (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)))) {
            age--;
        }
        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();


        return ageS;

    }

    private void income_selection() {
        //For INCOME
        Call<Salary> call3 = api.getSalaryJson(api_key, "Token " + pref.getString("token", null));
        Log.d("token_detail", "used for salary: " + pref.getString("token", null));
        progressDialog.show();

        call3.enqueue(new Callback<Salary>() {
            @Override
            public void onResponse(Call<Salary> call, Response<Salary> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    List<String> salary_obj_list = response.body().getData().getList();
                    //Toast.makeText(personal_info_1.this, country_string, Toast.LENGTH_LONG).show();
                    salary_map = new HashMap<>();


                    salary_name_list = new ArrayList<>();

                    for (String g : salary_obj_list) {

                        //  gender_map.put(g.getCode(), g.getName());
                        //making an array from MAP's values
                        salary_name_list.add(g);
                    }
                    //printing -- LOG for testing

                    salary_name = new String[salary_name_list.size()];

                    for (int i = 0; i < salary_name_list.size(); i++) {
                        salary_name[i] = salary_name_list.get(i);
                        Log.i("TAG", salary_name[i]);
                    }
                    //loading the data in a view
                    if (salary_name_list != null) {            //if not fatched any data than coutry filed should not be clicked; Otherwise it will be crashed.! __Rv__

                        dialog = new Dialog(getActivity());
                        dialog.setContentView(R.layout.list_view);
                        dialog.setTitle("Select Annual Income");
                        dialog.setCancelable(true);
                        dialog.setCanceledOnTouchOutside(true);
                        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;


                        //prepare a list view in dialog
                        listview_salary = dialog.findViewById(R.id.dialogList);


                        //String[] aray = {"rohn0", "fdad", "aqwe"};
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item, R.id.textViewStyle, salary_name);
                        listview_salary.setAdapter(adapter);


                        listview_salary.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                //Toast.makeText(personal_info_1.this, "Clicked Item: " + parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                                view.setSelected(true);
                                selected_salary = parent.getItemAtPosition(position).toString();


                                income_tv.setText(selected_salary);

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
                    //calling a function


                    Toast.makeText(getActivity(), "response not received", Toast.LENGTH_SHORT).show();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());

                        if (jObjError.getString("detail").equals("Invalid Token")) {
                            update_token();
                            income_selection();
                        }
                        /* String status = jObjError.getString("detail");
                         */
                        Toast.makeText(getActivity(), jObjError.toString(), Toast.LENGTH_LONG).show();

                        //Build_alert_dialog(getApplicationContext(), "Error", status);


                    } catch (Exception e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Salary> call, Throwable t) {
                progressDialog.dismiss();
                Build_alert_dialog(getActivity(), "Connection Error", "Please Check You Internet Connection");
            }
        });
    }


    //called when press the save button
    public void save_button() {
        //FIRST check if ZIP code is valid or not then go for final submit info call
        if (!selected_city.equals("not_selected")) {

            //taking user input of Zip code
            final String s = zip_tv.getText().toString().trim();


            selected_zip = s;


            //NOW GO FOR FINAL SUBMIT CALL BECAUSE SELECTED_ZIP iS now compltely valid

            if (selected_gender.equals("not_selected") || selected_education.equals("not_selected") || selected_dob.equals("not_selected") || selected_salary.equals("not_selected")) {
                Build_alert_dialog(getActivity(), "Incomplete Details", "Please fill all the details");
            } else {
                String FcmToken = pref.getString("fcm_token", null);
                String device_id = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
                //ALL OK , SUBMIT THE DETAILS
                Call<UpdateProfile> call = api.getUpdateProfileJson(api_key, "Token " + pref.getString("token", null),
                        selected_zip, country_code, state_code, selected_city,
                        education_code, gender_code, selected_dob,
                        "3", selected_salary, FcmToken, "Android",
                        device_id, PublicClass.isNotificationAllowed.toString(), "true");

                progressDialog.show();

                //call enque
                call.enqueue(new Callback<UpdateProfile>() {
                    @Override
                    public void onResponse(Call<UpdateProfile> call, Response<UpdateProfile> response) {
                        progressDialog.dismiss();

                        if (response.isSuccessful() && "Success".equals(response.body().getStatus())) {


                            //writing in database (shared pref)
                            editor.putString("country_code", country_code);
                            editor.putString("state_code", state_code);
                            editor.putString("city_name", selected_city);
                            editor.putString("zip_code", selected_zip);
                            editor.putString("education_code", education_code);
                            editor.putString("gender_code", gender_code);
                            editor.putString("dob", selected_dob);
                            editor.putString("income", selected_salary);
                            editor.putString("IsComplete", "true");
                            editor.commit();
                            getFragmentManager().beginTransaction().replace(R.id.framelayout_container, new ProfileFragment()).commit();


                        } else {
                            //but but i can access the error body here.,
                            try {
                                JSONObject jObjError = new JSONObject(response.errorBody().string());

                                if (jObjError.getString("detail").equals("Invalid Token")) {
                                    update_token();
                                    save_button();
                                }
                                String status = jObjError.getString("message");
                                String error_msg = jObjError.getJSONObject("data").getString("errors");
                                Build_alert_dialog(getActivity(), status, error_msg);

                            } catch (Exception e) {
                                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UpdateProfile> call, Throwable t) {

                    }
                });


            }


        }


    }


    //update token function with some changes
    public void update_token_edited() {
        //pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //Toast.makeText(getActivity(), "email from pref: " + pref.getString("email", "not fatched from pref"), Toast.LENGTH_SHORT).show();
        ApiService api = RetroClient.getApiService();

        //if fcm token is null then do not write in shared pref!
        if (PublicClass.FCM_TOKEN != null) {
            editor.putString("fcm_token", PublicClass.FCM_TOKEN);
            editor.commit();
        }

        Call<Login> call = api.getLoginJason(pref.getString("email", null), pref.getString("password", null), pref.getString("fcm_token", null),
                "Android", Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID));

        progressDialog.show();

        call.enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                progressDialog.dismiss();

                if (response.isSuccessful()) {
                    //editor = pref.edit();
                    editor.putString("token", response.body().getData().getToken());

                    editor.commit();
                    Log.d("token", "Token " + pref.getString("token", null));

                    first_name_tv.setText(response.body().getData().getFirstName().toString().trim());
                    last_name_tv.setText(response.body().getData().getLastName().toString().trim());

                    country_tv.setText(response.body().getData().getProfile().getCountryname().toString().trim());
                    country_code = response.body().getData().getProfile().getCountry().toString().trim();
                    selected_country = response.body().getData().getProfile().getCountryname().toString().trim();

                    state_tv.setText(response.body().getData().getProfile().getStatename().toString().trim());
                    state_code = response.body().getData().getProfile().getState().toString().trim();
                    selected_state = response.body().getData().getProfile().getStatename().toString().trim();

                    city_tv.setText(response.body().getData().getProfile().getCity().toString().trim());
                    selected_city = response.body().getData().getProfile().getCity().toString().trim();

                    zip_tv.setText(response.body().getData().getProfile().getZipcode().toString().trim());
                    selected_zip = response.body().getData().getProfile().getZipcode().toString().trim();

                    education_tv.setText(response.body().getData().getProfile().getEducationname().toString().trim());
                    education_code = response.body().getData().getProfile().getEducation().toString().trim();
                    selected_education = response.body().getData().getProfile().getEducationname().toString().trim();

                    gender_tv.setText(response.body().getData().getProfile().getGendername().toString().trim());
                    gender_code = response.body().getData().getProfile().getGender().toString().trim();
                    selected_gender = response.body().getData().getProfile().getGendername().toString().trim();

                    dob_tv.setText(response.body().getData().getProfile().getDob().toString().trim());
                    selected_dob = response.body().getData().getProfile().getDob().toString().trim();


                    income_tv.setText(response.body().getData().getProfile().getIncome().toString().trim());
                    selected_salary = response.body().getData().getProfile().getIncome().toString().trim();


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
                        Build_alert_dialog(getActivity(), status, error_msg);

                    } catch (Exception e) {
                        // Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                progressDialog.dismiss();
                //Build_alert_dialog(getActivity(), "Connection Error", "Please Check You Internet Connection");
            }
        });

    }

    public void update_token() {
        //pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //Toast.makeText(getActivity(), "email from pref: " + pref.getString("email", "not fatched from pref"), Toast.LENGTH_SHORT).show();
        ApiService api = RetroClient.getApiService();

        //if fcm token is null then do not write in shared pref!
        if (PublicClass.FCM_TOKEN != null) {
            editor.putString("fcm_token", PublicClass.FCM_TOKEN);
            editor.commit();
        }

        Call<Login> call = api.getLoginJason(pref.getString("email", null), pref.getString("password", null), pref.getString("fcm_token", null),
                "Android", Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID));

        progressDialog.show();

        call.enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                progressDialog.dismiss();

                if (response.isSuccessful()) {
                    //editor = pref.edit();
                    editor.putString("token", response.body().getData().getToken());

                    editor.commit();
                    Log.d("token", "Token " + pref.getString("token", null));

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
                        Build_alert_dialog(getActivity(), status, error_msg);

                    } catch (Exception e) {
                        // Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                progressDialog.dismiss();
                //Build_alert_dialog(getActivity(), "Connection Error", "Please Check You Internet Connection");
            }
        });

    }
}
