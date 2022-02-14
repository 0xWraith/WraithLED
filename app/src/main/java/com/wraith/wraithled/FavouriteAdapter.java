package com.wraith.wraithled;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Locale;

public class FavouriteAdapter extends ArrayAdapter<SavedFavourites>
{
    private Context mContext;
    private ArrayList<SavedFavourites> savedFavouritesList = new ArrayList<>();

    public FavouriteAdapter(@NonNull Context context, ArrayList<SavedFavourites> savedFavouritesList)
    {
        super(context, 0 , savedFavouritesList);
        mContext = context;
        this.savedFavouritesList = savedFavouritesList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        if(convertView == null)
            convertView = LayoutInflater.from(mContext).inflate(R.layout.favourites_list, parent,false);

        SavedFavourites favouriteItem = savedFavouritesList.get(position);

        int mode = favouriteItem.getMode();
        int[] arguments = new int[favouriteItem.getArgumentsLength(mode)];
        arguments = favouriteItem.getArguments();

        TextView icon = convertView.findViewById(R.id.itemIcon);
        TextView title = convertView.findViewById(R.id.itemTitle);
        TextView subTitle = convertView.findViewById(R.id.itemSubTitle);

        title.setText(favouriteItem.getName());
        subTitle.setText(favouriteItem.getDescription());

        if(mode == RGBStrip.MODE_MODES)
        {
            switch(arguments[1])
            {
                case 0: { icon.setBackground(getContext().getResources().getDrawable(R.drawable.sunrise)); break; }
                case 1: { icon.setBackground(getContext().getResources().getDrawable(R.drawable.sunset)); break; }
                case 2: { icon.setBackground(getContext().getResources().getDrawable(R.drawable.night)); break; }
                case 3: { icon.setBackground(getContext().getResources().getDrawable(R.drawable.clapperboard)); break; }
                case 4: { icon.setBackground(getContext().getResources().getDrawable(R.drawable.fireplace)); break; }
                case 5: { icon.setBackground(getContext().getResources().getDrawable(R.drawable.heart)); break; }
                case 6: { icon.setBackground(getContext().getResources().getDrawable(R.drawable.snow)); break; }
                case 7: { icon.setBackground(getContext().getResources().getDrawable(R.drawable.thunder)); break; }
            }
        }

        else
            icon.setBackgroundColor(Color.rgb(arguments[1], arguments[2], arguments[3]));

        return convertView;
    }
}
