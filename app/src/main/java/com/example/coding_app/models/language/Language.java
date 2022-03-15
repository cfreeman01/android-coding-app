package com.example.coding_app.models.language;

import android.content.Context;
import android.util.Log;

import com.amrdeveloper.codeview.CodeView;
import com.example.coding_app.models.JSONFileHandler;
import com.example.coding_app.models.challenge.ChallengeData;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Pattern;

public class Language {

    private static final String TAG = "Langage";
    private static final String PATH_ASSET = "language/";

    private String filename;
    private LanguageData languageData;

    private Map<Pattern, Integer> highlighterPatterns = new HashMap<Pattern, Integer>();

    public Language(Context context, String filename){
        this.filename = filename;
        File localFile = new File(context.getFilesDir(), PATH_ASSET + filename);

        //get language data from assets
        try {
            languageData = (LanguageData) JSONFileHandler.getDataFromAssets(context, PATH_ASSET + filename, LanguageData.class);
        }
        catch(Exception e){
            Log.e(TAG, "Local file " + filename + " could not be read.", e);
        }
    }

    public int getID(){
        return languageData.judge_id;
    }

    public String getName(){
        return languageData.name;
    }

    public void fillPatternsMap(Context context){
        for(Map.Entry<String, String> entry: languageData.highlighter.entrySet()){
            int colorResource = context.getResources().getIdentifier(entry.getValue(), "color", context.getPackageName());
            int colorValue = context.getResources().getColor(colorResource);
            highlighterPatterns.put(
                    Pattern.compile(entry.getKey()),
                    colorValue
            );
        }
    }

    public void apply(CodeView codeView){
        HashSet<Character> indentationStarts = new HashSet<Character>();
        indentationStarts.add('{');
        HashSet<Character> indentationEnds = new HashSet<Character>();
        indentationStarts.add('}');

        codeView.resetSyntaxPatternList();
        codeView.setSyntaxPatternsMap(highlighterPatterns);
        codeView.setIndentationStarts(indentationStarts);
        codeView.setIndentationEnds(indentationEnds);
        codeView.setTabLength(2);
        codeView.setEnableAutoIndentation(true);
    }
}
