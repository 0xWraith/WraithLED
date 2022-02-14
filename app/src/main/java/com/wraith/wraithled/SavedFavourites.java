package com.wraith.wraithled;

import java.io.Serializable;
import java.util.Locale;


public class SavedFavourites implements Serializable
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

    public  SavedFavourites(String name, int mode)
    {
        this.name = name;
        this.mode = mode;
        this.arguments = new int[this.modeArgumentsLength[mode]];
    }
    public SavedFavourites(String name, int mode, int ...arguments)
    {
        this.name = name;
        this.mode = mode;

        this.arguments = new int[arguments.length];

        for(int i = 0; i < arguments.length; i++)
            this.arguments[i] = arguments[i];

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

    public String getDescription()
    {
        String description = new String();

        switch(this.mode)
        {
            case RGBStrip.MODE_MODES: return String.format(Locale.getDefault(), "%s | Яркость: %d%%", RGBStrip.getMoodyModeName(this.arguments[1]), this.arguments[0] * 100 / 255);
            case RGBStrip.MODE_TEMPERATURE: return String.format(Locale.getDefault(), "Температура | %dK | Яркость: %d%%", this.arguments[4], this.arguments[0] * 100 / 255);
            case RGBStrip.MODE_COLOR: return String.format(Locale.getDefault(), "Цвет | %d, %d, %d | Яркость: %d%%", this.arguments[1], this.arguments[2], this.arguments[3], this.arguments[0] * 100 / 255);
        }

        return String.format(Locale.getDefault(), "Ошибка #%d", this.mode);
    }

    public int getArgumentsLength(int mode) { return modeArgumentsLength[mode]; }
}
