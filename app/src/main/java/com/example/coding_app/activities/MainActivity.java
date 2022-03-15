package com.example.coding_app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.coding_app.R;
import com.example.coding_app.fragments.CodingEnvironmentFragment;
import com.example.coding_app.fragments.ProfileFragment;
import com.example.coding_app.models.Judge.Judge;
import com.example.coding_app.models.challenge.ChallengeManager;
import com.example.coding_app.models.language.LanguageManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView mainBottomNavView;
    private FragmentContainerView mainFragContainerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize static objects
        Judge.init(getApplicationContext());
        LanguageManager.init(getApplicationContext());
        ChallengeManager.init(getApplicationContext());

        //hide top bar
        getSupportActionBar().hide();

        mainFragContainerView = findViewById(R.id.main_fragment_container_view);
        createMainBottomNavView();
    }

    //hook up the bottom navigation view
    private void createMainBottomNavView(){
        mainBottomNavView = findViewById(R.id.main_bottom_nav_view);
        mainBottomNavView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.bottom_nav_code){
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_fragment_container_view, CodingEnvironmentFragment.class, null)
                            .setReorderingAllowed(true)
                            .commit();
                }
                else if(item.getItemId() == R.id.bottom_nav_profile){
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_fragment_container_view, ProfileFragment.class, null)
                            .setReorderingAllowed(true)
                            .commit();
                }
                return true;
            }
        });
    }
}