package com.something.dennisthicklin.ntouchnewsradio;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.something.dennisthicklin.Configuration.Configuration;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Timer;

public class MainActivity extends AppCompatActivity {


    FirebaseDatabase database;
    DatabaseReference myRef;
    RecyclerView adScrollView;
    LinearLayoutManager layoutManager;
    private BroadcastReceiver broadcastReceiver;



    ImageView liveRadioSymbol;
    DrawerLayout mDrawerLayout;
    NavigationView navigationView;
    Button linkButton, contactButton, aboutButton, archiveBButton, scheduleButton;
    Button liveRadioButton;
    MediaPlayer mediaPlayer;
    ImageButton menu;
    Timer timer;
    String stream = "http://s6.voscast.com:10886";
    boolean prepared = false;
    boolean started = false;

    // this variable will hold the count of all the images initially. It is made final to signify that it won't change
    //final int count;
    final int duration = 10;
    final int pixelsToMove = 30;
    private final Handler mHandler = new Handler(Looper.getMainLooper());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("ADS");
        linkButton = findViewById(R.id.website_button);
        contactButton = findViewById(R.id.contact_button);
        aboutButton = findViewById(R.id.about_button);
        liveRadioButton = findViewById(R.id.liveRadioButton);
        archiveBButton = findViewById(R.id.archive_button);
        scheduleButton = findViewById(R.id.schedule_button);
        //liveRadioSymbol = findViewById(R.id.liveRadioSymbol);
        adScrollView = findViewById(R.id.adViewer);
        adScrollView.setLayoutManager(layoutManager);
        adScrollView.setHasFixedSize(true);
        mediaPlayer = new MediaPlayer();
        navigationView = findViewById(R.id.navigationView);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        menu = findViewById(R.id.menu_button);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Configuration.STRING_PUSH)){
                    String message = intent.getStringExtra("message");
                    showNotification("NTouchNewsRadio", message);
                }
            }
        };

        new PlayerTask().execute(stream);

        liveRadioButton.setEnabled(false);
        liveRadioButton.setText("LOADING");


        liveRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (started && liveRadioButton.getText().equals("PLAY")) {
                    started = false;
                    mediaPlayer.setVolume(1, 1);
                    liveRadioButton.setText("PAUSE");

                } else {
                    started = true;
                    mediaPlayer.setVolume(0, 0);
                    liveRadioButton.setText("PLAY");
                }

            }
        });

        linkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToWebsite("https://www.n-touchnews.com");
            }
        });

        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Contact.class);
                startActivity(i);
                mediaPlayer.setVolume(0, 0);
            }
        });

        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, about.class);
                startActivity(i);
                mediaPlayer.setVolume(0, 0);
            }
        });

    }

    private void showNotification(String nTouchNewsRadio, String message) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.splash_logo)
                .setContentTitle(nTouchNewsRadio)
                .setContentText(message)
                .setContentIntent(contentIntent);
        NotificationManager notificationManager = (NotificationManager)getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private final Runnable SCROLLING_RUNNABLE = new Runnable() {

        @Override
        public void run() {
            adScrollView.smoothScrollBy(pixelsToMove, 0);
            mHandler.postDelayed(this, duration);
        }
    };

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

    private void goToWebsite(String url) {
        Uri myUri = Uri.parse(url);
        Intent goToBrowser = new Intent(Intent.ACTION_VIEW, myUri);
        startActivity(goToBrowser);
    }

    @Override
    protected void onStart() {
        super.onStart();

        final FirebaseRecyclerAdapter<ModelClass, ProductViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<ModelClass, ProductViewHolder>(
                        ModelClass.class,
                        R.layout.design_row,
                        ProductViewHolder.class,
                        myRef) {
                    @Override
                    protected void populateViewHolder(ProductViewHolder viewHolder, final ModelClass model, int position) {
                        viewHolder.setImage(getApplicationContext(), model.getImage());
                    }
                };
        adScrollView.setAdapter(firebaseRecyclerAdapter);


    }


    @Override
    protected void onStop() {
        super.onStop();

        if (timer != null) {
            timer.cancel();
        }
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        View myView;

        public ProductViewHolder(View productView) {
            super(productView);
            myView = productView;
        }

        public void setImage(Context ctx, String image) {
            ImageView productImageButton = myView.findViewById(R.id.adImageButton);
            Picasso.with(ctx).load(image).into(productImageButton);
        }


    }


    class PlayerTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {

            try {
                mediaPlayer.setDataSource(strings[0]);
                mediaPlayer.prepare();
                prepared = true;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return prepared;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            mediaPlayer.start();
            liveRadioButton.setEnabled(true);
            liveRadioButton.setText("PAUSE");
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (started) {
            mediaPlayer.setVolume(0, 0);
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (started) {
            mediaPlayer.setVolume(1, 1);
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver
                , new IntentFilter("registrationComplete"));
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(Configuration.STRING_PUSH));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (prepared) {
            mediaPlayer.release();
        }
    }
}




