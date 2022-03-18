package com.example.coding_app.models.challenge;

import android.content.Context;

import com.example.coding_app.models.language.Language;

import java.util.HashMap;
import java.util.Map;

/**
 * Static class for managing all of the coding challenges
 */
public class ChallengeManager {
    private static Map<String, Challenge> challenges = new HashMap<String, Challenge>();

    //initialize challenge data
    public static void init(Context context){
        challenges.put("Hello World!", new Challenge(context, "hello_world.json"));
        challenges.put("Longest Common Prefix", new Challenge(context, "longest_common_prefix.json"));
    }

    public static Challenge getChallenge(String challengeName){
        if(!challenges.containsKey(challengeName)) return null;
        return challenges.get(challengeName);
    }

    public static Challenge[] getChallenges(){
        Challenge[] chs = challenges.values().toArray(new Challenge[0]);
        return chs;
    }
}
