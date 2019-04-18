package com.e2excel.myvoice.Fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.bumptech.glide.Glide;
import com.e2excel.myvoice.CustomDialogs.DeleteAccountNotificationErrorDialogFragment;
import com.e2excel.myvoice.R;
import com.e2excel.myvoice.Retrofit.ApiService;
import com.e2excel.myvoice.Retrofit.RetroClient;
import com.e2excel.myvoice.pojo.Invitation_delete.InviteDelete;
import com.e2excel.myvoice.pojo.SignIn.Login;
import com.e2excel.myvoice.pojo.invitation_accepted_list.Datum;
import com.e2excel.myvoice.pojo.invitation_accepted_list.InvitationList_accept_list;
import com.e2excel.myvoice.pojo.invitation_details.Invite;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class InvitaionList extends Fragment {

    View v;
    ApiService api;
    String api_key;
    private SharedPreferences pref, pref2;
    private SharedPreferences.Editor editor;
    private ProgressDialog progressDialog;
    private List<com.e2excel.myvoice.pojo.invitation_accepted_list.Datum> invitation_list;

    private ListView invitation_list_view;
    private SwipeMenuListView swipeMenuListView;

    private SwipeRefreshLayout swipeRefreshLayout_pending, swipeRefreshLayout_accepted;

    Button pending_button, accepted_button;
    TextView no_pending_tv, no_accepted_tv;
    ListViewAdapter_of_accepted_invitations adapter;
    ListViewAdapter_of_pending_invitations adapter1;

    public InvitaionList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_invitaion_list, container, false);
        progressDialog = new ProgressDialog(this.getActivity());
        progressDialog.setMax(100);
        progressDialog.setMessage("Loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        // mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeToRefresh);
//        mSwipeRefreshLayout.setColorSchemeResources(R.color.dark_blue);

        pref = this.getActivity().getSharedPreferences("MYVOICEAPP_PREF", Context.MODE_PRIVATE);
        editor = pref.edit();
        pref2 = this.getActivity().getSharedPreferences("FCM_PREF", Context.MODE_PRIVATE);

        api = RetroClient.getApiService();
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        TextView tt = toolbar.findViewById(R.id.title_text);
        tt.setText("Invitations");

        ImageView back = toolbar.findViewById(R.id.back_image);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.framelayout_container, new NotificationFragment()).commit();
            }
        });

        TextView logout_btn = toolbar.findViewById(R.id.logout_textview);
        logout_btn.setVisibility(View.INVISIBLE);

        api_key = getResources().getString(R.string.APIKEY);

        invitation_list = new ArrayList<>();

        invitation_list_view = v.findViewById(R.id.invitation_list);
        invitation_list_view.setVisibility(View.INVISIBLE);
        swipeMenuListView = v.findViewById(R.id.invitation_list_accepted);
        swipeMenuListView.setVisibility(View.INVISIBLE);


        pending_button = v.findViewById(R.id.p_b);

        accepted_button = v.findViewById(R.id.a_b);

        no_pending_tv = v.findViewById(R.id.no_pending_textview);
        no_pending_tv.setVisibility(View.INVISIBLE);
        no_accepted_tv = v.findViewById(R.id.no_accepted_textview);
        no_accepted_tv.setVisibility(View.INVISIBLE);

        swipeRefreshLayout_pending = v.findViewById(R.id.invitation_list_refreshView);
        swipeRefreshLayout_pending.setColorSchemeResources(R.color.dark_blue);
        swipeRefreshLayout_pending.setVisibility(View.INVISIBLE);
        swipeRefreshLayout_accepted = v.findViewById(R.id.swipeList_refreshView);
        swipeRefreshLayout_accepted.setColorSchemeResources(R.color.dark_blue);
        swipeRefreshLayout_accepted.setVisibility(View.VISIBLE);

        adapter1 = new ListViewAdapter_of_pending_invitations();
        adapter = new ListViewAdapter_of_accepted_invitations();

        //test_b = v.findViewById(R.id.test);

        swipeRefreshLayout_pending.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                no_pending_tv.setVisibility(View.INVISIBLE);
                no_accepted_tv.setVisibility(View.INVISIBLE);

                invitation_list_view.setVisibility(View.VISIBLE);
                swipeMenuListView.setVisibility(View.INVISIBLE);

                swipeRefreshLayout_pending.setVisibility(View.VISIBLE);
                swipeRefreshLayout_accepted.setVisibility(View.INVISIBLE);

                invitation_list_view.setAdapter(null);

                Call<InvitationList_accept_list> call = api.getInvitaionList_pending_Json(api_key, "Token " + pref.getString("token", null));

                if (!((Activity) getActivity()).isFinishing()) {
                    //show dialog
                    progressDialog.show();
                }

                call.enqueue(new Callback<InvitationList_accept_list>() {
                    @Override
                    public void onResponse(Call<InvitationList_accept_list> call, Response<InvitationList_accept_list> response) {
                        progressDialog.dismiss();
                        if (response.isSuccessful() && "Success".equals(response.body().getStatus())) {

                            adapter1 = null;
                            invitation_list = response.body().getData();

                            adapter1 = new ListViewAdapter_of_pending_invitations(getActivity(), invitation_list);

                            invitation_list_view.setAdapter(adapter1);


                        } else {
                            progressDialog.dismiss();

                            try {

                                JSONObject jObjError = new JSONObject(response.errorBody().string());
                                /* String status = jObjError.getString("detail");
                                 */

                                if (jObjError.has("detail")) {
                                    if (jObjError.getString("detail").equals("Invalid Token")) {
                                        update_token_pending();

                                    }
                                    else if (jObjError.getString("detail").equals("AccountDeleted")) {
                                        DeleteAccountNotificationErrorDialogFragment deleteAccountNotificationErrorDialogFragment = new DeleteAccountNotificationErrorDialogFragment();
                                        deleteAccountNotificationErrorDialogFragment.show(getFragmentManager(), "DeleteNotificationDialogFragment");
                                    }
                                }

                                no_pending_tv.setVisibility(View.VISIBLE);

                            } catch (Exception e) {
                                //Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<InvitationList_accept_list> call, Throwable t) {
                        progressDialog.dismiss();
                    }
                });
            }
        });

        swipeRefreshLayout_accepted.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                no_pending_tv.setVisibility(View.INVISIBLE);
                no_accepted_tv.setVisibility(View.INVISIBLE);

                swipeMenuListView.setVisibility(View.VISIBLE);
                invitation_list_view.setVisibility(View.INVISIBLE);

                swipeRefreshLayout_accepted.setVisibility(View.VISIBLE);
                swipeRefreshLayout_pending.setVisibility(View.INVISIBLE);


                Call<InvitationList_accept_list> call = api.getInvitaionList_accept_Json(api_key, "Token " + pref.getString("token", null));


                call.enqueue(new Callback<InvitationList_accept_list>() {
                    @Override
                    public void onResponse(Call<InvitationList_accept_list> call, Response<InvitationList_accept_list> response) {
                        swipeRefreshLayout_accepted.setRefreshing(false);
                        if (response.isSuccessful() && "Success".equals(response.body().getStatus())) {

                            invitation_list = response.body().getData();
                            //if(invitation_list!=null || invitation_list.size() == 0){

                            adapter = null;
                            adapter = new ListViewAdapter_of_accepted_invitations(getActivity(), invitation_list);
                            //invitation_list_view.setAdapter(null);
                            //adapter.notifyDataSetChanged();
                            swipeMenuListView.setAdapter(adapter);

                            SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
                                @Override
                                public void create(SwipeMenu menu) {
                                    SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity());
                                    deleteItem.setBackground(new ColorDrawable(Color.RED));
                                    deleteItem.setTitle("Delete");
                                    deleteItem.setTitleColor(Color.WHITE);
                                    deleteItem.setWidth(200);

                                    deleteItem.setTitleSize(15);
                                    menu.addMenuItem(deleteItem);
                                }
                            };
                            swipeMenuListView.setMenuCreator(swipeMenuCreator);


                            // }

                        } else {

                            swipeRefreshLayout_accepted.setRefreshing(false);

                            try {
                                JSONObject jObjError = new JSONObject(response.errorBody().string());
                                /* String status = jObjError.getString("detail");
                                 */


                                if (jObjError.has("detail")) {
                                    if (jObjError.getString("detail").equals("Invalid Token")) {
                                        update_token_accept();

                                    }
                                    else if (jObjError.getString("detail").equals("AccountDeleted")) {
                                        DeleteAccountNotificationErrorDialogFragment deleteAccountNotificationErrorDialogFragment = new DeleteAccountNotificationErrorDialogFragment();
                                        deleteAccountNotificationErrorDialogFragment.show(getFragmentManager(), "DeleteNotificationDialogFragment");
                                    }
                                }

                                no_accepted_tv.setVisibility(View.VISIBLE);

                            } catch (Exception e) {
                                //Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<InvitationList_accept_list> call, Throwable t) {

                        swipeRefreshLayout_accepted.setRefreshing(false);
                    }
                });

                swipeMenuListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(int position, SwipeMenu menu, final int index) {
                        Log.d("delete", "deleted Item id:" + invitation_list.get(index).getId().toString().trim());
                        Call<InviteDelete> call1 = api.getDelete_invitaitonJson(api_key, "Token " + pref.getString("token", null),
                                invitation_list.get(index).getId().toString().trim());
                        if (!((Activity) getActivity()).isFinishing()) {
                            //show dialog
                            progressDialog.show();
                        }

                        call1.enqueue(new Callback<InviteDelete>() {
                            @Override
                            public void onResponse(Call<InviteDelete> call, Response<InviteDelete> response) {
                                if (response.isSuccessful() && "Success".equals(response.body().getStatus())) {
                                    progressDialog.dismiss();
                                    invitation_list.remove(index);
                                    adapter.notifyDataSetChanged();
                                } else {


                                    progressDialog.dismiss();

                                    try {


                                        JSONObject jObjError = new JSONObject(response.errorBody().string());

                                        //check for invalid token
                                        if (jObjError.has("detail")) {
                                            if (jObjError.getString("detail").equals("Invalid Token")) {
                                                update_token_accept();
                                            }
                                            else if (jObjError.getString("detail").equals("AccountDeleted")) {
                                                DeleteAccountNotificationErrorDialogFragment deleteAccountNotificationErrorDialogFragment = new DeleteAccountNotificationErrorDialogFragment();
                                                deleteAccountNotificationErrorDialogFragment.show(getFragmentManager(), "DeleteNotificationDialogFragment");
                                            }

                                        }

                                        if (invitation_list.size() == 0) {
                                            no_accepted_tv.setVisibility(View.VISIBLE);
                                            swipeMenuListView.setVisibility(View.INVISIBLE);
                                        }

                                    } catch (Exception e) {
                                        //Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<InviteDelete> call, Throwable t) {
                                progressDialog.dismiss();
                            }
                        });

                        return false;
                    }
                });
            }
        });

        pending_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("test", "pending button clicked");
                //change the buttons' background color
                //pending_button.setBackgroundColor(getResources().getColor(R.color.dark_blue));
                //accepted_button.setBackgroundColor(Color.TRANSPARENT);

                //change text colors too
                pending_button.setBackgroundDrawable(getResources().getDrawable(R.drawable.pending_button_style_active));
                accepted_button.setBackgroundDrawable(getResources().getDrawable(R.drawable.accepted_button_style_not_active));

                pending_button.setTextColor(Color.WHITE);
                accepted_button.setTextColor(getResources().getColor(R.color.dark_blue));

                //call for getting the list of pending invitations
                get_pending_list_call();


            }


        });

        accepted_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("test", "accepted button clicked");
                //change the buttons' background color
                //accepted_button.setBackgroundColor(getResources().getColor(R.color.dark_blue));
                //pending_button.setBackgroundColor(Color.TRANSPARENT);
                accepted_button.setBackgroundDrawable(getResources().getDrawable(R.drawable.accepted_button_style_active));
                pending_button.setBackgroundDrawable(getResources().getDrawable(R.drawable.pending_button_style_not_active));
                //change text colors too
                accepted_button.setTextColor(Color.WHITE);
                pending_button.setTextColor(getResources().getColor(R.color.dark_blue));


                //call for getting the list of accepted invitations

                get_accept_list_call();


            }
        });


        pending_button.callOnClick();


       /* test_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("test", "test button is clicked");
            }
        });*/


    }

    private void get_accept_list_call() {

        no_pending_tv.setVisibility(View.INVISIBLE);
        no_accepted_tv.setVisibility(View.INVISIBLE);

        swipeMenuListView.setVisibility(View.VISIBLE);
        invitation_list_view.setVisibility(View.INVISIBLE);

        swipeRefreshLayout_accepted.setVisibility(View.VISIBLE);
        swipeRefreshLayout_pending.setVisibility(View.INVISIBLE);


        Call<InvitationList_accept_list> call = api.getInvitaionList_accept_Json(api_key, "Token " + pref.getString("token", null));

        if (!((Activity) getActivity()).isFinishing()) {
            //show dialog
            progressDialog.show();
        }

        call.enqueue(new Callback<InvitationList_accept_list>() {
            @Override
            public void onResponse(Call<InvitationList_accept_list> call, Response<InvitationList_accept_list> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && "Success".equals(response.body().getStatus())) {

                    invitation_list = response.body().getData();
                    //if(invitation_list!=null || invitation_list.size() == 0){

                    adapter = new ListViewAdapter_of_accepted_invitations(getActivity(), invitation_list);
                    //invitation_list_view.setAdapter(null);
                    //adapter.notifyDataSetChanged();
                    swipeMenuListView.setAdapter(adapter);

                    SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
                        @Override
                        public void create(SwipeMenu menu) {
                            SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity());
                            deleteItem.setBackground(new ColorDrawable(Color.RED));
                            deleteItem.setTitle("Delete");
                            deleteItem.setTitleColor(Color.WHITE);
                            deleteItem.setWidth(200);

                            deleteItem.setTitleSize(15);
                            menu.addMenuItem(deleteItem);
                        }
                    };
                    swipeMenuListView.setMenuCreator(swipeMenuCreator);


                    // }

                } else {
                    progressDialog.dismiss();

                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        /* String status = jObjError.getString("detail");
                         */


                        if (jObjError.has("detail")) {
                            if (jObjError.getString("detail").equals("Invalid Token")) {
                                update_token_accept();

                            }
                            else if (jObjError.getString("detail").equals("AccountDeleted")) {
                                DeleteAccountNotificationErrorDialogFragment deleteAccountNotificationErrorDialogFragment = new DeleteAccountNotificationErrorDialogFragment();
                                deleteAccountNotificationErrorDialogFragment.show(getFragmentManager(), "DeleteNotificationDialogFragment");
                            }
                        }


                        no_accepted_tv.setVisibility(View.VISIBLE);

                    } catch (Exception e) {
                        //Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<InvitationList_accept_list> call, Throwable t) {
                progressDialog.dismiss();
            }
        });

        swipeMenuListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, final int index) {
                Log.d("delete", "deleted Item id:" + invitation_list.get(index).getId().toString().trim());
                Call<InviteDelete> call1 = api.getDelete_invitaitonJson(api_key, "Token " + pref.getString("token", null),
                        invitation_list.get(index).getId().toString().trim());
                if (!((Activity) getActivity()).isFinishing()) {
                    //show dialog
                    progressDialog.show();
                }

                call1.enqueue(new Callback<InviteDelete>() {
                    @Override
                    public void onResponse(Call<InviteDelete> call, Response<InviteDelete> response) {
                        if (response.isSuccessful() && "Success".equals(response.body().getStatus())) {
                            progressDialog.dismiss();
                            invitation_list.remove(index);
                           adapter.notifyDataSetChanged();

                            get_accept_list_call();

                        } else {


                            progressDialog.dismiss();

                            try {


                                JSONObject jObjError = new JSONObject(response.errorBody().string());

                                //check for invalid token
                                if (jObjError.has("detail")) {
                                    if (jObjError.getString("detail").equals("Invalid Token")) {
                                        update_token_accept();
                                    }
                                    else if (jObjError.getString("detail").equals("AccountDeleted")) {
                                        DeleteAccountNotificationErrorDialogFragment deleteAccountNotificationErrorDialogFragment = new DeleteAccountNotificationErrorDialogFragment();
                                        deleteAccountNotificationErrorDialogFragment.show(getFragmentManager(), "DeleteNotificationDialogFragment");
                                    }

                                }

                                if (invitation_list.size() == 0) {
                                    no_accepted_tv.setVisibility(View.VISIBLE);
                                    swipeMenuListView.setVisibility(View.INVISIBLE);
                                }

                            } catch (Exception e) {
                                //Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<InviteDelete> call, Throwable t) {
                        progressDialog.dismiss();
                    }
                });

                return false;
            }
        });
    }

    private void get_pending_list_call() {
        no_pending_tv.setVisibility(View.INVISIBLE);
        no_accepted_tv.setVisibility(View.INVISIBLE);

        invitation_list_view.setVisibility(View.VISIBLE);
        swipeMenuListView.setVisibility(View.INVISIBLE);

        swipeRefreshLayout_pending.setVisibility(View.VISIBLE);
        swipeRefreshLayout_accepted.setVisibility(View.INVISIBLE);

        invitation_list_view.setAdapter(null);

        Call<InvitationList_accept_list> call = api.getInvitaionList_pending_Json(api_key, "Token " + pref.getString("token", null));

        if (!((Activity) getActivity()).isFinishing()) {
            //show dialog
            progressDialog.show();
        }

        call.enqueue(new Callback<InvitationList_accept_list>() {
            @Override
            public void onResponse(Call<InvitationList_accept_list> call, Response<InvitationList_accept_list> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && "Success".equals(response.body().getStatus())) {

                    invitation_list = response.body().getData();

                    ListViewAdapter_of_pending_invitations adapter = new ListViewAdapter_of_pending_invitations(getActivity(), invitation_list);

                    invitation_list_view.setAdapter(adapter);

                } else {
                    progressDialog.dismiss();

                    try {

                        JSONObject jObjError = new JSONObject(response.errorBody().string());

                        if (jObjError.has("detail")) {
                            if (jObjError.getString("detail").equals("Invalid Token")) {
                                update_token_pending();

                            }
                            else if (jObjError.getString("detail").equals("AccountDeleted")) {
                                DeleteAccountNotificationErrorDialogFragment deleteAccountNotificationErrorDialogFragment = new DeleteAccountNotificationErrorDialogFragment();
                                deleteAccountNotificationErrorDialogFragment.show(getFragmentManager(), "DeleteNotificationDialogFragment");
                            }
                        }
                        no_pending_tv.setVisibility(View.VISIBLE);

                    } catch (Exception e) {
                        //Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<InvitationList_accept_list> call, Throwable t) {
                progressDialog.dismiss();
            }
        });
    }

    private void update_token_pending() {
        //pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //Toast.makeText(getActivity(), "email from pref: " + pref.getString("email", "not fatched from pref"), Toast.LENGTH_SHORT).show();
        ApiService api = RetroClient.getApiService();

        //if fcm token is null then do not write in shared pref!
       /* if (PublicClass.FCM_TOKEN != null) {
            editor.putString("fcm_token", PublicClass.FCM_TOKEN);
            editor.commit();
        }
*/
        Call<Login> call = api.getLoginJason(pref.getString("email", null), pref.getString("password", null),
                pref2.getString("fcm_token", null),
                "Android", Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID));
        Log.d("update_token", "login called");
        if (!((Activity) getActivity()).isFinishing()) {
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
                    //call_api_coutry();
                    get_pending_list_call();
                }

            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                progressDialog.dismiss();
                //Build_alert_dialog(getActivity(), "Connection Error", "Please Check You Internet Connection");
            }
        });
    }

    private void update_token_accept() {
        //pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //Toast.makeText(getActivity(), "email from pref: " + pref.getString("email", "not fatched from pref"), Toast.LENGTH_SHORT).show();
        ApiService api = RetroClient.getApiService();

        //if fcm token is null then do not write in shared pref!
        /*if (PublicClass.FCM_TOKEN != null) {
            editor.putString("fcm_token", PublicClass.FCM_TOKEN);
            editor.commit();
        }*/

        Call<Login> call = api.getLoginJason(pref.getString("email", null), pref.getString("password", null),
                pref2.getString("fcm_token", null),
                "Android", Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID));
        Log.d("update_token", "login called");
        if (!((Activity) getActivity()).isFinishing()) {
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
                    //call_api_coutry();
                    get_accept_list_call();
                }

            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                progressDialog.dismiss();
                //Build_alert_dialog(getActivity(), "Connection Error", "Please Check You Internet Connection");
            }
        });
    }

    class ListViewAdapter_of_pending_invitations extends BaseAdapter {

        public ListViewAdapter_of_pending_invitations() {
        }

        private List<com.e2excel.myvoice.pojo.invitation_accepted_list.Datum> list;
        private Context context;

        public ListViewAdapter_of_pending_invitations(Context context, List<com.e2excel.myvoice.pojo.invitation_accepted_list.Datum> list) {
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
            if (!((Activity) getActivity()).isFinishing()) {
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
                            if (jObjError.has("detail")) {
                                if (jObjError.getString("detail").equals("Invalid Token")) {
                                    update_token_invitation_operation(idOfInvitaiton, operation);

                                }
                                else if (jObjError.getString("detail").equals("AccountDeleted")) {
                                    DeleteAccountNotificationErrorDialogFragment deleteAccountNotificationErrorDialogFragment = new DeleteAccountNotificationErrorDialogFragment();
                                    deleteAccountNotificationErrorDialogFragment.show(getFragmentManager(), "DeleteNotificationDialogFragment");
                                }
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
           /* if (PublicClass.FCM_TOKEN != null) {
                editor.putString("fcm_token", PublicClass.FCM_TOKEN);
                editor.commit();
            }*/

            Call<Login> call = api.getLoginJason(pref.getString("email", null), pref.getString("password", null),
                    pref2.getString("fcm_token", null),
                    "Android", Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID));

            if (!((Activity) getActivity()).isFinishing()) {
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

    class ListViewAdapter_of_accepted_invitations extends BaseAdapter {

        private List<Datum> list;
        private Context context;

        public ListViewAdapter_of_accepted_invitations() {
        }

        public ListViewAdapter_of_accepted_invitations(Context context, List<com.e2excel.myvoice.pojo.invitation_accepted_list.Datum> list) {
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
            View view = inflater.inflate(R.layout.invitation_accept_list_item, null);
            TextView title_text = view.findViewById(R.id.title_text);
            TextView date = view.findViewById(R.id.date_text);
            ImageView iv = view.findViewById(R.id.image);

            title_text.setText(list.get(position).getTitle().toString().trim());
            date.setText(list.get(position).getDate().toString().trim());
            Glide.with(getActivity()).load(list.get(position).getLogo()).into(iv);
            return view;
        }


    }


}
