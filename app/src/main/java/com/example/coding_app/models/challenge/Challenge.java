package com.example.coding_app.models.challenge;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.coding_app.models.JSONFileHandler;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.Set;

/**
 * Represents a single coding challenge. Data for the challenge (description,
 * test cases, etc.) is contained in 'challengeData' member
 */
public class Challenge {

    private static final String TAG = "Challenge";
    private static final String PATH_LOCAL = "data/data/com.example.coding_app.models.challenge/";
    private static final String PATH_ASSET = "challenge/";

    private String filename;
    private ChallengeData challengeData;

    public Challenge(Context context, String filename){
        this.filename = filename;
        File localFile = new File(context.getFilesDir(), PATH_LOCAL + filename);

        //if file already exists in local storage, read it
        if(localFile.exists()){
            challengeData = (ChallengeData)JSONFileHandler.getDataFromLocalFile(context, PATH_LOCAL + filename, ChallengeData.class);
        }

        //otherwise, get the initial data from assets
        else{
            challengeData = (ChallengeData)JSONFileHandler.getDataFromAssets(context, PATH_ASSET + filename, ChallengeData.class);

            try {
                localFile.getParentFile().mkdirs();
                localFile.createNewFile();
                writeChallengeToLocalFile(context);
            }
            catch(Exception e){
                Log.e(TAG, "Local file " + filename + " could not be created.", e);
            }
        }
    }

    public void writeChallengeToLocalFile(Context context){
        JSONFileHandler.writeDataToLocalFile(context, PATH_LOCAL + filename, challengeData);
    }

    public boolean isCompleted(){
        return challengeData.completed;
    }

    public String getDescriptionHTML() {
        return challengeData.description_html;
    }

    public String getName(){
        return challengeData.name;
    }

    public String getDifficultyLevel(){
        return challengeData.difficulty_level;
    }

    public String getSolution(String lang){
        return challengeData.solutions.get(lang);
    }

    public void setSolution(String langName, String newSolution){
        challengeData.solutions.put(langName, newSolution);
    }

    public void setCompleted(boolean completed){
        challengeData.completed = completed;
    }

    public TestCase[] getTestCases(){
        return challengeData.test_cases;
    }
}
