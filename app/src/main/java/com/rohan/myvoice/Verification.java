package com.rohan.myvoice;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.rohan.myvoice.Retrofit.ApiService;
import com.rohan.myvoice.Retrofit.RetroClient;

public class Verification extends AppCompatActivity {

    private EditText e1, e2, e3, e4, e5, e6;
    private TextView title_tv;

    ApiService api;
    String api_key;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        title_tv = findViewById(R.id.title);

        e1 = findViewById(R.id.e1);
        e2 = findViewById(R.id.e2);
        e3 = findViewById(R.id.e3);
        e4 = findViewById(R.id.e4);
        e5 = findViewById(R.id.e5);
        e6 = findViewById(R.id.e6);

        // Set up progress before call
        progressDialog = new ProgressDialog(Verification.this);
        progressDialog.setMax(100);
        progressDialog.setMessage("Loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        // mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeToRefresh);
        //mSwipeRefreshLayout.setColorSchemeResources(R.color.dark_blue);

        pref = getSharedPreferences("MYVOICEAPP_PREF", Context.MODE_PRIVATE);
        editor = pref.edit();

        api = RetroClient.getApiService();
        api_key = getResources().getString(R.string.APIKEY);

        e1.addTextChangedListener(new GenericTextWatcher(e1));
        e2.addTextChangedListener(new GenericTextWatcher(e2));
        e3.addTextChangedListener(new GenericTextWatcher(e3));
        e4.addTextChangedListener(new GenericTextWatcher(e4));
        e5.addTextChangedListener(new GenericTextWatcher(e5));
        e6.addTextChangedListener(new GenericTextWatcher(e6));

        e1.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // You can identify which key pressed buy checking keyCode value
                // with KeyEvent.KEYCODE_
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    // this is for backspace
                   clear_all();
                }
                return false;
            }
        });

        e2.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // You can identify which key pressed buy checking keyCode value
                // with KeyEvent.KEYCODE_
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    // this is for backspace
                    clear_all();
                }
                return false;
            }
        });
        e3.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // You can identify which key pressed buy checking keyCode value
                // with KeyEvent.KEYCODE_
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    // this is for backspace
                    clear_all();
                }
                return false;
            }
        });
        e4.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // You can identify which key pressed buy checking keyCode value
                // with KeyEvent.KEYCODE_
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    // this is for backspace
                    clear_all();
                }
                return false;
            }
        });
        e5.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // You can identify which key pressed buy checking keyCode value
                // with KeyEvent.KEYCODE_
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    // this is for backspace
                    clear_all();
                }
                return false;
            }
        });

        e6.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // You can identify which key pressed buy checking keyCode value
                // with KeyEvent.KEYCODE_
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    // this is for backspace
                    clear_all();
                }
                return false;
            }
        });


        /*///change the title text
    title_tv.setText();*/

        String email = pref.getString("email", " ");
        title_tv.setText("We just sent a verification code \n to " + email + ".");


    }

    public void submit_otp_request(View view) {
    }

    public void reset_password_request(View view) {
    }


    class GenericTextWatcher implements TextWatcher {
        private View view;

        private GenericTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // TODO Auto-generated method stub
            String text = editable.toString();
            switch (view.getId()) {

                case R.id.e1:
                    if (text.length() == 1)
                        e2.requestFocus();
                    break;
                case R.id.e2:
                    if (text.length() == 1)
                        e3.requestFocus();
                    else if (text.length() == 0)
                        e1.requestFocus();
                    break;
                case R.id.e3:
                    if (text.length() == 1)
                        e4.requestFocus();
                    else if (text.length() == 0)
                        e2.requestFocus();
                    break;
                case R.id.e4:
                    if (text.length() == 1)
                        e5.requestFocus();
                    else if (text.length() == 0)
                        e3.requestFocus();
                    break;
                case R.id.e5:
                    if (text.length() == 1)
                        e6.requestFocus();
                    else if (text.length() == 0)
                        e4.requestFocus();
                    break;
                case R.id.e6:
                    if (text.length() == 1) {
                        hide_keyboard();

                    } else if (text.length() == 0)
                        e5.requestFocus();
                    break;
            }
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub


        }



    }

    public void clear_all(){
        //backspace pressed
        e1.getText().clear();
        e2.getText().clear();
        e3.getText().clear();
        e4.getText().clear();
        e5.getText().clear();
        e6.getText().clear();

        e1.requestFocus();

    }

    private void hide_keyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Verification.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}
