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
            try {
                challengeData = (ChallengeData)JSONFileHandler.getDataFromLocalFile(context, PATH_LOCAL + filename, ChallengeData.class);
            }
            catch(Exception e){
                Log.e(TAG, "Local file " + filename + " could not be read.", e);
            }
        }

        //otherwise, get the initial data from assets
        else{
            try {
                challengeData = (ChallengeData)JSONFileHandler.getDataFromAssets(context, PATH_ASSET + filename, ChallengeData.class);
            }
            catch(Exception e){
                Log.e(TAG, "Asset file " + filename + " could not be read.", e);
            }

            try {
                localFile.getParentFile().mkdirs();
                localFile.createNewFile();
                JSONFileHandler.writeDataToLocalFile(context, PATH_LOCAL + filename, challengeData);
            }
            catch(Exception e){
                Log.e(TAG, "Local file " + filename + " could not be written.", e);
            }
        }
    }

    public boolean getCompleted(){
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
}
