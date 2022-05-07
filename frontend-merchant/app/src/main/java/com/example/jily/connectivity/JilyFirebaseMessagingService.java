package com.example.jily.connectivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.jily.R;
import com.example.jily.model.User;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

public class JilyFirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static ServerInterface mServerIf;
    private static FirebaseHandler mHandler;
    private static volatile JilyFirebaseMessagingService mInstance;

    public JilyFirebaseMessagingService() {
        mServerIf = ServerInterface.getInstance();
        mHandler = new FirebaseHandler();
    }

    public static JilyFirebaseMessagingService getInstance() {
        // Double check locking pattern
        if (mInstance == null) {                     // Check for the first time
            synchronized (JilyFirebaseMessagingService.class) {   // Check for the second time
                // If there is no instance available create a new one
                if (mInstance == null) mInstance = new JilyFirebaseMessagingService();
            }
        }
        return mInstance;
    }

    public void updateFirebaseToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("[JilyFirebaseMessagingService] UpdateFirebaseToken", "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();

                    updateFirebaseToken_(token);
                });
    }

    private void updateFirebaseToken_(String token) {
        mServerIf.setHandler(mHandler);
        User currentUser = RuntimeManager.getInstance().getCurrentUser();
        if (currentUser.getAccessToken().equals(User.DUMMY_ACCESS_TOKEN)) {
            return;
        }
        mServerIf.updateFirebaseToken(currentUser, token);
    }

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d("[JilyFirebaseMessagingService] OnNewToken", "Refreshed token: " + token);
        updateFirebaseToken_(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("[JilyFirebaseMessagingService] OnMessageReceived", "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d("[JilyFirebaseMessagingService] OnMessageReceived", "Message data payload: " + remoteMessage.getData());
        }

        if (remoteMessage.getData().get("message") != null) {
            sendNotification(remoteMessage.getData().get("message"));
        }
    }

    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, getClass());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = MessageConstants.DEFAULT_NOTIFICATION_ID;
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_menu_orders)
                        .setContentTitle(MessageConstants.FCM_MESSAGE)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        // TODO: Check messageBody and update orders correspondingly

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private class FirebaseHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.arg2) {
                case MessageConstants.OPERATION_SUCCESS:
                    break;

                default:
                    Toast.makeText(getApplicationContext(), "There's an issue updating Firebase token", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
