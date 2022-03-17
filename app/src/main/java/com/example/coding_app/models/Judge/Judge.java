package com.example.coding_app.models.Judge;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.example.coding_app.fragments.CodingEnvironmentFragment;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Static class for making HTTP requests to Judge0 API
 */
public class Judge {
    private static final String TAG = "Judge";
    private static final String BASE_URL = "https://judge0-ce.p.rapidapi.com";
    private static final String HOST = "judge0-ce.p.rapidapi.com";
    private static final String AUTH_TOKEN = "5b0ed02bb9mshad23e19878c65f8p1c7dd5jsn145419a8f43f";

    private static AsyncHttpClient client = new AsyncHttpClient();
    private static JudgeData[] lastResponse = null;

    //add necessary headers to async http client
    public static void init(Context context){
        //add headers
        client.addHeader("content-type", "application/json");
        client.addHeader("x-rapidapi-host", HOST);
        client.addHeader("x-rapidapi-key", AUTH_TOKEN);
    }

    public static boolean isProcessingRequest(){
        return (lastResponse != null);
    }

    //Send a single code submission to Judge
    public static void createSubmissionBatch(CodingEnvironmentFragment cef, JudgeData[] submissions){
        if(isProcessingRequest()) return;

        //create the JSON string to send
        Gson gson = new Gson();
        JudgeBatch batch = new JudgeBatch();
        batch.submissions = submissions;
        StringEntity entity;

        try {
            entity = new StringEntity(gson.toJson(batch));
        }
        catch(Exception e){
            Log.e(TAG, "String entity could not be constructed.", e);
            return;
        }

        client.post(cef.getContext(),
                BASE_URL + "/submissions/batch/?base64_encoded=false&wait=false",
                entity,
                "application/json",
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String responseString = new String(responseBody);
                        Gson gson = new Gson();
                        lastResponse = gson.fromJson(responseString, JudgeData[].class);
                        getSubmissionBatch(cef);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.e(TAG, "Judge submission failed.");
                    }
                });
    }

    public static void getSubmissionBatch(CodingEnvironmentFragment cef){
        if(!isProcessingRequest()) return;

        //construct tokens string
        String tokens = "";
        for(int i=0; i<lastResponse.length-1; i++)
            tokens += lastResponse[i].token + ",";
        tokens += lastResponse[lastResponse.length-1].token;

        client.get(cef.getContext(),
                BASE_URL + "/submissions/batch?tokens=" + tokens + "&base64_encoded=false&fields=*",
                null,
                "application/json",
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String responseString = new String(responseBody);
                        Gson gson = new Gson();
                        JudgeBatch response = gson.fromJson(responseString, JudgeBatch.class);
                        JudgeData[] results = response.submissions;

                        boolean allFinished = true;
                        for(int i=0; i<results.length; i++){
                            if(results[i].status_id < 3){
                                allFinished = false;
                                break;
                            }
                        }

                        if(allFinished) {             //if all submissions finished, process the results
                            lastResponse = null;
                            cef.handleJudgeResponse(results);
                        }
                        else{                         //otherwise, try again after a delay
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable(){
                                @Override
                                public void run() {
                                    getSubmissionBatch(cef);
                                }
                            }, 100);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.e(TAG, "Judge get failed.");
                    }
                });
    }
}
