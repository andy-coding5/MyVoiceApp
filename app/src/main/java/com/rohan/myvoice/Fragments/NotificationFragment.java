package com.rohan.myvoice.Fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rohan.myvoice.GlobalValues.PublicClass;
import com.rohan.myvoice.R;
import com.rohan.myvoice.Retrofit.ApiService;
import com.rohan.myvoice.Retrofit.RetroClient;
import com.rohan.myvoice.pojo.Notification_details.Datum;
import com.rohan.myvoice.pojo.Notification_details.Notification;
import com.rohan.myvoice.pojo.Notification_details.Notifications;
import com.rohan.myvoice.pojo.SignIn.Login;
import com.rohan.myvoice.pojo.invitation_details.Invite;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.rohan.myvoice.MainActivity.Build_alert_dialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {
    View v;
    ApiService api;
    String api_key;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private ProgressDialog progressDialog;
    private ArrayList<Datum> invitations_list;
    private ArrayList<Notification> notification_list;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private TextView see_all_invitation_text_view;

    private TextView noNotification, noInvitation;
    private ListView invitation_list_view, notification_list_view;

    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        v = inflater.inflate(R.layout.fragment_notifications, container, false);

        // Set up progress before call
        progressDialog = new ProgressDialog(this.getActivity());
        progressDialog.setMax(100);
        progressDialog.setMessage("Loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        // mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeToRefresh);
//        mSwipeRefreshLayout.setColorSchemeResources(R.color.dark_blue);

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
        tt.setText("Notifications");

        ImageView back = toolbar.findViewById(R.id.back_image);
        back.setVisibility(View.INVISIBLE);

        TextView logout_btn = toolbar.findViewById(R.id.logout_textview);
        logout_btn.setVisibility(View.INVISIBLE);

        //select the home button (so color is now blue) and the
       /* BottomNavigationView bv = getActivity().findViewById(R.id.bottom_navigation);
        //bv.getMenu().getItem(0).setChecked(true);
        bv.setSelectedItemId(R.id.notifications_menu_item);*/

        BottomNavigationView mBottomNavigationView=(BottomNavigationView)getActivity().findViewById(R.id.bottom_navigation);
        mBottomNavigationView.getMenu().findItem(R.id.notifications_menu_item).setChecked(true);

        invitation_list_view = v.findViewById(R.id.listview);
        notification_list_view = v.findViewById(R.id.notification_list);
        mSwipeRefreshLayout = v.findViewById(R.id.swipeToRefresh);
        invitations_list = new ArrayList<>();
        notification_list = new ArrayList<>();

        see_all_invitation_text_view = v.findViewById(R.id.see_all_invitations);

        noInvitation = v.findViewById(R.id.noInvitations);
        noNotification = v.findViewById(R.id.noNotification);

        noInvitation.setVisibility(View.INVISIBLE);
        noNotification.setVisibility(View.INVISIBLE);

        api_key = getResources().getString(R.string.APIKEY);

        load_data();

        //text view  "see_all_invitations" on click
        see_all_invitation_text_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment f = new InvitaionList();

                //Bundle b = new Bundle();

                // b.putSerializable("invitation_list", (Serializable) invitations_list);
                // f.setArguments(b);
                getFragmentManager().beginTransaction().replace(R.id.framelayout_container, f).commit();

            }
        });

        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.dark_blue));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                load_data();
            }
        });


    }

    private void load_data() {
        Call<Notifications> call = api.getnotificationssJson(api_key, "Token " + pref.getString("token", null));

        if(!((Activity) getActivity()).isFinishing())
        {
            //show dialog
            progressDialog.show();
        }

        call.enqueue(new Callback<Notifications>() {
            @Override
            public void onResponse(Call<Notifications> call, Response<Notifications> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && "Success".equals(response.body().getStatus())) {

                    //setting the invitation list
                    mSwipeRefreshLayout.setRefreshing(false);
                    invitations_list = (ArrayList<Datum>) response.body().getData();
                    if (invitations_list == null || invitations_list.size() == 0) {
                        noInvitation.setVisibility(View.VISIBLE);
                    } else {
                        ListViewAdapter arrayAdapter = new ListViewAdapter(getActivity(), invitations_list);
                        invitation_list_view.setAdapter(arrayAdapter);
                    }

                    //setting the notification list
                    notification_list = (ArrayList<Notification>) response.body().getNotification();
                    if (notification_list == null || notification_list.size() == 0) {
                        noNotification.setVisibility(View.VISIBLE);
                    } else {
                        ListViewAdaper_NotificationList listViewAdapter = new ListViewAdaper_NotificationList(getActivity(), notification_list);
                        notification_list_view.setAdapter(listViewAdapter);
                    }

                } else {
                    progressDialog.dismiss();
                    mSwipeRefreshLayout.setRefreshing(false);
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        /* String status = jObjError.getString("detail");
                         */
                        if (jObjError.getString("detail").equals("Invalid Token")) {
                            update_token_load_data();

                        } else {
                            noInvitation.setVisibility(View.VISIBLE);
                            noNotification.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        //Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Notifications> call, Throwable t) {
                progressDialog.dismiss();
                mSwipeRefreshLayout.setRefreshing(false);
                noInvitation.setVisibility(View.VISIBLE);
                noNotification.setVisibility(View.VISIBLE);
            }
        });
    }

    private void update_token_load_data() {
        ApiService api = RetroClient.getApiService();

        //if fcm token is null then do not write in shared pref!
        if (PublicClass.FCM_TOKEN != null) {
            editor.putString("fcm_token", PublicClass.FCM_TOKEN);
            editor.commit();
        }

        Call<Login> call = api.getLoginJason(pref.getString("email", null), pref.getString("password", null), pref.getString("fcm_token", null),
                "Android", Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID));
        Log.d("update_token", "login called");
        if(!((Activity) getActivity()).isFinishing())
        {
            //show dialog
            progressDialog.show();
        }

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
                    load_data();
                    //call_api_coutry();
                }



            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                progressDialog.dismiss();
                //Build_alert_dialog(getActivity(), "Connection Error", "Please Check You Internet Connection");
            }
        });

    }


    class ListViewAdapter extends BaseAdapter {

        private List<Datum> list;
        private Context context;

        public ListViewAdapter(Context context, ArrayList<Datum> list) {
            this.list = list;
            this.context = context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.notification_list_view_item_1, null);
            TextView title_text = view.findViewById(R.id.title_text);
            TextView date = view.findViewById(R.id.date_text);
            ImageView iv = view.findViewById(R.id.image);

            title_text.setText(list.get(position).getTitle().toString().trim());
            date.setText(list.get(position).getDate().toString().trim());
            Glide.with(getActivity()).load(list.get(position).getLogo()).into(iv);

            Button accept_button = view.findViewById(R.id.accept_btn);
            Button ignore_button = view.findViewById(R.id.ignore_btn);

            accept_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    invitation_operation(list.get(position).getId().toString().trim(), "Accept");


                }


            });

            ignore_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    invitation_operation(list.get(position).getId().toString().trim(), "Ignore");

                }
            });


            return view;
        }

        private void invitation_operation(final String idOfInvitaiton, final String operation) {
            Call<Invite> call = api.getInviteJson(api_key, "Token " + pref.getString("token", null),
                    idOfInvitaiton, operation, "Android");
            if(!((Activity) getActivity()).isFinishing())
            {
                //show dialog
                progressDialog.show();
            }
            call.enqueue(new Callback<Invite>() {
                @Override
                public void onResponse(Call<Invite> call, Response<Invite> response) {

                    if (response.isSuccessful() && "Success".equals(response.body().getStatus())) {
                        progressDialog.dismiss();
                        //redirect user to main survey li8st fragment
                        if ("Accept".equals(operation)) {
                            getFragmentManager().beginTransaction().replace(R.id.framelayout_container, new HomeFragment()).commit();
                        } else {
                            //refresh this fragment (i.e notifications fragment)
                            getFragmentManager().beginTransaction().replace(R.id.framelayout_container, new NotificationFragment()).commit();
                        }

                    } else {
                        progressDialog.dismiss();

                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            /* String status = jObjError.getString("detail");
                             */
                            if (jObjError.getString("detail").equals("Invalid Token")) {
                                update_token_invitation_operation(idOfInvitaiton, operation);
                            } else {
                                noInvitation.setVisibility(View.VISIBLE);
                                noNotification.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {
                            //Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }
                }

                @Override
                public void onFailure(Call<Invite> call, Throwable t) {
                    progressDialog.dismiss();
                }
            });

        }

        private void update_token_invitation_operation(final String idOfInvitaiton, final String operation) {

            ApiService api = RetroClient.getApiService();

            //if fcm token is null then do not write in shared pref!
            if (PublicClass.FCM_TOKEN != null) {
                editor.putString("fcm_token", PublicClass.FCM_TOKEN);
                editor.commit();
            }

            Call<Login> call = api.getLoginJason(pref.getString("email", null), pref.getString("password", null), pref.getString("fcm_token", null),
                    "Android", Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID));

            if(!((Activity) getActivity()).isFinishing())
            {
                //show dialog
                progressDialog.show();
            }

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

                        invitation_operation(idOfInvitaiton, operation);
                        //call_api_coutry();
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

    class ListViewAdaper_NotificationList extends BaseAdapter {

        private List<Notification> notificationList;
        private Context context;

        public ListViewAdaper_NotificationList(Context context, ArrayList<Notification> notificationList) {
            this.notificationList = notificationList;
            this.context = context;
        }

        @Override
        public int getCount() {
            return notificationList.size();
        }

        @Override
        public Object getItem(int position) {
            return notificationList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);

            View view = layoutInflater.inflate(R.layout.notification_list_view_item_2, null);

            TextView title_notification = view.findViewById(R.id.notification_title);
            TextView date_notification = view.findViewById(R.id.notification_date);

            title_notification.setText(notificationList.get(position).getTitle().toString().trim());
            date_notification.setText(notificationList.get(position).getDate().toString().trim());

            return view;
        }
    }
}
