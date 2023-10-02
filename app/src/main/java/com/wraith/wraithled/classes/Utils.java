package com.wraith.wraithled.classes;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;

public class Utils
{
    public final static int CARD_LAYOUT_WIDTH = 366;
    public final static int CARD_LAYOUT_HEIGHT = 180;
    public final static int CARD_LAYOUT_ICON_SIZE = 60;
    public final static int CARD_LAYOUT_ICON_MARGIN = 15;

    public final static int MAX_KELVIN_TEMPERATURE = 5000;
    public final static int MIN_KELVIN_TEMPERATURE = 1500;

    public final static String MOOD_MODES_OBJ_NAME = "moodesObj";
    public final static String RGB_STRIP_OBJ_NAME = "stripObject";
    public final static String FAVOURITES_OBJ_NAME = "favouritesObj";
    public final static String FAVOURITES_FILE_NAME = "favourites.json";

    public final static String NODEMCU_IP_ADRESS = "http://192.168.31.5/";

    public static int hexToRGB(String hex)
    {
        int hexColorInt = Color.parseColor('#' + hex);
        return Color.rgb((hexColorInt >> 16) & 0xFF, (hexColorInt >> 8) & 0xFF, (hexColorInt) & 0xFF);
    }
    public static int generateID() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return View.generateViewId();
        }
        return -1;
    }
    public static int convertDPToPx(Context context, int dp) { return (int)(dp * context.getResources().getDisplayMetrics().density + 0.5f); }
}
