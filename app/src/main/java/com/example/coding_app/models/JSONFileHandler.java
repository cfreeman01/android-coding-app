package com.example.coding_app.models;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

public class JSONFileHandler {

    static Gson gson = new GsonBuilder()
            .enableComplexMapKeySerialization()
            .setPrettyPrinting()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    public static Object getDataFromLocalFile(Context context, String path, Class dataType) throws IOException{
        File localFile = new File(context.getFilesDir(), path);
        FileReader reader = new FileReader(localFile);

        int read = '0';

        String jsonString = "";
        while((read = reader.read()) != -1){
            jsonString += (char)read;
        }

        return gson.fromJson(jsonString, dataType);
    }

    public static Object getDataFromAssets(Context context, String path, Class dataType) throws IOException {
        InputStream is = context.getAssets().open(path, MODE_PRIVATE);

        int readResult = 0;
        int numRead = 0;
        byte[] assetData = new byte[16384];

        while ((readResult = is.read(assetData, 0, assetData.length)) != -1) {
            numRead = readResult;
        }
        String jsonString = new String(assetData, 0, numRead, "UTF-8");

        return gson.fromJson(jsonString, dataType);
    }

    public static void writeDataToLocalFile(Context context, String path, Object data) throws IOException{
        File localFile = new File(context.getFilesDir(), path);
        FileWriter writer = new FileWriter(localFile, false);

        String jsonString = gson.toJson(data);

        writer.write(jsonString, 0, jsonString.length());
        writer.flush();
        writer.close();
    }
}
