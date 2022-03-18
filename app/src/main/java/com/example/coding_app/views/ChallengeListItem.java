package com.example.coding_app.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.coding_app.R;
import com.example.coding_app.models.challenge.Challenge;

/**
 * Selectable list item for a coding challenge.
 */
public class ChallengeListItem extends FrameLayout {

    private Challenge challenge;
    private Context context;
    private View rootView;
    private TextView challengeTitle;
    private TextView difficultyLevel;

    public ChallengeListItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        rootView = inflate(context, R.layout.challenge_list_item, this);
        challengeTitle = rootView.findViewById(R.id.challenge_title);
        difficultyLevel = rootView.findViewById(R.id.difficulty_level);
    }

    /**
     * Initializes the view by passing in the appropriate challenge data
     */
    public void setChallenge(Challenge challenge){
        this.challenge = challenge;
        challengeTitle.setText(challenge.getName());

        String dl = challenge.getDifficultyLevel();
        difficultyLevel.setText(dl);
        if(dl.equals("Easy")){
            difficultyLevel.setTextColor(context.getResources().getColor(R.color.monokia_pro_green));
        }
        else if(dl.equals("Medium")){
            difficultyLevel.setTextColor(context.getResources().getColor(R.color.monokia_pro_orange));
        }
        else{
            difficultyLevel.setTextColor(context.getResources().getColor(R.color.monokia_pro_red));
        }
    }

    public Challenge getChallenge(){
        return challenge;
    }

    @Override
    public void setOnClickListener(OnClickListener l){
        rootView.findViewById(R.id.constraint_layout).setOnClickListener(l);
    }
}
