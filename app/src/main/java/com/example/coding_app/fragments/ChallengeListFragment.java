package com.example.coding_app.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import com.example.coding_app.R;
import com.example.coding_app.activities.MainActivity;
import com.example.coding_app.models.challenge.Challenge;
import com.example.coding_app.models.challenge.ChallengeManager;
import com.example.coding_app.views.ChallengeListItem;

/**
 * Displays the list of available coding challenges
 */
public class ChallengeListFragment extends Fragment {

    private View rootView;
    private LinearLayout list_container;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.challenge_list_layout, null);
        list_container = rootView.findViewById(R.id.list_container);
        fillList();
        return rootView;
    }

    /**
     * Fill the layout with selectable items for the challenges
     */
    private void fillList(){
        boolean alternate_background = false;

        for(Challenge ch: ChallengeManager.getChallenges()){

            //create the view
            ChallengeListItem cli = new ChallengeListItem(rootView.getContext(), null);
            cli.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            cli.setChallenge(ch);
            if(alternate_background)
                cli.setBackground(rootView.getContext().getDrawable(R.color.light_grey));
            else
                cli.setBackground(rootView.getContext().getDrawable(R.color.med_grey));
            alternate_background = !alternate_background;

            /* on click: replace main fragment with a coding environment fragment,
            passing the appropriate challenge as an argument */
            cli.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.setBackground(getResources().getDrawable(R.color.purple_200));
                    Bundle args = new Bundle();
                    args.putString("Challenge", cli.getChallenge().getName());
                    ((MainActivity)getActivity()).replaceMainFragment(CodingEnvironmentFragment.class, args);
                }
            });

            //add new ChallengeListItem to the layout
            list_container.addView(cli);
        }
    }
}
