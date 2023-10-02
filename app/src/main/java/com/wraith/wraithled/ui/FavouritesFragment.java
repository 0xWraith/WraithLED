package com.wraith.wraithled.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wraith.wraithled.R;
import com.wraith.wraithled.adapters.FavouriteAdapter;
import com.wraith.wraithled.classes.Favourites;
import com.wraith.wraithled.classes.MoodesHandler;
import com.wraith.wraithled.classes.RGBStrip;
import com.wraith.wraithled.classes.FavouritesInfo;
import com.wraith.wraithled.interfaces.SyncDataInterface;
import com.wraith.wraithled.classes.Utils;
import com.wraith.wraithled.interfaces.UIInterface_Fragment;

import java.io.FileOutputStream;

public class FavouritesFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, UIInterface_Fragment
{

    private RGBStrip strip;
    private Favourites favourites;
    private MoodesHandler moodesHandler;

    private SyncDataInterface listener;

    private TextView infoText;
    private ListView listView;
    private Button clearButton;
    private FavouriteAdapter favouriteAdapter;

    public FavouritesFragment newInstance(RGBStrip strip, Favourites favourites, MoodesHandler moodesHandler)
    {
        FavouritesFragment fragment = new FavouritesFragment();
        Bundle bundle = new Bundle();

        bundle.putSerializable(Utils.RGB_STRIP_OBJ_NAME, strip);
        bundle.putSerializable(Utils.FAVOURITES_OBJ_NAME, favourites);
        bundle.putSerializable(Utils.MOOD_MODES_OBJ_NAME, moodesHandler);

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_favourites, container, false);

        loadUIVariables(view);
        updateFragmentUI();
        return view;
    }
    private void updateFragmentUI()
    {
        if(favourites.getFavourites().size() == 0)
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
    public void onClick(View view)
    {
        if(view == clearButton)
        {

            favourites.getFavourites().clear();
            favourites.saveFav(getContext());

            favouriteAdapter.clear();
            favouriteAdapter.notifyDataSetChanged();

            updateFragmentUI();
            sendDataToActivity();

        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        FavouritesInfo savedFavourite = (FavouritesInfo)adapterView.getItemAtPosition(i);

        int[] arguments = savedFavourite.getArguments();

        strip.setBrightness(arguments[0]);
        strip.setModeType(savedFavourite.getMode());

        switch (savedFavourite.getMode())
        {
            case RGBStrip.MODE_MODES: { strip.setMoodMode(arguments[1]); break; }
            case RGBStrip.MODE_TEMPERATURE: { strip.setTemperature(arguments[1]); break; }
            case RGBStrip.MODE_COLOR: { strip.setColor(arguments[1], arguments[2], arguments[3]); break; }
        }

        strip.setPower(1);
        strip.setBrightness(arguments[0]);

        strip.sync();
        sendDataToActivity();

        Toast.makeText(getActivity(), "Установлен режим: " + savedFavourite.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        FavouritesInfo deleteFavourite = (FavouritesInfo)adapterView.getItemAtPosition(i);

        favourites.getFavourites().remove(deleteFavourite);
        favourites.saveFav(getContext());
        favouriteAdapter.notifyDataSetChanged();
        updateFragmentUI();
        sendDataToActivity();

        Toast.makeText(getActivity(), "Удалён режим: " + deleteFavourite.getName(), Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void loadUIVariables(View view)
    {
        strip = (RGBStrip) getArguments().getSerializable(Utils.RGB_STRIP_OBJ_NAME);
        favourites = (Favourites) getArguments().getSerializable(Utils.FAVOURITES_OBJ_NAME);
        moodesHandler = (MoodesHandler) getArguments().getSerializable(Utils.MOOD_MODES_OBJ_NAME);

        infoText = view.findViewById(R.id.infoText);
        listView = view.findViewById(R.id.favouritesList);
        clearButton = view.findViewById(R.id.buttonRemoveFavourite);
        clearButton.setOnClickListener(this);

        favouriteAdapter = new FavouriteAdapter(getContext(), favourites.getFavourites(), moodesHandler);
        listView.setAdapter(favouriteAdapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
    }

    /*
    * Sync data with MainActivity
    */
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
    private void sendDataToActivity()
    {
        listener.syncStripMainClass(strip);
        listener.syncFavourites(favourites);
        listener.changePowerIcon(strip.getPowerState());
    }
}