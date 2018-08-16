package com.something.dennisthicklin.ntouchnewsradio;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.messaging.RemoteMessage;
import com.something.dennisthicklin.Configuration.Configuration;

/**
 * Created by dennisthicklin on 5/5/18.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        handleNotification(remoteMessage.getNotification().getBody());
    }

    private void handleNotification(String body) {
        Intent pushNotification = new Intent(Configuration.STRING_PUSH);
        pushNotification.putExtra("message", body);
        LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
    }
}
