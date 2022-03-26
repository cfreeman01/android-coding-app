package com.example.coding_app.models.Judge;

import android.content.Context;
import android.os.Handler;
import android.util.Base64;
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

    /**
     Add necessary headers to the AsyncHttpClient
     */
    public static void init(Context context){
        client.addHeader("content-type", "application/json");
        client.addHeader("x-rapidapi-host", HOST);
        client.addHeader("x-rapidapi-key", AUTH_TOKEN);
    }

    /**
     Send a batch of code submissions to Judge. Once all of the submissions are processed,
     the results are passed back to the JudgeResponseHandler
     */
    public static void sendSubmissionBatch(Context context, JudgeData[] submissions, JudgeResponseHandler jrh){
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

        client.post(context,
                BASE_URL + "/submissions/batch/?base64_encoded=false&wait=false",
                entity,
                "application/json",
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String responseString = new String(responseBody);
                        Gson gson = new Gson();
                        JudgeData[] tokens = gson.fromJson(responseString, JudgeData[].class);
                        getSubmissionBatch(context, tokens, jrh);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        String responseString = new String(responseBody);
                        Log.e(TAG, "Judge POST failed: " + responseString);
                    }
                });
    }

    /**
     Called by sendSubmissionBatch. Repeatedly requests the results of the submission batch, and once
     they are completed the results are passed back to the JudgeResponseHandler
     */
    private static void getSubmissionBatch(Context context, JudgeData[] tokens, JudgeResponseHandler jrh){

        //construct tokens string
        if(tokens == null) return;
        String token_string = "";
        for(int i=0; i<tokens.length-1; i++)
            token_string += tokens[i].token + ",";
        token_string += tokens[tokens.length-1].token;

        client.get(context,
                BASE_URL + "/submissions/batch?tokens=" + token_string + "&base64_encoded=true&fields=*",
                null,
                "application/json",
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String responseString = new String(responseBody);
                        Gson gson = new Gson();
                        JudgeBatch response = gson.fromJson(responseString, JudgeBatch.class);
                        JudgeData[] results = response.submissions;
                        if(results == null) return;

                        //check if all submissions are finished executing
                        boolean allFinished = true;
                        for(int i=0; i<results.length; i++){
                            if(results[i].status_id < 3){
                                allFinished = false;
                                break;
                            }
                        }

                        if(allFinished) {             //if all submissions finished, process the results

                            //convert from base64
                            for(JudgeData result: results) {
                                if(result.stdin != null)
                                    result.stdin = new String(Base64.decode(result.stdin, Base64.NO_WRAP));
                                if(result.stdout != null)
                                    result.stdout = new String(Base64.decode(result.stdout, Base64.NO_WRAP));
                                if(result.compile_output != null)
                                    result.compile_output = new String(Base64.decode(result.compile_output, Base64.NO_WRAP));
                                if(result.stderr != null)
                                    result.stderr = new String(Base64.decode(result.stderr, Base64.NO_WRAP));
                                if(result.expected_output != null)
                                    result.expected_output = new String(Base64.decode(result.expected_output, Base64.NO_WRAP));
                            }

                            //return results to response handler
                            jrh.handleJudgeResponse(results);
                        }
                        else{                         //otherwise, try again after a delay
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable(){
                                @Override
                                public void run() {
                                    getSubmissionBatch(context, tokens, jrh);
                                }
                            }, 100);
                            getSubmissionBatch(context, tokens, jrh);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        String responseString = new String(responseBody);
                        Log.e(TAG, "Judge GET failed: " + responseString);
                    }
                });
    }
}
