package com.rohan.myvoice;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class GetStarted extends AppCompatActivity {

    private TextView username, bold, regular;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);

        username = findViewById(R.id.hi_user);
        bold = findViewById(R.id.bold_text);
        regular = findViewById(R.id.reg_text);

        Intent i = getIntent();
        String u_name = i.getStringExtra("username");

        //Toast.makeText(this, "USERNAME: "+u_name, Toast.LENGTH_SHORT).show();

        username.setText("Hi, " + u_name);

        bold.setText("Thank you for downloading the\n" +
                        "MyVoice app, Please complete your profile.\n");

        regular.setText("Once your Profile is completed,\n" +
                        "you can start answering questions and be\n" +
                        "entered into our Prize Drawing.\n");

    }

    public void getstarted(View view) {
    }
}
