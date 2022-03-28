package com.example.coding_app.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.amrdeveloper.codeview.CodeView;
import com.example.coding_app.R;
import com.example.coding_app.models.Judge.Judge;
import com.example.coding_app.models.Judge.JudgeData;
import com.example.coding_app.models.Judge.JudgeResponseHandler;
import com.example.coding_app.models.challenge.Challenge;
import com.example.coding_app.models.challenge.ChallengeManager;
import com.example.coding_app.models.challenge.TestCase;
import com.example.coding_app.models.language.Language;
import com.example.coding_app.models.language.LanguageManager;
import com.example.coding_app.views.ChallengeListItem;
import com.example.coding_app.views.TestCaseResult;

import java.util.Map;
import java.util.Set;

/**
 * The main environment for solving coding challenges. Provides challenge description,
 * code editor, submit button, and results of code execution
 */
public class CodingEnvironmentFragment extends Fragment implements JudgeResponseHandler {

    private Language currentLanguage;
    private Challenge currentChallenge;

    private View rootView;
    private CodeView codeView;

    public CodingEnvironmentFragment getCEFragment(){ return this; }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.coding_environment, null);

        //initialize the fragment with the appropriate challenge
        String currentChallengeName = getArguments().getString("Challenge");
        this.currentChallenge = ChallengeManager.getChallenge(currentChallengeName);

        initCodeView();
        initLanguageSpinner();
        initSubmitButton();
        initChallengeDescription();

        return rootView;
    }

    /**
     * Initialize the code editor
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initCodeView(){
        codeView = rootView.findViewById(R.id.code_view);
        codeView.setEnableLineNumber(true);
        codeView.setLineNumberTextColor(getResources().getColor(R.color.purple_200));
        codeView.setLineNumberTextSize(codeView.getTextSize());
        codeView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction()&MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_UP:
                        view.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });
    }

    /**
     * Initialize the spinner for selecting language
     */
    private void initLanguageSpinner(){
        Spinner langSpinner = rootView.findViewById(R.id.lang_spinner);
        String[] langChoices = LanguageManager.getLanguageNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, langChoices);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        langSpinner.setAdapter(adapter);

        langSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

                //save the current solution for the currently selected language
                String codeViewText = codeView.getText().toString();
                if(currentChallenge != null && currentLanguage != null && !codeViewText.equals(""))
                    currentChallenge.setSolution(currentLanguage.getName(), codeViewText);

                //then select the new language and display the appropriate solution
                currentLanguage = LanguageManager.getLanguageByName(LanguageManager.getLanguageNames()[position]);
                currentLanguage.apply(codeView);
                codeView.setText(currentChallenge.getSolution(currentLanguage.getName()));
                codeView.resetHighlighter();
                return;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    /**
     * Initialize the button used to submit code
     */
    private void initSubmitButton(){
        Button submitButton = rootView.findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitToJudge();
            }
        });
    }

    /**
     * Initialize the WebView that displays the challenge description
     */
    private void initChallengeDescription(){
        WebView challengeDescription = rootView.findViewById(R.id.challenge_description);
        if(currentChallenge != null) {
            challengeDescription.loadDataWithBaseURL(null,
                    currentChallenge.getDescriptionHTML(),
                    "text/html", "UTF-8", null);
        }
    }

    /**
     * Save challenge data when fragment is no longer active
     */
    @Override
    public void onStop(){
        super.onStop();
        if(currentChallenge != null) {
            String test = codeView.getText().toString();
            currentChallenge.setSolution(currentLanguage.getName(), codeView.getText().toString());
            currentChallenge.writeChallengeToLocalFile(getContext());
        }
    }

    /**
     * Called when user presses 'submit' button; sends code submission to the
     * Judge class
     */
    private void submitToJudge(){
        TestCase[] testCases = currentChallenge.getTestCases();
        JudgeData[] submissions = new JudgeData[testCases.length];
        String sourceCode = codeView.getText().toString();
        int languageId = currentLanguage.getID();

        for(int i=0; i<submissions.length; i++){
            submissions[i] = new JudgeData();
            submissions[i].language_id = languageId;
            submissions[i].source_code = sourceCode;
            submissions[i].stdin = testCases[i].in;
            submissions[i].expected_output = testCases[i].out;
        }

        Judge.sendSubmissionBatch(getContext(), submissions, this);
    }

    /**
     * Callback function; called by the Judge class after a code submission
     * is finished executing
     */
    public void handleJudgeResponse(JudgeData[] response){
        if(response == null) return;
        LinearLayout resultsContainer = rootView.findViewById(R.id.results_container);
        resultsContainer.removeAllViews();

        boolean allTestCasesPassed = true;

        //initialize the views for the test case results
        for(int i=0; i<response.length; i++){
            TestCaseResult tcr = new TestCaseResult(rootView.getContext(), null);
            tcr.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            tcr.setResult(response[i], i + 1);
            resultsContainer.addView(tcr);

            if(response[i].status_id != 3){
                allTestCasesPassed = false;
            }
        }

        //if all test cases passed, mark the challenge as completed
        if(allTestCasesPassed)
            currentChallenge.setCompleted(true);
    }
}