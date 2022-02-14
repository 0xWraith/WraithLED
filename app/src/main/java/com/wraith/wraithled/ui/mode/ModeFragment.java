package com.wraith.wraithled.ui.mode;

import android.content.Context;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wraith.wraithled.R;
import com.wraith.wraithled.RGBStrip;
import com.wraith.wraithled.SyncDataInterface;

public class ModeFragment extends Fragment implements View.OnClickListener {

    private SyncDataInterface listener;
    private static final String DESCRIBABLE_KEY = "stripObject";
    private RGBStrip strip;

    final int MAX_MOODY_CARDS = 8;
    private final CardView cardMoods[] = new CardView[MAX_MOODY_CARDS];

    public ModeFragment() {
    }

    public static ModeFragment newInstance(RGBStrip strip) {
        ModeFragment fragment = new ModeFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(DESCRIBABLE_KEY, strip);
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
        strip = (RGBStrip) getArguments().getSerializable(DESCRIBABLE_KEY);

        cardMoods[0] = view.findViewById(R.id.cardOne);
        cardMoods[1] = view.findViewById(R.id.cardTwo);
        cardMoods[2] = view.findViewById(R.id.cardThree);
        cardMoods[3] = view.findViewById(R.id.cardFour);
        cardMoods[4] = view.findViewById(R.id.cardFive);
        cardMoods[5] = view.findViewById(R.id.cardSix);
        cardMoods[6] = view.findViewById(R.id.cardSeven);
        cardMoods[7] = view.findViewById(R.id.cardEight);

        for(int i = 0; i < MAX_MOODY_CARDS; i++)
            cardMoods[i].setOnClickListener(this);

        updateFragmentUI();

        return view;
    }

    private void updateFragmentUI()
    {
        if(strip.getModeType() != RGBStrip.MODE_MODES)
            return;

        updateCardState(strip.getMoodMode(), true);
    }

    private void updateCardState(int cardID, boolean selected)
    {
        if(cardID < 0 || cardID >= MAX_MOODY_CARDS)
            return;

        cardMoods[cardID].setCardBackgroundColor(getResources().getColor( !selected ? R.color.cardItem : R.color.cardItemSelected));
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SyncDataInterface) {
            listener = (SyncDataInterface) context;
        }
    }
    @Override
    public void onDetach() {
        listener = null;
        super.onDetach();
    }

    private void sendDataToActivity() { listener.syncStripMainClass(strip); }

    @Override
    public void onClick(View view)
    {
        for(int i = 0; i < MAX_MOODY_CARDS; i++)
        {
            if(cardMoods[i].getId() == view.getId())
            {
                if(i == strip.getMoodMode())
                    break;

                updateCardState(strip.getMoodMode(), false);
                updateCardState(i, true);

                strip.setModeType(RGBStrip.MODE_MODES);
                strip.setMoodMode(i);

                if(strip.sync())
                    sendDataToActivity();
            }
        }
    }
}