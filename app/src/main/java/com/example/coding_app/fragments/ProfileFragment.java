package com.example.coding_app.fragments;

import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.coding_app.R;
import com.example.coding_app.activities.MainActivity;
import com.example.coding_app.models.challenge.Challenge;
import com.example.coding_app.models.challenge.ChallengeManager;
import com.example.coding_app.models.productivity.AppData;
import com.example.coding_app.models.productivity.AppManager;
import com.example.coding_app.models.productivity.AppUsageManager;
import com.example.coding_app.models.productivity.PushNotificationManager;
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

        if(AppUsageManager.checkPermissions(getContext())) {
            AppUsageManager.init(getContext());
            PushNotificationManager.init(getContext());
            rootView = inflater.inflate(R.layout.profile_layout, null);
            initChallengesCompletedView();
            initSuggestedChallenge();
            initAppsTracker();
        }
        else{
            rootView = inflater.inflate(R.layout.no_permission_layout, null);
            TextView message = rootView.findViewById(R.id.no_permission_message);
            message.setText("App usage permission not granted.\n\n" +
                    "Please grant permission to view app usage data in order to use productivity features.\n\n" +
                    "(Settings > Apps and Notifications > Special app access > Usage access)");
        }

        return rootView;
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
        ImageView infoButton = rootView.findViewById(R.id.info_button);
        TextView infoBox = rootView.findViewById(R.id.info_box);

        //add already tracked apps to list
        for(AppData ai: AppManager.getApps())
            addAppToList(ai);

        //initialize info button and text box
        infoButton.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_baseline_info_24));
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(infoBox.getVisibility() == View.INVISIBLE)
                    infoBox.setVisibility(View.VISIBLE);
                else
                    infoBox.setVisibility(View.INVISIBLE);
            }
        });
        infoBox.setVisibility(View.INVISIBLE);
        infoBox.setText(
                "Tracking an app can help you stay more productive by: \n\n" +
                "1) Showing you the amount of time you used the app in the last week\n\n" +
                "2) Sending you notifications with suggestions for coding challenges while using the app"
        );

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
            AppData newApp = AppManager.addApp(getContext(), appPackageName);

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
        AppManager.writeAppsToLocalFile(getContext());
    }

    /**
     * Add an app to the tracked apps list
     */
    private void addAppToList(AppData newApp){
        LinearLayout appList = rootView.findViewById(R.id.apps_list);
        AppListItem ali = new AppListItem(rootView.getContext(), null);
        ali.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        //initialize the view
        ali.setApp(newApp);
        ali.setUsageTime(AppUsageManager.getUsageTime(newApp.getPackageName()));

        //determine background color for the view
        if(alternate_background)
            ali.setBackground(rootView.getContext().getDrawable(R.color.light_grey));
        else
            ali.setBackground(rootView.getContext().getDrawable(R.color.med_grey));
        alternate_background = !alternate_background;

        //add to list
        appList.addView(ali);

        //set click listener to remove the tracked app
        ali.setRemoveClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppManager.removeApp(newApp.getPackageName());
                appList.removeAllViews();
                alternate_background = false;
                for(AppData ai: AppManager.getApps())
                    addAppToList(ai);
            }
        });
    }
}
