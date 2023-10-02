package com.wraith.wraithled.classes;

import android.graphics.Color;
import android.util.Log;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.stream.IntStream;

public class RGBStrip implements Serializable
{

    static final public int MODE_MODES = 0;
    static final public int MODE_TEMPERATURE = 1;
    static final public int MODE_COLOR = 2;
    static final public int MODE_THREAD = 3;

    public static final int MODE_LAST = 3;

    private int power;

    private int mode;

    private int moodMode;

    private int rgb[] = new int[3];
//    private int threadRGB[][] = new int[4][3];

    private int kelvins;
    private int brightness;
    private long timer;
    private int arduinoTimer;

    {
        power = 1;
        mode   = MODE_COLOR;

        moodMode        = 0;

        rgb[0] =  rgb[1] = rgb[2] = 255;

        kelvins = 6500;
        brightness = 255;
    }
    public RGBStrip() { }

    public void setPower(int power) { this.power = power; }
    public int getPowerState() { return this.power; }

    public void setColor(int colorR, int colorG, int colorB)
    {
        rgb[0] = Math.max(Math.min(colorR, 255), 0);
        rgb[1] = Math.max(Math.min(colorG, 255), 0);
        rgb[2] = Math.max(Math.min(colorB, 255), 0);
    }
    public int[] getColor() { return rgb; }
    public Boolean setModeType(int mode)
    {
        if(mode < 0 || mode > MODE_LAST)
            return false;

        this.mode = mode;

        return true;
    }
    public int getModeType() { return this.mode; }

    public boolean setTemperature(int temperature)
    {
        if(temperature < 1_500 || temperature > 6_500)
            return false;

        kelvins = temperature;

        return true;
    }
    public int getTemperature() { return kelvins; }

    public boolean setBrightness(int brightness)
    {
        if(brightness < 0 || brightness > 255)
            return false;

        this.brightness = brightness;

        return true;
    }
    public int getBrightness() { return this.brightness; }

    public void setMoodMode(int moodMode) { this.moodMode = moodMode; }
    public int getMoodMode() { return this.moodMode; }

    public void setTimer(long timer, int arduinoTimer) { this.timer = timer; this.arduinoTimer = arduinoTimer; }
    public long getTimer() { return this.timer; }

    public void setThreadRGB(int colors[])
    {
        IntStream.range(0, colors.length).parallel().forEach(i -> {
//            threadRGB[i][0] = Color.red(colors[i]);
//            threadRGB[i][1] = Color.green(colors[i]);
//            threadRGB[i][2] = Color.blue(colors[i]);
        });
    }

    public boolean sync()
    {
        Gson gson = new Gson();
        NetworkHandler.sendPOSTRequest(this, gson.toJson(this));
        return true;
    }
    public void NetworkPOSTResponse(int responseCode)
    {
        if(responseCode == 409)
            NetworkHandler.sendGETRequest(this, "");
    }
    public void NetworkResponse(final String response)
    {
        Gson gson = new Gson();
        RGBStrip newone = gson.fromJson(response, RGBStrip.class);
    }
}
