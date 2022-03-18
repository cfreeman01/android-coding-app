package com.example.coding_app.models.language;

import android.content.Context;

import com.amrdeveloper.codeview.CodeView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Static class for managing all of the available
 * programming languages
 */
public class LanguageManager {
    private static Map<String, Language> languages = new HashMap<String, Language>();

    //initialize language data
    public static void init(Context context){
        languages.put("C", new Language(context, "c.json"));
        languages.put("C++", new Language(context, "cpp.json"));
        languages.put("Go", new Language(context, "go.json"));
        languages.put("Java", new Language(context, "java.json"));
        languages.put("Python 3", new Language(context, "python3.json"));

        for(Map.Entry<String, Language> entry: languages.entrySet()){
            entry.getValue().fillPatternsMap(context);
        }
    }

    public static Language getLanguageByName(String name){
        if(!languages.containsKey(name)) return null;
        return languages.get(name);
    }

    public static Language[] getLanguages(){
        Language[] langs = languages.values().toArray(new Language[0]);
        return langs;
    }

    public static String[] getLanguageNames(){
        String[] langs = languages.keySet().toArray(new String[0]);
        Arrays.sort(langs);
        return langs;
    }
}
