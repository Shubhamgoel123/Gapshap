package com.example.kachucool.gapshap;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Kachucool on 05-10-2017.
 */

public class FirebaseMessaginService extends FirebaseMessagingService{

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String Notification_title=remoteMessage.getNotification().getTitle();
        String Notification_message=remoteMessage.getNotification().getBody();

        String Clich_action=remoteMessage.getNotification().getClickAction();

        String from_user_id=remoteMessage.getData().get("from_user_id");
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(Notification_title)
                        .setContentText(Notification_message);


        Intent resultIntent = new Intent(Clich_action);
        resultIntent.putExtra("user_id",from_user_id);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);


        // Sets an ID for the notification
        int mNotificationId = (int)System.currentTimeMillis();
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }
}
