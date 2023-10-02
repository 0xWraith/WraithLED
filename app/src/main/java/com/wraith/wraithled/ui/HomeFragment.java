package com.wraith.wraithled.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.wraith.wraithled.R;
import com.wraith.wraithled.classes.Favourites;
import com.wraith.wraithled.classes.RGBStrip;
import com.wraith.wraithled.classes.Utils;
import com.wraith.wraithled.interfaces.SyncDataInterface;
import com.wraith.wraithled.classes.NetworkHandler;
import com.wraith.wraithled.interfaces.UIInterface_Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class HomeFragment extends Fragment implements UIInterface_Fragment
{

    private RGBStrip strip;
    private SyncDataInterface listener;

    private Button noConnectionBtn;
    private ConstraintLayout connectionBlock;
    private ConstraintLayout noConnectionBlock;


    public HomeFragment newInstance(RGBStrip strip)
    {
        Bundle bundle = new Bundle();
        HomeFragment fragment = new HomeFragment();

        bundle.putSerializable(Utils.RGB_STRIP_OBJ_NAME, strip);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        loadUIVariables(view);
        return view;
    }

    public void NetworkResponse(final String response)
    {
        if(response == null)
        {
            showNoConnectionUI();
            return;
        }

        try
        {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray rgb = jsonObject.getJSONArray("rgb");

            strip.setPower(jsonObject.getInt("power"));
            strip.setTimer(jsonObject.getInt("timer"), jsonObject.getInt("arduinoTimer"));
            strip.setModeType(jsonObject.getInt("mode"));
            strip.setMoodMode(jsonObject.getInt("moodMode"));
            strip.setTemperature(jsonObject.getInt("kelvins"));
            strip.setBrightness(jsonObject.getInt("brightness"));
            strip.setColor(rgb.getInt(0), rgb.getInt(1), rgb.getInt(2));

            sendDataToActivity();

            new Handler(Looper.getMainLooper()).post(this::showConnectedUI);
        }
        catch (Exception exception) { Log.e("[err]", exception.getMessage()); }
    }

    private void showNoConnectionUI()
    {
        listener.toolBarState(View.INVISIBLE);
        noConnectionBlock.setVisibility(View.VISIBLE);
    }
    private void hideNoConnectionUI()
    {
        listener.toolBarState(View.VISIBLE);
        noConnectionBlock.setVisibility(View.INVISIBLE);
    }

    private void showConnectedUI()
    {
        hideNoConnectionUI();
        connectionBlock.setVisibility(View.VISIBLE);
    }


    private void sendDataToActivity()
    {
        listener.syncStripMainClass(strip);
        listener.changePowerIcon(strip.getPowerState());
    }

    @Override
    public void loadUIVariables(View view)
    {

        strip = (RGBStrip) (getArguments() != null ? getArguments().getSerializable(Utils.RGB_STRIP_OBJ_NAME) : new RGBStrip());

        noConnectionBtn = view.findViewById(R.id.noConnectionBtn);
        connectionBlock = view.findViewById(R.id.connectionBlock);
        noConnectionBlock = view.findViewById(R.id.noConnectionBlock);

        noConnectionBtn.setOnClickListener(view1 ->
        {
            if(view1 == noConnectionBtn && NetworkHandler.checkIfConnectedCorrect(getContext()))
            {
                try { NetworkHandler.sendGETRequest(this,""); }
                catch (Exception exception) { Toast.makeText(getActivity(), "No response", Toast.LENGTH_SHORT).show(); }
            }
        });

//        if(!NetworkHandler.checkIfConnectedCorrect(getContext()))
//        {
//            showNoConnectionUI();
//            return;
//        }

        try { NetworkHandler.sendGETRequest(this,""); }
        catch (Exception e) { showNoConnectionUI(); }
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
}