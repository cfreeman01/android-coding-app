package com.example.coding_app.models;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.Map;

import cz.msebera.android.httpclient.Header;

/**
 * Static class for making HTTP requests to Judge0 API
 */
public class Judge {
    private static final String BASE_URL = "https://judge0-ce.p.rapidapi.com/";
    private static final String HOST = "judge0-ce.p.rapidapi.com";
    private static final String AUTH_TOKEN = "5b0ed02bb9mshad23e19878c65f8p1c7dd5jsn145419a8f43f";

    private static AsyncHttpClient client = new AsyncHttpClient();
    private static String currentToken = "";

    //add necessary headers to async http client
    public static void init(){
        client.addHeader("x-rapidapi-host", HOST);
        client.addHeader("x-rapidapi-key", AUTH_TOKEN);
    }

    //Send a single code submission to Judge
    public static void createSubmission(String sourceCode, int languageID, String input){
        RequestParams params = new RequestParams();
        params.put("source_code", sourceCode);
        params.put("language_id", languageID);
        params.put("stdin", input);

        client.post(BASE_URL + "/submissions/?base64_encoded=false&wait=false",
                params,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String responseString = new String(responseBody);
                        Gson gson = new Gson();
                        JudgeResponse response = gson.fromJson(responseString, JudgeResponse.class);
                        return;
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        int x = 2;
                        return;
                    }
                });
    }
}
