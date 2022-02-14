package com.wraith.wraithled.ui.favourites;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wraith.wraithled.FavouriteAdapter;
import com.wraith.wraithled.MainActivity;
import com.wraith.wraithled.R;
import com.wraith.wraithled.RGBStrip;
import com.wraith.wraithled.SavedFavourites;
import com.wraith.wraithled.SyncDataInterface;

import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class FavouritesFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private RGBStrip strip;
    private SyncDataInterface listener;
    private ArrayList<SavedFavourites> favourites;

    private static final String DESCRIBABLE_KEY = "stripObject";
    private static final String KEY_FAVOURITES = "favouritesArrayList";

    private TextView infoText;
    private ListView listView;
    private Button clearButton;

    FavouriteAdapter favouriteAdapter;

    public FavouritesFragment() { }

    public FavouritesFragment newInstance(RGBStrip strip, ArrayList<SavedFavourites> favourites)
    {
        FavouritesFragment fragment = new FavouritesFragment();
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
        View view = inflater.inflate(R.layout.fragment_favourites, container, false);

        strip = (RGBStrip) getArguments().getSerializable(DESCRIBABLE_KEY);
        favourites = (ArrayList<SavedFavourites>) getArguments().getSerializable(KEY_FAVOURITES);

        infoText = view.findViewById(R.id.infoText);
        listView = view.findViewById(R.id.favouritesList);
        clearButton = view.findViewById(R.id.buttonRemoveFavourite);

        clearButton.setOnClickListener(this);

        favouriteAdapter = new FavouriteAdapter(getContext(), favourites);
        listView.setAdapter(favouriteAdapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);

        updateFragmentUI();

        return view;
    }
    private void updateFragmentUI()
    {
        if(favourites.size() == 0)
        {
            infoText.setVisibility(View.VISIBLE);
            listView.setVisibility(View.INVISIBLE);
            clearButton.setVisibility(View.INVISIBLE);

            return;
        }
        listView.setVisibility(View.VISIBLE);
        infoText.setVisibility(View.INVISIBLE);
        clearButton.setVisibility(View.VISIBLE);
    }
    @Override
    public void onAttach(Context context)
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

    private void sendDataToActivity() { listener.syncStripMainClass(strip); listener.syncFavourites(favourites); listener.changePowerIcon(true); }

    @Override
    public void onClick(View view)
    {
        if(view == clearButton)
        {

            FileOutputStream fileOutputStream;

            try {
                fileOutputStream = getActivity().openFileOutput(MainActivity.FAVOURITES_FILE_NAME, Context.MODE_PRIVATE);
                fileOutputStream.close();

                favourites.clear();
                favouriteAdapter.clear();
                favouriteAdapter.notifyDataSetChanged();

                updateFragmentUI();
                sendDataToActivity();
            }
            catch (Exception ex) { Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show(); }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        SavedFavourites savedFavourite = (SavedFavourites)adapterView.getItemAtPosition(i);

        int[] arguments = new int[savedFavourite.getArgumentsLength(savedFavourite.getMode())];
        arguments = savedFavourite.getArguments();

        strip.setBrightness(arguments[0]);
        strip.setModeType(savedFavourite.getMode());

        switch (savedFavourite.getMode())
        {
            case RGBStrip.MODE_MODES: { strip.setMoodMode(arguments[1]); break; }
            case RGBStrip.MODE_TEMPERATURE: { strip.setTemperature(arguments[1]); break; }
            case RGBStrip.MODE_COLOR: { strip.setColor(arguments[1], arguments[2], arguments[3]); break; }
        }

        strip.setPower(true);
        strip.setBrightness(arguments[0]);

        strip.sync();
        sendDataToActivity();

        Toast.makeText(getActivity(), "Установлен режим: " + savedFavourite.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        return false;
    }
}