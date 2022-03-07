package com.example.coding_app.models.language;

import android.content.Context;

import com.amrdeveloper.codeview.CodeView;
import com.example.coding_app.R;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class Go extends Language{

    private static final Pattern PATTERN_KEYWORDS = Pattern.compile("\\b(break|default|func|interface|case|defer|" +
            "go|map|struct|chan|else|goto|package|switch|const" +
            "|fallthrough|if|range|type|continue|for|import|return|var|" +
            "string|true|false|new|nil|byte|bool|int|int8|int16|int32|int64)\\b");

    private static final Pattern PATTERN_BUILTINS = Pattern.compile("[,:;[->]{}()]");

    private static final Pattern PATTERN_NUMBERS = Pattern.compile("\\b(\\d*[.]?\\d+)\\b");
    private static final Pattern PATTERN_CHAR = Pattern.compile("'[a-zA-Z]'");
    private static final Pattern PATTERN_STRING = Pattern.compile("\".*\"");
    private static final Pattern PATTERN_HEX = Pattern.compile("0x[0-9a-fA-F]+");
    private static final Pattern PATTERN_COMMENT = Pattern.compile("//(?!TODO )[^\\n]*" + "|" + "/\\*(.|\\R)*?\\*/");
    private static final Pattern PATTERN_ATTRIBUTE = Pattern.compile("\\.[a-zA-Z0-9_]+");
    private static final Pattern PATTERN_OPERATION =Pattern.compile( ":|==|>|<|!=|>=|<=|->|=|>|<|%|-|-=|%=|\\+|\\-|\\-=|\\+=|\\^|\\&|\\|::|\\?|\\*");

    private static Set<Character> indentationStarts = new HashSet<Character>();
    private static Set<Character> indentationEnds = new HashSet<Character>();

    public Go(Context context){
        super(context);
        name = "Go";
        judgeId = 60;

        highlighter.put(PATTERN_HEX, context.getResources().getColor(R.color.monokia_pro_purple));
        highlighter.put(PATTERN_CHAR, context.getResources().getColor(R.color.monokia_pro_green));
        highlighter.put(PATTERN_STRING, context.getResources().getColor(R.color.monokia_pro_orange));
        highlighter.put(PATTERN_NUMBERS, context.getResources().getColor(R.color.monokia_pro_purple));
        highlighter.put(PATTERN_KEYWORDS, context.getResources().getColor(R.color.monokia_pro_pink));
        highlighter.put(PATTERN_BUILTINS, context.getResources().getColor(R.color.monokia_pro_white));
        highlighter.put(PATTERN_COMMENT, context.getResources().getColor(R.color.monokia_pro_grey));
        highlighter.put(PATTERN_ATTRIBUTE, context.getResources().getColor(R.color.monokia_pro_sky));
        highlighter.put(PATTERN_OPERATION, context.getResources().getColor(R.color.monokia_pro_pink));

        indentationStarts.add('{');
        indentationEnds.add('}');
    }

    public void apply(CodeView codeView){
        codeView.resetSyntaxPatternList();
        codeView.setSyntaxPatternsMap(highlighter);
        codeView.setIndentationStarts(indentationStarts);
        codeView.setIndentationEnds(indentationEnds);
        codeView.setTabLength(2);
        codeView.setEnableAutoIndentation(true);
    }

    public String getDefaultCode(){
        return "package main\n" +
                "\n" +
                "import \"fmt\"\n" +
                "\n" +
                "func main() {\n" +
                "    fmt.Println(\"Hello world!\")\n" +
                "}";
    }
}
