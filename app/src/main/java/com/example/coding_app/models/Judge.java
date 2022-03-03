package com.example.coding_app.models;

import android.content.Context;

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
    private static JudgeLanguage[] languages;
    private static JudgeStatus[] statuses;

    //add necessary headers to async http client
    public static void init(){
        //add headers
        client.addHeader("content-type", "application/json");
        client.addHeader("x-rapidapi-host", HOST);
        client.addHeader("x-rapidapi-key", AUTH_TOKEN);

        //get languages

        //get statuses
    }

    //Send a single code submission to Judge
    public static void createSubmission(Context context, String sourceCode, int languageID, String input){
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

        client.post(context,
                BASE_URL + "/submissions/?base64_encoded=false&wait=false",
                entity,
                "application/json",
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String responseString = new String(responseBody);
                        Gson gson = new Gson();
                        lastResponse = gson.fromJson(responseString, JudgeData.class);
                        return;
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        if(responseBody != null){
                        }
                        return;
                    }
                });
    }

    public static void getSubmission(Context context){
        if(lastResponse == null) return;

        JSONObject params = new JSONObject();
        StringEntity entity;
        try {
            entity = new StringEntity(params.toString());
        }
        catch(Exception e){
            e.printStackTrace();
            return;
        }

        client.get(context,
                BASE_URL + "/submissions/" + lastResponse.token + "?base64_encoded=false&wait=false&fields=stdout,stderr,status_id,language_id",
                entity,
                "application/json",
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String responseString = new String(responseBody);
                        Gson gson = new Gson();
                        JudgeData response = gson.fromJson(responseString, JudgeData.class);
                        return;
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        if(responseBody != null){
                            String responseString = new String(responseBody);
                            Gson gson = new Gson();
                            JudgeData response = gson.fromJson(responseString, JudgeData.class);
                        }
                        return;
                    }
                });
    }
}
