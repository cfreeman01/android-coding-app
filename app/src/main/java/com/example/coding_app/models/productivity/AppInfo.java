package com.example.coding_app.models.productivity;

import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;

/**
 * Holds information about an application
 */
public class AppInfo {
    private ApplicationInfo info;
    private String name;
    private Drawable icon;

    public AppInfo(ApplicationInfo info, String name, Drawable icon){
        this.info = info;
        this.name = name;
        this.icon = icon;
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
}