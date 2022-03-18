package com.example.coding_app.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.coding_app.R;
import com.example.coding_app.models.Judge.JudgeData;

public class TestCaseResult extends FrameLayout {

    private int testCaseNum;
    private JudgeData result;
    private Context context;
    private View rootView;

    private TextView resultHeader;
    private TextView resultDetails;
    private int resultColor;

    public TestCaseResult(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        rootView = inflate(context, R.layout.test_case_result, this);
        resultDetails = rootView.findViewById(R.id.result_details);
        resultHeader = rootView.findViewById(R.id.result_header);
    }

    public void setResult(JudgeData result, int testCaseNum){
        this.result = result;
        this.testCaseNum = testCaseNum;
        String resultString;

        //status id 3: success
        if(result.status_id == 3){
            resultColor = context.getResources().getColor(R.color.monokia_pro_green);
            resultHeader.setText("Test Case " + testCaseNum + ": Success");
            resultString = "Input: " + result.stdin;
            resultString += "\n\nExpected Output: " + result.expected_output;
            resultString += "\n\nOutput: " + result.stdout;
        }

        //status id 4: wrong output
        else if(result.status_id == 4){
            resultColor = context.getResources().getColor(R.color.monokia_pro_red);
            resultHeader.setText("Test Case " + testCaseNum + ": Wrong Answer");
            resultString = "Input: " + result.stdin;
            resultString += "\n\nExpected Output: " + result.expected_output;
            resultString += "\n\nOutput: " + result.stdout;
        }

        //status id 5: time limit exceeded
        else if(result.status_id == 5){
            resultColor = context.getResources().getColor(R.color.monokia_pro_red);
            resultHeader.setText("Test Case " + testCaseNum + ": Wrong Answer");
            resultString = "Time limit exceeded.";
        }

        //any higher id: error
        else{
            resultColor = context.getResources().getColor(R.color.monokia_pro_red);
            resultHeader.setText("Test Case " + testCaseNum + ": Error");
            resultString = result.compile_output;
        }

        resultHeader.setTextColor(resultColor);
        resultDetails.setTextColor(resultColor);
        resultDetails.setText(resultString);
    }
}
