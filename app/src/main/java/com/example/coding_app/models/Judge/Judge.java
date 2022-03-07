package com.example.coding_app.models.Judge;

import android.content.Context;
import android.os.Handler;

import com.example.coding_app.fragments.CodingEnvironmentFragment;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Static class for making HTTP requests to Judge0 API
 */
public class Judge {
    private static final String BASE_URL = "https://judge0-ce.p.rapidapi.com";
    private static final String HOST = "judge0-ce.p.rapidapi.com";
    private static final String AUTH_TOKEN = "5b0ed02bb9mshad23e19878c65f8p1c7dd5jsn145419a8f43f";

    private static AsyncHttpClient client = new AsyncHttpClient();
    private static JudgeData lastResponse;

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
    public static void createSubmission(CodingEnvironmentFragment cef, String sourceCode, int languageID, String input){
        JSONObject params = new JSONObject();
        StringEntity entity;
        try {
            params.put("source_code", sourceCode);
            params.put("language_id", languageID);
            params.put("stdin", input);
            entity = new StringEntity(params.toString());
        }
        catch(Exception e){
            e.printStackTrace();
            return;
        }

        client.post(cef.getContext(),
                BASE_URL + "/submissions/?base64_encoded=false&wait=false",
                entity,
                "application/json",
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String responseString = new String(responseBody);
                        Gson gson = new Gson();
                        lastResponse = gson.fromJson(responseString, JudgeData.class);
                        getSubmission(cef);
                        return;
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        lastResponse = null;
                    }
                });
    }

    public static void getSubmission(CodingEnvironmentFragment cef){
        if(lastResponse == null) return;

        client.get(cef.getContext(),
                BASE_URL + "/submissions/" + lastResponse.token + "?base64_encoded=false&wait=false&fields=*",
                null,
                "application/json",
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String responseString = new String(responseBody);
                        Gson gson = new Gson();
                        JudgeData response = gson.fromJson(responseString, JudgeData.class);

                        if(response.status_id >= 3){  //if request is finished processing
                            cef.handleJudgeResponse(response);
                            lastResponse = null;
                        }
                        else{                         //otherwise, try again after a delay
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable(){
                                @Override
                                public void run() {
                                    getSubmission(cef);
                                }
                            }, 100);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        lastResponse = null;
                    }
                });
    }
}
