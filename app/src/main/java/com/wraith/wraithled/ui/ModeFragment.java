package com.wraith.wraithled.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wraith.wraithled.R;
import com.wraith.wraithled.classes.Mood;
import com.wraith.wraithled.classes.MoodCardGenerator;
import com.wraith.wraithled.classes.MoodesHandler;
import com.wraith.wraithled.classes.RGBStrip;
import com.wraith.wraithled.classes.Utils;
import com.wraith.wraithled.interfaces.SyncDataInterface;
import com.wraith.wraithled.interfaces.UIInterface_Fragment;

public class ModeFragment extends Fragment implements View.OnClickListener, UIInterface_Fragment
{

    private RGBStrip strip;
    private MoodesHandler moodesHandler;
    private SyncDataInterface listener;

    private ConstraintLayout modeLayout;


    public ModeFragment newInstance(RGBStrip strip, MoodesHandler moodesHandler)
    {
        Bundle bundle = new Bundle();
        ModeFragment fragment = new ModeFragment();

        bundle.putSerializable(Utils.RGB_STRIP_OBJ_NAME, strip);
        bundle.putSerializable(Utils.MOOD_MODES_OBJ_NAME, moodesHandler);

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_mode, container, false);


        loadUIVariables(view);
        createModeCardsUI(view);
        return view;
    }

    private void createModeCardsUI(View view)
    {

        Mood element;
        CardView[] cards;
        int idOfPrev = R.id.modeText;
        Context context = getContext();

        for(int i = 0; i < moodesHandler.getMoodCards().size();)
        {
            element = moodesHandler.getMoodCards().get(i);
            MoodCardGenerator row = new MoodCardGenerator(context, modeLayout, moodesHandler.getMoodCards(), i);
            row.createLayout(idOfPrev);

            cards = row.getCards();
            idOfPrev = row.getRowID();

            for (CardView card : cards)
                card.setOnClickListener(this);

            if(element.isStack())
                i += 2;
            else
                i += 1;
        }

        updateFragmentUI(view);
    }

    private void updateFragmentUI(View view)
    {
        if(strip.getModeType() != RGBStrip.MODE_MODES)
            return;

        updateCardState(view, strip.getMoodMode(), true);
    }

    private void updateCardState(View view, int moodeID, boolean selected)
    {
        Mood findCard = moodesHandler.getMoodCards().stream().filter(mood -> mood.getID() == moodeID).findFirst().orElse(null);

        if(findCard == null)
            return;

        CardView card = view.findViewById(findCard.getViewID());
        card.setBackgroundColor(getResources().getColor(!selected ? R.color.cardItem : R.color.cardItemSelected));
        card.invalidate();
    }

    @Override
    public void onClick(View view)
    {

        int viewID = view.getId();
        Mood findCard = moodesHandler.getMoodCards().stream().filter(mood -> mood.getViewID() == viewID).findFirst().orElse(null);

        if(findCard == null || (strip.getModeType() == RGBStrip.MODE_MODES && strip.getMoodMode() == findCard.getID()))
            return;

        if(strip.getModeType() == RGBStrip.MODE_MODES)
            updateCardState(getView(), strip.getMoodMode(), false);

        strip.setMoodMode(findCard.getID());
        strip.setModeType(RGBStrip.MODE_MODES);
        updateCardState(getView(), strip.getMoodMode(), true);

        if(strip.sync())
            sendDataToActivity();
    }

    @Override
    public void loadUIVariables(View view)
    {
        strip = (RGBStrip) getArguments().getSerializable(Utils.RGB_STRIP_OBJ_NAME);
        moodesHandler = (MoodesHandler) getArguments().getSerializable(Utils.MOOD_MODES_OBJ_NAME);

        modeLayout = view.findViewById(R.id.modeLayout);
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);

        if (context instanceof SyncDataInterface)
            listener = (SyncDataInterface) context;
    }
    @Override
    public void onDetach()
    {
        listener = null;
        super.onDetach();
    }
    private void sendDataToActivity() { listener.syncStripMainClass(strip); }
}