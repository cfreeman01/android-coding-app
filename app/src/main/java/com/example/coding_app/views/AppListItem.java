package com.example.coding_app.views;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.coding_app.R;
import com.example.coding_app.models.challenge.Challenge;
import com.example.coding_app.models.productivity.AppInfo;

/**
 * List item for an app that is being tracked by the user.
 */
public class AppListItem extends FrameLayout {

    private AppInfo appInfo;
    private Context context;
    private View rootView;

    public AppListItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        rootView = inflate(context, R.layout.app_list_item, this);
    }

    /**
     * Initializes the view by passing in the appropriate challenge data
     */
    public void setApp(AppInfo appInfo){
        this.appInfo = appInfo;
        ImageView appIcon = rootView.findViewById(R.id.app_icon);
        TextView appName = rootView.findViewById(R.id.app_name);
        appName.setText(appInfo.getName());
        appIcon.setImageDrawable(appInfo.getIcon());
    }
}
