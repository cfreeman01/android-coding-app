package com.example.coding_app.models.challenge;

import android.content.Context;

import com.example.coding_app.models.language.Language;

import java.util.HashMap;
import java.util.Map;

/**
 * Static class for managing all of the coding challenges
 */
public class ChallengeManager {
    private static Map<String, Challenge> challenges;

    //initialize challenge data
    public static void init(Context context){
        if(challenges != null) return;
        challenges = new HashMap<String, Challenge>();
        challenges.put("Hello World!", new Challenge(context, "hello_world.json"));
        challenges.put("Longest Common Prefix", new Challenge(context, "longest_common_prefix.json"));
        challenges.put("Kth Largest Element", new Challenge(context, "kth_largest.json"));
        challenges.put("Rotate Array", new Challenge(context, "rotate_array.json"));
        challenges.put("Next Permutation", new Challenge(context, "next_permutation.json"));
    }

    /**
     * Get a challenge by name
     */
    public static Challenge getChallenge(String challengeName){
        if(!challenges.containsKey(challengeName)) return null;
        return challenges.get(challengeName);
    }

    /**
     * Return an array of all challenges
     */
    public static Challenge[] getChallenges(){
        Challenge[] chs = challenges.values().toArray(new Challenge[0]);
        return chs;
    }

    /**
     * Get number of challenges completed
     */
    public static int getNumCompleted(){
        Challenge[] challenges = getChallenges();
        int numCompleted = 0;

        for(Challenge ch: challenges){
            if(ch.isCompleted()){
                numCompleted++;
            }
        }

        return numCompleted;
    }

    /**
     * Get the suggested next challenge for the user to complete
     */
    public static Challenge getSuggestedChallenge(){
        Challenge[] challenges = getChallenges();
        Challenge suggested = challenges[challenges.length - 1];

        for(Challenge ch: challenges){
            if(!ch.isCompleted()){
                suggested = ch;
                break;
            }
        }

        return suggested;
    }
}
