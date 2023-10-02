package com.wraith.wraithled.classes;

import android.content.Context;

import com.wraith.wraithled.R;

import java.io.Serializable;
import java.util.ArrayList;

public class MoodesHandler implements Serializable
{
    private final ArrayList<Mood> moodCards;

    public MoodesHandler()
    {
        moodCards = new ArrayList<>();

        moodCards.add(new Mood(1, R.string.mood1, R.drawable.sunrise, true));
        moodCards.add(new Mood(2, R.string.mood2, R.drawable.sunset, false));
        moodCards.add(new Mood(3, R.string.mood3, R.drawable.night, true));
        moodCards.add(new Mood(4, R.string.mood4, R.drawable.clapperboard, true));
        moodCards.add(new Mood(5, R.string.mood5, R.drawable.fireplace, false));
        moodCards.add(new Mood(6, R.string.mood6, R.drawable.heart, false));
        moodCards.add(new Mood(7, R.string.mood7, R.drawable.snow, true));
        moodCards.add(new Mood(8, R.string.mood8, R.drawable.thunder, false));
    }

    public ArrayList<Mood> getMoodCards() { return moodCards; }
    public String getMoodeName(Context context, int moodeID)
    {
        Mood moode = moodCards.stream().filter(mood -> mood.getID() == moodeID).findFirst().orElse(null);
        return moode == null ? "none" : context.getString(moode.getNameID());
    }
    public int getMoodeIcon(int moodeID)
    {
        Mood moode = moodCards.stream().filter(mood -> mood.getID() == moodeID).findFirst().orElse(null);
        return moode == null ? R.drawable.unknown_icon : moode.getIconID();
    }
}
