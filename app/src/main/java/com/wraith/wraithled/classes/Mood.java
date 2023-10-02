package com.wraith.wraithled.classes;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Locale;

public class Mood implements Serializable
{
    private int viewID;
    private final int ID;
    private final int nameID;
    private final int iconID;
    private final boolean stack;

    public Mood(int ID, int nameID, int iconID, boolean stack)
    {
        this.ID = ID;
        this.nameID = nameID;
        this.iconID = iconID;
        this.stack = stack;
    }

    public int getID() { return ID; }
    public int getViewID() { return viewID; }
    public int getNameID() { return nameID; }
    public int getIconID() { return iconID; }
    public boolean isStack() { return stack; }

    public void  setViewID(int viewID) { this.viewID = viewID; }

    @NonNull
    @Override
    public String toString() { return String.format(Locale.getDefault(), "ID: %d || %s || %d || %d", ID, nameID, iconID, viewID); }
}
