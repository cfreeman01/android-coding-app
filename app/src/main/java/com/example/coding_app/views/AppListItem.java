package com.example.coding_app.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.coding_app.R;
import com.example.coding_app.models.productivity.AppData;

/**
 * List item for an app that is being tracked by the user.
 */
public class AppListItem extends FrameLayout {

    private AppData appInfo;
    private Context context;
    private View rootView;
    private long usageTime;

    public AppListItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        rootView = inflate(context, R.layout.app_list_item, this);
        ImageView removeButton = rootView.findViewById(R.id.remove_button);
        removeButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_remove_circle_24));
    }

    /**
     * Initializes the view by passing in the appropriate app data
     */
    public void setApp(AppData appInfo){
        this.appInfo = appInfo;
        ImageView appIcon = rootView.findViewById(R.id.app_icon);
        TextView appName = rootView.findViewById(R.id.app_name);
        appName.setText(appInfo.getName());
        appIcon.setImageDrawable(appInfo.getIcon());
    }

    /**
     * Set the usage time to display on the view
     */
    public void setUsageTime(long usageTime){
        this.usageTime = usageTime;
        String usageTimeString = getUsageTimeString(usageTime);
        TextView appUsageTime = rootView.findViewById(R.id.app_usage_time);
        appUsageTime.setText(usageTimeString);
    }

    /**
     * Set OnClickListener for the remove button
     */
    public void setRemoveClickListener(View.OnClickListener ocl){
        ImageView removeButton = rootView.findViewById(R.id.remove_button);
        removeButton.setOnClickListener(ocl);
    }

    /**
     * Convert usage time in milliseconds to string format
     */
    public String getUsageTimeString(long usageTimeMs){
        int secs = (int)(usageTimeMs / 1000);
        int hours = secs / 3600;
        secs = secs % 3600;
        int mins = secs / 60;
        secs = secs % 60;
        return "" + hours + "h " + mins + "m " + secs + "s";
    }
}
