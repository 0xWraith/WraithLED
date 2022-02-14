package com.wraith.wraithled.ui.timer;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.wraith.wraithled.R;
import com.wraith.wraithled.RGBStrip;
import com.wraith.wraithled.SyncDataInterface;

import java.util.Locale;

public class TimerFragment extends Fragment implements View.OnClickListener
{
    private RGBStrip strip;
    private SyncDataInterface listener;
    private static final String DESCRIBABLE_KEY = "stripObject";

    private Button startTimer;
    private TextView textTimer;
    private NumberPicker timePicker;

    public TimerFragment() { }

    public TimerFragment newInstance(RGBStrip strip)
    {

        TimerFragment fragment = new TimerFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(DESCRIBABLE_KEY, strip);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_timer, container, false);

        strip = (RGBStrip) getArguments().getSerializable(DESCRIBABLE_KEY);

        timePicker = view.findViewById(R.id.timePicker);
        startTimer = view.findViewById(R.id.startTimer);
        textTimer = view.findViewById(R.id.timeLeft);

        timePicker.setMinValue(1);
        timePicker.setMaxValue(60);

        startTimer.setOnClickListener(this);

        updateFragmentUI();

        return view;
    }

    private void updateFragmentUI()
    {
        long unixTime = System.currentTimeMillis() / 1000L;

        if(unixTime < strip.getTimer())
        {
            textTimer.setText(getString(R.string.timerText, (strip.getTimer() - unixTime)/60));

            textTimer.setVisibility(View.VISIBLE);
            startTimer.setText(R.string.stopTimer);
            timePicker.setVisibility(View.INVISIBLE);

            return;
        }
        timePicker.setValue(1);

        timePicker.setVisibility(View.VISIBLE);
        textTimer.setVisibility(View.INVISIBLE);
        startTimer.setText(R.string.startTimer);
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

    private void sendDataToActivity() { listener.syncStripMainClass(strip); }

    @Override
    public void onClick(View view)
    {
        if(view == startTimer)
        {
            long unixTime = System.currentTimeMillis() / 1000L;

            if(unixTime < strip.getTimer())
            {
                strip.setTimer(0L);
                Toast.makeText(getActivity(), String.format(Locale.getDefault(), "Таймер был остановлен", timePicker.getValue()), Toast.LENGTH_SHORT).show();
            }
            else
            {
                strip.setTimer((unixTime + timePicker.getValue() * 60L));
                Toast.makeText(getActivity(), String.format(Locale.getDefault(), "Таймер был установлен", timePicker.getValue()), Toast.LENGTH_SHORT).show();
            }
            if(strip.sync())
                sendDataToActivity();

            updateFragmentUI();
        }
    }
}