package com.wraith.wraithled.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.wraith.wraithled.R;
import com.wraith.wraithled.classes.Favourites;
import com.wraith.wraithled.classes.RGBStrip;
import com.wraith.wraithled.classes.FavouritesInfo;
import com.wraith.wraithled.interfaces.SyncDataInterface;
import com.wraith.wraithled.classes.Utils;
import com.wraith.wraithled.interfaces.UIInterface_Fragment;

import java.io.FileOutputStream;
import java.io.IOException;

public class CreateFavouriteFragment extends Fragment implements View.OnClickListener, UIInterface_Fragment
{

    private RGBStrip strip;
    private Favourites favourites;
    private SyncDataInterface listener;

    private Button saveButton;
    private EditText favouriteInput;

    public CreateFavouriteFragment newInstance(RGBStrip strip, Favourites favourites)
    {
        Bundle bundle = new Bundle();
        CreateFavouriteFragment fragment = new CreateFavouriteFragment();

        bundle.putSerializable(Utils.RGB_STRIP_OBJ_NAME, strip);
        bundle.putSerializable(Utils.FAVOURITES_OBJ_NAME, favourites);

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_create_favourite, container, false);
        loadUIVariables(view);
        return view;
    }

    @Override
    public void onClick(View view)
    {
        if(view.getId() == saveButton.getId())
        {
            if(strip.getPowerState() == 0)
            {
                Toast.makeText(getActivity(), R.string.saveFavErr, Toast.LENGTH_SHORT).show();
                return;
            }

            String name = favouriteInput.getText().toString();

            if(name.length() == 0)
            {
                Toast.makeText(getActivity(), R.string.saveFavErrLen, Toast.LENGTH_SHORT).show();
                return;
            }

            int stripMode = strip.getModeType();
            FavouritesInfo favourite = new FavouritesInfo(name, stripMode);

            switch (stripMode)
            {
                case RGBStrip.MODE_MODES:
                {
                    favourite.setArguments(strip.getMoodMode(), 1);
                    favourite.setArguments(strip.getBrightness(), 0);
                    break;
                }
                case RGBStrip.MODE_TEMPERATURE:
                {
                    int[] colors;
                    colors = strip.getColor();

                    favourite.setArguments(colors[0], 1);
                    favourite.setArguments(colors[1], 2);
                    favourite.setArguments(colors[2], 3);

                    favourite.setArguments(strip.getBrightness(), 0);
                    favourite.setArguments(strip.getTemperature(), 4);
                    break;
                }
                case RGBStrip.MODE_COLOR:
                {
                    int[] colors;

                    colors = strip.getColor();
                    favourite.setArguments(colors[0], 1);
                    favourite.setArguments(colors[1], 2);
                    favourite.setArguments(colors[2], 3);
                    favourite.setArguments(strip.getBrightness(), 0);
                    break;
                }
            }

            favourites.getFavourites().add(favourite);
            favourites.saveFav(getContext());
            sendDataToActivity();
        }
    }

    public void loadUIVariables(View view)
    {
        saveButton = view.findViewById(R.id.buttonSaveFavourite);
        favouriteInput = view.findViewById((R.id.favouritesNameInput));
        strip = (RGBStrip) getArguments().getSerializable(Utils.RGB_STRIP_OBJ_NAME);
        favourites = (Favourites) getArguments().getSerializable(Utils.FAVOURITES_OBJ_NAME);
        saveButton.setOnClickListener(this);
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
        if (context instanceof SyncDataInterface)
            listener = (SyncDataInterface) context;
    }
    @Override
    public void onDetach()
    {
        listener = null;
        super.onDetach();
    }

    private void sendDataToActivity() { listener.syncFavourites(favourites); }
}