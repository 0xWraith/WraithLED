package com.wraith.wraithled.classes;

import com.wraith.wraithled.classes.RGBStrip;

import java.io.Serializable;
import java.util.Locale;


public class FavouritesInfo implements Serializable
{
    private String name;

    private int mode;
    private int[] arguments;

    private final int[] modeArgumentsLength = new int[RGBStrip.MODE_LAST + 1];

    {
        modeArgumentsLength[0] = 2;
        modeArgumentsLength[1] = 5;
        modeArgumentsLength[2] = 4;
        modeArgumentsLength[3] = 6;
    }

    public FavouritesInfo(String name, int mode)
    {
        this.name = name;
        this.mode = mode;
        this.arguments = new int[this.modeArgumentsLength[mode]];
    }
    public FavouritesInfo(String name, int mode, int ...arguments)
    {
        this.name = name;
        this.mode = mode;

        this.arguments = new int[arguments.length];
        System.arraycopy(arguments, 0, this.arguments, 0, arguments.length);

    }

    public void setName(String name) { this.name = name; }
    public String getName() { return this.name; }

    public void setMode(int mode)
    {
        if(mode < 0 || mode > RGBStrip.MODE_LAST)
            return;

        this.mode = mode;
    }
    public int getMode() { return this.mode; }

    public void setArguments(int argument, int idx)
    {
        if(idx < 0 || idx > this.arguments.length)
            return;

        this.arguments[idx] = argument;
    }
    public int[] getArguments() { return this.arguments; }
}
