package com.wraith.wraithled.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wraith.wraithled.R;
import com.wraith.wraithled.classes.FavouritesInfo;
import com.wraith.wraithled.classes.MoodesHandler;
import com.wraith.wraithled.classes.RGBStrip;
import com.wraith.wraithled.interfaces.UIInterface_Fragment;

import java.util.ArrayList;
import java.util.Locale;

public class FavouriteAdapter extends ArrayAdapter<FavouritesInfo> implements UIInterface_Fragment
{
    private final Context mContext;
    private final MoodesHandler moodesHandler;
    private final ArrayList<FavouritesInfo> savedFavouritesList;

    private TextView icon;
    private TextView title;
    private TextView subTitle;

    public FavouriteAdapter(@NonNull Context context, ArrayList<FavouritesInfo> savedFavouritesList, MoodesHandler moodesHandler)
    {
        super(context, 0 , savedFavouritesList);

        mContext = context;
        this.moodesHandler = moodesHandler;
        this.savedFavouritesList = savedFavouritesList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        int mode;
        int[] arguments;

        FavouritesInfo favouriteItem;

        if(convertView == null)
            convertView = LayoutInflater.from(mContext).inflate(R.layout.favourites_list, parent,false);

        loadUIVariables(convertView);

        favouriteItem = savedFavouritesList.get(position);
        mode = favouriteItem.getMode();
        arguments = favouriteItem.getArguments();

        title.setText(favouriteItem.getName());
        subTitle.setText(getDescription(mContext, favouriteItem));

        if(mode == RGBStrip.MODE_MODES)
        {
            Log.e("test", String.valueOf(moodesHandler.getMoodeIcon(arguments[1])));
            Log.e("Test", String.valueOf(arguments[1]));
            icon.setBackgroundResource(moodesHandler.getMoodeIcon(arguments[1]));
        }
        else
        {
//            GradientDrawable gradient = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[] {
//                    Color.rgb(47, 140, 117),
//                    Color.rgb(47, 103, 140),
//                    Color.rgb(140, 47, 84),
//                    Color.rgb(140, 90, 47)
//            });
//            icon.setBackground(gradient);

            icon.setBackgroundColor(Color.rgb(arguments[1], arguments[2], arguments[3]));
        }
        return convertView;
    }

    @Override
    public void loadUIVariables(View view)
    {
        icon = view.findViewById(R.id.itemIcon);
        title = view.findViewById(R.id.itemTitle);
        subTitle = view.findViewById(R.id.itemSubTitle);
    }

    private String getDescription(Context context, FavouritesInfo favouritesInfo)
    {
        int[] arguments = favouritesInfo.getArguments();

        switch(favouritesInfo.getMode())
        {
            case RGBStrip.MODE_TEMPERATURE: return String.format(Locale.getDefault(), "Температура | %dK | Яркость: %d%%", arguments[4], arguments[0] * 100 / 255);
            case RGBStrip.MODE_MODES: return String.format(Locale.getDefault(), "%s | Яркость: %d%%", moodesHandler.getMoodeName(context, arguments[1]), arguments[0] * 100 / 255);
            case RGBStrip.MODE_COLOR: return String.format(Locale.getDefault(), "Цвет | %d, %d, %d | Яркость: %d%%", arguments[1], arguments[2], arguments[3], arguments[0] * 100 / 255);
            default: return context.getString(R.string.error);
        }
    }
}
