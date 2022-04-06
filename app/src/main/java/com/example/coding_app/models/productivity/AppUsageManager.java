package com.example.coding_app.models.productivity;

import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Static class that tracks usage time of applications
 * using Android's UsageStatsManager API
 */
public class AppUsageManager {

    private static UsageStatsManager usageStatsManager;
    private static Map<String, UsageStats> usageStats;

    /**
     * Initialize AppUsageManager
     */
    public static void init(Context context){
        //get usage stats for all apps in the last week
        usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.WEEK_OF_YEAR, -1);
        usageStats = usageStatsManager.queryAndAggregateUsageStats(calendar.getTimeInMillis(), System.currentTimeMillis());
    }

    /**
     * Get usage time in the last week for a specific application denoted
     * by packageName
     */
    public static long getUsageTime(String packageName){
        if(usageStats.get(packageName) == null) return 0;
        return usageStats.get(packageName).getTotalTimeInForeground();
    }

    /**
     * Check if permission to view app usage is granted.
     */
    public static boolean checkPermissions(Context context){
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow("android:get_usage_stats", android.os.Process.myUid(), context.getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    /**
     * Get a list of apps that have run in the last minute
     */
    public static List<String> getRecentlyRunningApps(){
        if(usageStatsManager == null) return null;
        long time = System.currentTimeMillis();
        List<UsageStats> appList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,
                time - (1000 * 1000),
                time);

        long currentTime = System.currentTimeMillis();
        List<String> recentlyUsedPackageNames = new ArrayList<String>();

        for(UsageStats app: appList){
            if((currentTime - app.getLastTimeUsed()) <= (1000 * 60)){
                recentlyUsedPackageNames.add(app.getPackageName());
            }
        }

        return recentlyUsedPackageNames;
    }
}