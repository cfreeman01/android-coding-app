package com.example.coding_app.models.productivity;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.usage.UsageStats;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.coding_app.R;
import com.example.coding_app.activities.MainActivity;
import com.example.coding_app.models.JSONFileHandler;
import com.example.coding_app.models.challenge.Challenge;
import com.example.coding_app.models.challenge.ChallengeManager;

import java.io.File;
import java.util.List;
import java.util.Random;

/**
 * Manages sending push notifications to users.
 * AlarmManager is used to run 'onReceive' every minute to check if a tracked app has run in the last
 * minute. If a tracked app has run in the last minute, but wasn't found to be running in the previous
 * execution of 'onReceive', then it means the app was started by the user, and a push notification
 * will be sent.
 */
public class PushNotificationManager extends BroadcastReceiver {

    public static String TAG = "PushNotificationManager";
    public static String PATH = "data/data/com.example.coding_app.models.productivity/notification_data.json";
    public static String CHANNEL_ID = "coding_app_notification";

    /**
     * Initialize PushNotificationManager by setting up the alarm
     */
    public static void init(Context context)
    {
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, PushNotificationManager.class);
        intent.setAction("models.productivity.alarm");
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60000 , pi);
    }

    /**
     * Cancel push notifications
     */
    public static void cancelAlarm(Context context)
    {
        Intent intent = new Intent(context, PushNotificationManager.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    /**
     * When a broadcast is received, possibly send a push notification
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("START", "MEMEMEMEME");
        ChallengeManager.init(context);
        AppUsageManager.init(context);
        if(AppUsageManager.checkPermissions(context)) {

            Boolean trackedAppRunning = false; //will be set to true if a tracked app has recently run
            Boolean previousResult    = false; //the result from the previous execution of this method

            //get the previous result
            File previousResultFile = new File(context.getFilesDir(), PATH);
            if (!previousResultFile.exists()) {  //if this is the first time this method is executing
                try {                            //create the file to store the result
                    previousResultFile.getParentFile().mkdirs();
                    previousResultFile.createNewFile();
                }
                catch(Exception e){
                    Log.e(TAG, "Local file " + PATH + " could not be created.", e);
                }
            }
            else {  //if the file exists, read it to get the previous result
                previousResult = (Boolean) JSONFileHandler.getDataFromLocalFile(context, PATH, Boolean.class);
            }

            //get currently tracked apps
            File trackedAppsFile = new File(context.getFilesDir(), AppManager.PATH);
            if (!trackedAppsFile.exists()) {  //if there are no tracked apps in local storage, return
                return;
            }
            String[] trackedAppNames = (String[]) JSONFileHandler.getDataFromLocalFile(context, AppManager.PATH, String[].class);
            //get recently run apps
            List<String> recentAppNames = AppUsageManager.getRecentlyRunningApps();
            if (recentAppNames != null) {
                //cycle through recent apps and see if any of them are being tracked
                for (String recentApp : recentAppNames) {
                    for (String trackedApp : trackedAppNames) {
                        if (trackedApp.equals(recentApp)) {
                            trackedAppRunning = true;
                            break;
                        }
                    }
                    if(trackedAppRunning) break;
                }
            }
            //write result to file
            JSONFileHandler.writeDataToLocalFile(context, PATH, trackedAppRunning);

            //if a tracked app has started running in the last minute, send a push notification
            if(!previousResult.booleanValue() && trackedAppRunning) {
                createNotification(context);
            }
        }
    }

    /**
     * Send a push notification to the user
     */
    private static void createNotification(Context context){
        String suggestedChallengeName = ChallengeManager.getSuggestedChallenge().getName();

        createNotificationChannel(context);

        //set the notification's tap action
        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.putExtra("challengeName", suggestedChallengeName);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //create the notification object
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_code_24_purple)
                .setContentTitle("Solve Your Next Coding Challenge")
                .setContentText(suggestedChallengeName)
                .setChannelId(CHANNEL_ID)
                .setColor(context.getResources().getColor(R.color.light_grey))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        //send the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(0, builder.build());
    }

    /**
     * Set up channel for the push notifications
     */
    private static void createNotificationChannel(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Coding App Notification", importance);
            channel.setDescription("Notifications from coding app");
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            Log.e("SEND", "MEMEMEMEME");
        }
    }
}