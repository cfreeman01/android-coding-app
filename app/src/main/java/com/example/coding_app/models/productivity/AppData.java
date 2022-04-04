package com.example.coding_app.models.productivity;

import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;

/**
 * Holds information about an application
 */
public class AppData {
    private ApplicationInfo info;
    private String name;
    private Drawable icon;
    private String packageName;

    public AppData(ApplicationInfo info, String name, Drawable icon, String packageName){
        this.info = info;
        this.name = name;
        this.icon = icon;
        this.packageName = packageName;
    }

    public ApplicationInfo getInfo(){
        return info;
    }

    public String getName(){
        return name;
    }

    public Drawable getIcon(){
        return icon;
    }

    public String getPackageName(){
        return packageName;
    }
}