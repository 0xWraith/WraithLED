package com.wraith.wraithled.ui.cfavourite;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.wraith.wraithled.MainActivity;
import com.wraith.wraithled.R;
import com.wraith.wraithled.RGBStrip;
import com.wraith.wraithled.SavedFavourites;
import com.wraith.wraithled.SyncDataInterface;
import com.wraith.wraithled.ui.mode.ModeFragment;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class CreateFavouriteFragment extends Fragment implements View.OnClickListener
{

    private SyncDataInterface listener;

    private static final String DESCRIBABLE_KEY = "stripObject";
    private static final String KEY_FAVOURITES = "favouritesArrayList";

    private RGBStrip strip;
    private ArrayList<SavedFavourites> favourites;

    Button saveButton;
    EditText favouriteInput;

    public CreateFavouriteFragment() { }

    public CreateFavouriteFragment newInstance(RGBStrip strip, ArrayList<SavedFavourites> favourites) {

        CreateFavouriteFragment fragment = new CreateFavouriteFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(DESCRIBABLE_KEY, strip);
        bundle.putSerializable(KEY_FAVOURITES, (Serializable) favourites);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_create_favourite, container, false);

        strip = (RGBStrip) getArguments().getSerializable(DESCRIBABLE_KEY);
        favourites = (ArrayList<SavedFavourites>) getArguments().getSerializable(KEY_FAVOURITES);

        saveButton = view.findViewById(R.id.buttonSaveFavourite);
        favouriteInput = view.findViewById((R.id.favouritesNameInput));

        saveButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SyncDataInterface) {
            listener = (SyncDataInterface) context;
        }
    }
    @Override
    public void onDetach() {
        listener = null;
        super.onDetach();
    }

    private void sendDataToActivity() { listener.syncFavourites(favourites); }

    private void saveFavourites()
    {
        FileOutputStream fileOutputStream = null;

        try
        {
            fileOutputStream = getActivity().openFileOutput(MainActivity.FAVOURITES_FILE_NAME, Context.MODE_PRIVATE);
            fileOutputStream.write(new Gson().toJson(favourites).getBytes());

        }
        catch (Exception exception) { Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_LONG); }

        if(fileOutputStream != null)
        {
            try { fileOutputStream.close(); }
            catch (IOException exception) { exception.printStackTrace(); }
        }

    }

    @Override
    public void onClick(View view)
    {
        if(view.getId() == saveButton.getId())
        {
            if(!strip.getPowerState())
            {
                Toast.makeText(getActivity(), "Невозможно сохранить текущее состояние пока лента выключена", Toast.LENGTH_SHORT).show();
                return;
            }

            String name = favouriteInput.getText().toString();

            if(name.length() < 3)
            {
                Toast.makeText(getActivity(), "Минимальная длина названия - 3 символа!", Toast.LENGTH_SHORT).show();
                return;
            }
            int stripMode = strip.getModeType();

            SavedFavourites favourite = new SavedFavourites(name, stripMode);

            switch (stripMode)
            {
                case RGBStrip.MODE_MODES:
                {
                    favourite.setArguments(strip.getBrightness(), 0);
                    favourite.setArguments(strip.getMoodMode(), 1);
                    break;
                }
                case RGBStrip.MODE_TEMPERATURE:
                {
                    int[] colors = new int[3];
                    colors = strip.getColor();

                    favourite.setArguments(strip.getBrightness(), 0);
                    favourite.setArguments(colors[0], 1);
                    favourite.setArguments(colors[1], 2);
                    favourite.setArguments(colors[2], 3);
                    favourite.setArguments(strip.getTemperature(), 4);

                    break;
                }
                case RGBStrip.MODE_COLOR:
                {
                    int[] colors = new int[3];
                    colors = strip.getColor();

                    favourite.setArguments(strip.getBrightness(), 0);
                    favourite.setArguments(colors[0], 1);
                    favourite.setArguments(colors[1], 2);
                    favourite.setArguments(colors[2], 3);
                    break;
                }
            }

            favourites.add(favourite);

            saveFavourites();
            sendDataToActivity();

            return;
        }
    }
}