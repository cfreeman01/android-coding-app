package com.example.coding_app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.amrdeveloper.codeview.CodeView;
import com.example.coding_app.R;
import com.example.coding_app.models.Judge.Judge;
import com.example.coding_app.models.Judge.JudgeData;
import com.example.coding_app.models.challenge.Challenge;
import com.example.coding_app.models.challenge.ChallengeManager;
import com.example.coding_app.models.language.Language;
import com.example.coding_app.models.language.LanguageManager;

public class CodingEnvironmentFragment extends Fragment {

    private static Language currentLanguage;
    private static Challenge currentChallenge;

    private static View rootView;
    private static CodeView codeView;
    private static Spinner langSpinner;
    private static Button submitButton;
    private static WebView challengeDescription;

    public CodingEnvironmentFragment getCEFragment(){ return this; }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.coding_environment, null);

        //initialize the fragment with the appropriate challenge
        String currentChallengeName = getArguments().getString("Challenge");
        this.currentChallenge = ChallengeManager.getChallenge(currentChallengeName);

        //initialize code view
        codeView = rootView.findViewById(R.id.code_view);
        codeView.setEnableLineNumber(true);
        codeView.setLineNumberTextColor(getResources().getColor(R.color.purple_200));
        codeView.setLineNumberTextSize(codeView.getTextSize());

        initLanguageSpinner();
        initSubmitButton();
        initChallengeDescription();

        return rootView;
    }

    //initialize the spinner for selecting language
    private void initLanguageSpinner(){
        langSpinner = rootView.findViewById(R.id.lang_spinner);
        String[] langChoices = LanguageManager.getLanguageNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, langChoices);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        langSpinner.setAdapter(adapter);

        langSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
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

    //initialize the button used to submit code
    private void initSubmitButton(){
        submitButton = rootView.findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Judge.isProcessingRequest())
                    return;
                String temp = codeView.getText().toString();
                Judge.createSubmission(getCEFragment(),
                        codeView.getText().toString(),
                        currentLanguage.getID(),
                        "");
            }
        });
    }

    //initialize the webview for the challenge description
    private void initChallengeDescription(){
        challengeDescription = rootView.findViewById(R.id.challenge_description);
        if(currentChallenge != null) {
            challengeDescription.loadDataWithBaseURL(null,
                    currentChallenge.getDescriptionHTML(),
                    "text/html", "UTF-8", null);
        }
    }

    public void handleJudgeResponse(JudgeData response){
        codeView.append("\n" + response.stdout);
    }
}

