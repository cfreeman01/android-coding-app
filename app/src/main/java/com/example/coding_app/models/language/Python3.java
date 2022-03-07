package com.example.coding_app.models.language;

import android.content.Context;

import com.amrdeveloper.codeview.CodeView;
import com.example.coding_app.R;

import java.util.regex.Pattern;

public class Python3 extends Language{

    private static final Pattern PATTERN_KEYWORDS = Pattern.compile("\\b(False|await|else|import|pass|None|break|except|in|raise" +
            "|True|class|finally|is|return|and|continue|for|lambda" +
            "|try|as|def|from|nonlocal|while|assert|del|global|not" +
            "|with|async|elif|if|or|yield)\\b");

    private static final Pattern PATTERN_BUILTINS = Pattern.compile("[,:;[->]{}()]");
    private static final Pattern PATTERN_NUMBERS = Pattern.compile("\\b(\\d*[.]?\\d+)\\b");
    private static final Pattern PATTERN_CHAR = Pattern.compile("'[a-zA-Z]'");
    private static final Pattern PATTERN_STRING = Pattern.compile("\".*\"");
    private static final Pattern PATTERN_HEX = Pattern.compile("0x[0-9a-fA-F]+");
    private static final Pattern PATTERN_TODO_COMMENT = Pattern.compile("#TODO[^\n]*");
    private static final Pattern PATTERN_ATTRIBUTE = Pattern.compile("\\.[a-zA-Z0-9_]+");
    private static final Pattern PATTERN_OPERATION =Pattern.compile( ":|==|>|<|!=|>=|<=|->|=|>|<|%|-|-=|%=|\\+|\\-|\\-=|\\+=|\\^|\\&|\\|::|\\?|\\*");
    private static final Pattern PATTERN_HASH_COMMENT = Pattern.compile("#(?!TODO )[^\\n]*");

    public Python3(Context context){
        super(context);
        name = "Python 3";
        judgeId = 71;

        highlighter.put(PATTERN_HEX, context.getResources().getColor(R.color.monokia_pro_purple));
        highlighter.put(PATTERN_CHAR, context.getResources().getColor(R.color.monokia_pro_green));
        highlighter.put(PATTERN_STRING, context.getResources().getColor(R.color.monokia_pro_orange));
        highlighter.put(PATTERN_NUMBERS, context.getResources().getColor(R.color.monokia_pro_purple));
        highlighter.put(PATTERN_KEYWORDS, context.getResources().getColor(R.color.monokia_pro_pink));
        highlighter.put(PATTERN_BUILTINS, context.getResources().getColor(R.color.monokia_pro_white));
        highlighter.put(PATTERN_HASH_COMMENT, context.getResources().getColor(R.color.monokia_pro_grey));
        highlighter.put(PATTERN_ATTRIBUTE, context.getResources().getColor(R.color.monokia_pro_sky));
        highlighter.put(PATTERN_OPERATION, context.getResources().getColor(R.color.monokia_pro_pink));
    }

    public void apply(CodeView codeView){
        codeView.resetSyntaxPatternList();
        codeView.setSyntaxPatternsMap(highlighter);
        codeView.setTabLength(2);
    }

    public String getDefaultCode(){
        return "print(\"Hello world!\")";
    }
}
