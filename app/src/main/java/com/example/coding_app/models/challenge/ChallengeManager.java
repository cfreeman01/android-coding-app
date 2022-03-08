package com.example.coding_app.models.challenge;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

public class ChallengeManager {
    private static Map<String, Challenge> challenges = new HashMap<String, Challenge>();

    //initialize language data
    public static void init(Context context){
        challenges.put("Hello World!", new Challenge(context.getSharedPreferences("Hello World!", MODE_PRIVATE)));
    }
}
