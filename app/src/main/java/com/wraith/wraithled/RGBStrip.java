package com.wraith.wraithled;

import android.util.Log;

import com.google.gson.Gson;

import java.io.Serializable;

public class RGBStrip implements Serializable
{

    static final public int MODE_MODES = 0;
    static final public int MODE_TEMPERATURE = 1;
    static final public int MODE_COLOR = 2;
    static final public int MODE_THREAD = 3;

    static final int MODE_LAST = 3;

    private boolean power;

    private int stripMode;

    private int mode;

    private int colorR;
    private int colorG;
    private int colorB;

    private int temperature;
    private int brightness;
    private long timer;

    {
        stripMode   = MODE_MODES;

        mode        = 0;

        colorR      =
        colorG      =
        colorB      = 255;

        temperature = 6500;
        brightness = 255;
    }
    RGBStrip() { }

    public void setPower(boolean power) { this.power = power; }
    public boolean getPowerState() { return this.power; }

    public void setColor(int colorR, int colorG, int colorB)
    {
        if(colorR < 0)
            colorR = 0;

        if(colorG < 0)
            colorR = 0;

        if(colorB < 0)
            colorR = 0;

        if(colorR > 255)
            colorR = 255;

        if(colorG > 255)
            colorR = 255;

        if(colorB > 255)
            colorR = 255;

        this.colorR = colorR;
        this.colorG = colorG;
        this.colorB = colorB;
    }
    public int[] getColor()
    {
        int color[] = new int[3];

        color[0] = this.colorR;
        color[1] = this.colorG;
        color[2] = this.colorB;

        return color;
    }
    public Boolean setModeType(int stripMode)
    {
        if(stripMode < 0 || stripMode > MODE_LAST)
            return false;

        this.stripMode = stripMode;

        return true;
    }
    public int getModeType() { return this.stripMode; }

    public boolean setTemperature(int temperature)
    {
        if(temperature < 1_500 || temperature > 6_500)
            return false;

        this.temperature = temperature;

        return true;
    }
    public int getTemperature() { return this.temperature; }

    public boolean setBrightness(int brightness)
    {
        if(brightness < 0 || brightness > 255)
            return false;

        this.brightness = brightness;

        return true;
    }
    public int getBrightness() { return this.brightness; }

    public void setMoodMode(int mode) { this.mode = mode; }
    public int getMoodMode() { return this.mode; }

    public void setTimer(long timer) { this.timer = timer; }
    public long getTimer() { return this.timer; }

    public static String getMoodyModeName(int mode)
    {
        switch(mode)
        {
            case 0: return "Рассвет";
            case 1: return "Закат";
            case 2: return "Ночник";
            case 3: return "Кино";
            case 4: return "Камин";
            case 5: return "Horny";
            case 6: return "Снег";
            case 7: return "Гроза";
            default: return "Ошибка";
        }
    }

    public boolean sync()
    {

        if(this.getPowerState() == false)
            return true;

        Gson gson = new Gson();
        Log.d("Data", gson.toJson(this));

        return true;
    }
}
