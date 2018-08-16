package com.something.dennisthicklin.ntouchnewsradio;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class Contact extends MainActivity {

    EditText name_field, email_field, subject_field, message_field;
    Button submitButton;
    DrawerLayout mDrawerLayout;
    NavigationView navigationView;
    ImageButton menu;

    /*public Contact() {
        super(count);
    }*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        subject_field = findViewById(R.id.subject_field);
        name_field = findViewById(R.id.name_field);
        message_field = findViewById(R.id.message_field);
        email_field = findViewById(R.id.email_text_field);
        submitButton = findViewById(R.id.submitButton);

        menu = findViewById(R.id.menu_button);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigationView);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.home:
                        Intent intent = new Intent(Contact.this, MainActivity.class);
                        startActivity(intent);
                        mediaPlayer.setVolume(0,0);
                        break;
                    case R.id.about:
                        Intent x = new Intent(Contact.this, about.class);
                        startActivity(x);
                        break;
                    case R.id.contact:
                        mDrawerLayout.closeDrawer(Gravity.START);
                        mediaPlayer.setVolume(0,0);
                        break;
                }

                return false;
            }
        });

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);



        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(Gravity.START);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendEmail = new Intent(Intent.ACTION_SEND);
                sendEmail.setData(Uri.parse("mailto:"));
                String[] emailTo = {"ntouch2020@gmail.com"};
                sendEmail.putExtra(Intent.EXTRA_EMAIL, emailTo);
                sendEmail.putExtra(Intent.EXTRA_SUBJECT,  name_field.getText().toString() + " : " + subject_field.getText().toString());
                sendEmail.putExtra(Intent.EXTRA_TEXT, message_field.getText().toString());
                sendEmail.setType("message/rfc822");
                startActivity(Intent.createChooser(sendEmail, "Send Us Your Message"));
            }
        });

    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        View decorView = getWindow().getDecorView();
        if (hasFocus) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                decorView.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
        }
    }
}
