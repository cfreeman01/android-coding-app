package com.example.coding_app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.coding_app.R;
import com.example.coding_app.fragments.ChallengeListFragment;
import com.example.coding_app.fragments.ProfileFragment;
import com.example.coding_app.models.Judge.Judge;
import com.example.coding_app.models.challenge.ChallengeManager;
import com.example.coding_app.models.language.LanguageManager;
import com.example.coding_app.models.productivity.AppManager;
import com.example.coding_app.models.productivity.AppUsageManager;
import com.example.coding_app.models.productivity.PushNotificationManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

/**
 * Starting activity
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize static objects
        Context context = getApplicationContext();
        Judge.init(context);
        LanguageManager.init(context);
        ChallengeManager.init(context);
        AppManager.init(context);

        //hide top bar
        getSupportActionBar().hide();

        createMainBottomNavView();
    }

    /**
     * Hook up the main bottom navigation view
     */
    private void createMainBottomNavView(){
        BottomNavigationView mainBottomNavView = findViewById(R.id.main_bottom_nav_view);
        mainBottomNavView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.bottom_nav_code)
                    replaceMainFragment(ChallengeListFragment.class, null);
                else if(item.getItemId() == R.id.bottom_nav_profile)
                    replaceMainFragment(ProfileFragment.class, null);
                return true;
            }
        });
    }

    /**
     * Swap out the main fragment being displayed
     */
    public void replaceMainFragment(Class newFragmentType, Bundle args){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container_view, newFragmentType, args)
                .setReorderingAllowed(true)
                .commit();
    }
}