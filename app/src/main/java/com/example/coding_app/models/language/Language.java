package com.example.coding_app.models.language;

import android.content.Context;

import com.amrdeveloper.codeview.CodeView;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class Language {
    protected String name;
    protected int judgeId;
    protected Map<Pattern, Integer> highlighter = new HashMap<Pattern, Integer>();

    public Language(Context context){}

    public abstract void apply(CodeView codeView);
    public abstract String getDefaultCode();
    public int getId(){ return judgeId; }

}
