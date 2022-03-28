package com.example.coding_app.fragments;

import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.coding_app.R;
import com.example.coding_app.activities.MainActivity;
import com.example.coding_app.models.challenge.Challenge;
import com.example.coding_app.models.challenge.ChallengeManager;
import com.example.coding_app.models.productivity.AppInfo;
import com.example.coding_app.models.productivity.AppUsageManager;
import com.example.coding_app.views.AppListItem;
import com.example.coding_app.views.ChallengeListItem;

/**
 * Displays the user's personalized profile which contains:
 * 1) Number of challenges completed
 * 2) Suggested next challenge
 * 3) Productivity tools (see models/productivity)
 */
public class ProfileFragment extends Fragment {

    private final int REQUEST_CHOOSE_APP = 0;

    private View rootView;
    private boolean alternate_background = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.profile_layout, null);

        if(!checkPermissions()) return rootView;

        initChallengesCompletedView();
        initSuggestedChallenge();
        initAppsTracker();

        return rootView;
    }

    /**
     * Check if usage access is granted. If not, display a message saying so.
     * If granted, display the rest of the layout
     */
    private boolean checkPermissions(){
        AppOpsManager appOps = (AppOpsManager) getContext().getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow("android:get_usage_stats", android.os.Process.myUid(), getContext().getPackageName());
        boolean granted = mode == AppOpsManager.MODE_ALLOWED;
        return granted;
    }

    /**
     * Initialize the text view which shows the number of challenges completed
     */
    private void initChallengesCompletedView(){
        TextView challengesCompleted = rootView.findViewById(R.id.challenges_completed);
        Challenge[] challenges = ChallengeManager.getChallenges();
        int numCompleted = ChallengeManager.getNumCompleted();
        challengesCompleted.setText("Challenges completed:\n" +
                numCompleted + "/" + challenges.length);
    }

    /**
     * Initialize the view which shows the suggested next challenge for the user to complete
     */
    private void initSuggestedChallenge(){
        ChallengeListItem cli = rootView.findViewById(R.id.suggested_challenge);
        Challenge suggested = ChallengeManager.getSuggestedChallenge();
        cli.setChallenge(suggested);
        cli.setBackground(rootView.getContext().getDrawable(R.color.med_grey));
        cli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setBackground(getResources().getDrawable(R.color.purple_200));
                Bundle args = new Bundle();
                args.putString("Challenge", cli.getChallenge().getName());
                ((MainActivity)getActivity()).replaceMainFragment(CodingEnvironmentFragment.class, args);
            }
        });
    }

    /**
     * Initialize the portion of the that allows users to track usage of
     * other apps enable push notifications while using them
     */
    private void initAppsTracker(){
        LinearLayout appsList = rootView.findViewById(R.id.apps_list);
        Button addAppButton = rootView.findViewById(R.id.add_app_button);

        //add already tracked apps to list
        for(AppInfo ai: AppUsageManager.getApps())
            addAppToList(ai);

        /*on click: new activity opens allowing the user to select an app
        to add to the list*/
        addAppButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_SETTINGS);
                Intent extra = new Intent(Intent.ACTION_MAIN, null);
                extra.addCategory(Intent.CATEGORY_LAUNCHER);
                i.setAction(Intent.ACTION_PICK_ACTIVITY);
                i.putExtra(Intent.EXTRA_INTENT, extra);
                startActivityForResult(i, REQUEST_CHOOSE_APP);
            }
        });
    }

    /**
     * This is called after a user selects a new app to track. If the result is valid
     * the new app is saved in AppUsageManager and the list item for the app is added
     * to the layout for this fragment
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        //if user is adding an app to the list
        if(requestCode == REQUEST_CHOOSE_APP && data != null) {

            //add the app to the AppUsageManager
            ComponentName cm = data.resolveActivity(getActivity().getPackageManager());
            String appPackageName = cm.getPackageName();
            AppInfo newApp = AppUsageManager.addApp(getContext(), appPackageName);

            //add the view to the app list
            addAppToList(newApp);
        }
    }

    /**
     * On stop: save tracked apps to local file
     */
    @Override
    public void onStop(){
        super.onStop();
        AppUsageManager.writeAppsToLocalFile(getContext());
    }

    /**
     * Add an app to the tracked apps list
     */
    private void addAppToList(AppInfo newApp){
        LinearLayout appList = rootView.findViewById(R.id.apps_list);
        AppListItem ali = new AppListItem(rootView.getContext(), null);
        ali.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        ali.setApp(newApp);
        if(alternate_background)
            ali.setBackground(rootView.getContext().getDrawable(R.color.light_grey));
        else
            ali.setBackground(rootView.getContext().getDrawable(R.color.med_grey));
        alternate_background = !alternate_background;
        appList.addView(ali);
    }
}
