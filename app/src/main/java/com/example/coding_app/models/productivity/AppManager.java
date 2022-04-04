package com.example.coding_app.models.productivity;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.coding_app.models.JSONFileHandler;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Static class for managing apps that are being "tracked" by the user in order
 * to stay more productive. Tracking an app has two effects:
 *
 * 1) The usage time in the last week is displayed in the user's profile
 *
 * 2) The user will receive push notifications with suggestions for coding
 *    challenges while using the app.
 */
public class AppManager {

    private static final String TAG = "AppUsageManager";
    private static final String PATH = "data/data/com.example.coding_app.models.productivity/tracked_apps.json";

    private static Map<String, AppData> trackedApps = new HashMap<String, AppData>();

    /**
     * Initialize AppManager
     */
    public static void init(Context context){
        //check local storage for apps being tracked
        File localFile = new File(context.getFilesDir(), PATH);

        //if file already exists in local storage, read it
        if(localFile.exists()){
            String[] packageNames = (String[]) JSONFileHandler.getDataFromLocalFile(context, PATH, String[].class);
            if(packageNames != null)
                for(String packageName: packageNames)
                    addApp(context, packageName);
        }
    }

    /**
     * Get all tracked apps as an array
     */
    public static AppData[] getApps(){
        AppData[] apps = trackedApps.values().toArray(new AppData[0]);
        return apps;
    }

    /**
     * Add an app to trackedApps
     */
    public static AppData addApp(Context context, String packageName){
        AppData appInfo = null;

        try {
            ApplicationInfo app = context.getPackageManager().getApplicationInfo(packageName, 0);
            Drawable icon = context.getPackageManager().getApplicationIcon(app);
            String name = context.getPackageManager().getApplicationLabel(app).toString();

            appInfo = new AppData(app, name, icon, packageName);
            trackedApps.put(packageName, appInfo);
        }
        catch (Exception e) {
            Log.e(TAG, "Error adding app to tracked apps list.", e);
        }

        return appInfo;
    }

    /**
     * Store tracked apps in a local file by the associated package name
     */
    public static void writeAppsToLocalFile(Context context){
        //get local file
        File localFile = new File(context.getFilesDir(), PATH);

        //if it doesn't exist, create it
        if(!localFile.exists()){
            try {
                localFile.getParentFile().mkdirs();
                localFile.createNewFile();
            }
            catch(Exception e){
                Log.e(TAG, "Local file " + PATH + " could not be created.", e);
            }
        }

        //write the package names to the file as a json object
        String[] packageNames = trackedApps.keySet().toArray(new String[0]);
        JSONFileHandler.writeDataToLocalFile(context, PATH, packageNames);
    }

    /**
     * Get app info from package name
     */
    public static AppData getApp(String packageName){
        return trackedApps.get(packageName);
    }

    /**
     * Remove an app from trackedApps
     */
    public static void removeApp(String packageName){
        trackedApps.remove(packageName);
    }
}