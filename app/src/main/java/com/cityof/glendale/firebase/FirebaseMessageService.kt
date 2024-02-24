package com.cityof.glendale.firebase

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.cityof.glendale.ComposeMainActivity
import com.cityof.glendale.R
import com.cityof.glendale.utils.LegacyPreferences
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber
import java.util.Random


class BeelineFirebaseMessagingService : FirebaseMessagingService() {

    val TAG: String = this::class.java.simpleName

    private val CLIMATE_CHANNEL_ID = "com.cityof.glendale"
    private val CLIMATE_CHANNEL_NAME = "Climate Mobility"

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)


        Timber.d("ON MESSAGE RECEIVED")
        message.notification?.title?.let {
            sendNotification(it)
            Timber.d("ON MESSAGE RECEIVED:: it")
        }


    }


    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.d("$TAG NEW_TOKEN -> $token")
        LegacyPreferences.fcmToken = token
    }


    //This method is only generating push notification
    //It is same as we did in earlier posts
    /*
    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Notification", messageBody);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        notificationBuilder.setSmallIcon(getNotificationIcon(notificationBuilder), 1);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(TWO_CHANNEL_ID,
                    getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(0, notificationBuilder.build());
    }
*/
    private fun sendNotification(message: String) {

        var launchIntent: Intent? = null
        launchIntent = Intent(this, ComposeMainActivity::class.java)
        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addNextIntent(launchIntent)
        val notifyId = Random().nextInt(9999 - 1000) + 1000
        var pendingIntent = stackBuilder.getPendingIntent(notifyId, PendingIntent.FLAG_IMMUTABLE)

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        var notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this).setContentTitle(getString(R.string.app_name))
                .setContentText(message).setAutoCancel(true).setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(
                CLIMATE_CHANNEL_ID, CLIMATE_CHANNEL_NAME, importance
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.setShowBadge(true)
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            manager.createNotificationChannel(notificationChannel)
            notificationBuilder = NotificationCompat.Builder(this, CLIMATE_CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name)).setContentText(message)
                .setAutoCancel(true).setSound(defaultSoundUri).setContentIntent(pendingIntent)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setChannelId(CLIMATE_CHANNEL_ID)
        } else {
            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
        }
        manager.notify(notifyId, notificationBuilder.build())
    }
}