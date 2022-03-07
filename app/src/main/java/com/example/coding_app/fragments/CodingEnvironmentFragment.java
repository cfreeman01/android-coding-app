package com.example.coding_app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.amrdeveloper.codeview.CodeView;
import com.example.coding_app.R;
import com.example.coding_app.models.Judge.Judge;
import com.example.coding_app.models.Judge.JudgeData;
import com.example.coding_app.models.language.LanguageManager;

public class CodingEnvironmentFragment extends Fragment {

    private static String currentLanguage = LanguageManager.getLanguages()[0];

    private static View rootView;
    private static CodeView codeView;
    private static Spinner langSpinner;
    private static Button submitButton;

    public CodingEnvironmentFragment() {
    }

    public CodingEnvironmentFragment getCEFragment(){ return this; }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.coding_environment, null);

        //initialize code view
        codeView = rootView.findViewById(R.id.code_view);
        codeView.setEnableLineNumber(true);
        codeView.setLineNumberTextColor(getResources().getColor(R.color.purple_200));
        codeView.setLineNumberTextSize(codeView.getTextSize());
        LanguageManager.apply(currentLanguage, codeView);

        initLanguageSpinner();
        initSubmitButton();

        return rootView;
    }

    //initialize the spinner for selecting language
    private void initLanguageSpinner(){
        langSpinner = rootView.findViewById(R.id.lang_spinner);
        String[] langChoices = LanguageManager.getLanguages();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, langChoices);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        langSpinner.setAdapter(adapter);

        langSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                currentLanguage = LanguageManager.getLanguages()[position];
                LanguageManager.apply(currentLanguage, codeView);
                codeView.setText(LanguageManager.getDefaultCode(currentLanguage));
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
                        LanguageManager.getLanguageId(currentLanguage),
                        "");
            }
        });
    }

    public void handleJudgeResponse(JudgeData response){
        codeView.append("\n" + response.stdout);
    }
}

