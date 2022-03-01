package com.example.coding_app.fragments;

import androidx.fragment.app.Fragment;
import com.example.coding_app.R;
import com.example.coding_app.models.Judge;

public class CodingEnvironmentFragment extends Fragment {

    static final String ex_code = "#include <stdio.h> \n" +
            "int main(void){ printf(\"epic\") };";

    public CodingEnvironmentFragment() {
        super(R.layout.coding_environment);
        Judge.init();
        Judge.createSubmission(ex_code, 48, "");
    }
}

