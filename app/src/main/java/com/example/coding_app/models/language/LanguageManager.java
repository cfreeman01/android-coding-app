package com.example.coding_app.models.language;

import android.content.Context;

import com.amrdeveloper.codeview.CodeView;
import com.example.coding_app.models.language.C;
import com.example.coding_app.models.language.Java;
import com.example.coding_app.models.language.Language;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LanguageManager {
    private static Map<String, Language> languages = new HashMap<String, Language>();

    //initialize language data
    public static void init(Context context){
        languages.put("C", new C(context));
        languages.put("C++", new Cpp(context));
        languages.put("Go", new Go(context));
        languages.put("Java", new Java(context));
        languages.put("Python 3", new Python3(context));
    }

    //apply syntax highlighting for a specific language
    public static void apply(String langName, CodeView codeView){
        if(!languages.containsKey(langName))
            return;

        languages.get(langName).apply(codeView);
    }

    public static String[] getLanguages(){
        String[] langs = languages.keySet().toArray(new String[0]);
        Arrays.sort(langs);
        return langs;
    }

    public static int getLanguageId(String langName){
        for(Map.Entry<String, Language> entry: languages.entrySet()){
            if(langName.equals(entry.getKey()))
                return entry.getValue().getId();
        }
        return -1;
    }

    public static String getDefaultCode(String langName){
        for(Map.Entry<String, Language> entry: languages.entrySet()){
            if(langName.equals(entry.getKey()))
                return entry.getValue().getDefaultCode();
        }
        return null;
    }
}
