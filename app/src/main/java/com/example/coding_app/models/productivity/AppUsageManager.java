package com.example.coding_app.models.productivity;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;

import java.util.Calendar;
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
}