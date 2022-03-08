package com.example.coding_app.models.challenge;

import android.content.SharedPreferences;

public class Challenge {

    private SharedPreferences file;
    private ChallengeUserData userData;

    public Challenge(SharedPreferences file){
        this.file = file;
    }

    public String getDescriptionHTML() { return userData.descriptionHTML; }

    public void serialize(){

    }
}
