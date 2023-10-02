package com.wraith.wraithled.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.wraith.wraithled.R;
import com.wraith.wraithled.classes.RGBStrip;
import com.wraith.wraithled.classes.Utils;
import com.wraith.wraithled.interfaces.SyncDataInterface;
import com.wraith.wraithled.interfaces.UIInterface_Fragment;

import java.util.Locale;

public class TemperatureFragment extends Fragment implements View.OnTouchListener, SeekBar.OnSeekBarChangeListener, UIInterface_Fragment
{

    private RGBStrip strip;
    private SyncDataInterface listener;

    private int colorR, colorG, colorB, temperature;

    private TextView temperatureText;
    private ImageView kelvinScale;
    private ImageView colorPickerIcon;
    private SeekBar brightnessBar;

    public TemperatureFragment newInstance(RGBStrip strip)
    {
        Bundle bundle = new Bundle();
        TemperatureFragment fragment = new TemperatureFragment();

        bundle.putSerializable(Utils.RGB_STRIP_OBJ_NAME, strip);

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_temperature, container, false);

        loadUIVariables(view);
        updateFragmentUI();
        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent)
    {
        if(motionEvent.getAction() == MotionEvent.ACTION_DOWN || motionEvent.getAction() == MotionEvent.ACTION_MOVE)
        {
            int posX, posY, pixel;
            final int MAX_X = kelvinScale.getRight();
            final int MAX_Y = kelvinScale.getBottom();
            Bitmap bitmap = kelvinScale.getDrawingCache();
            final double coef = (double) Utils.MAX_KELVIN_TEMPERATURE / (double)(MAX_X + MAX_Y);

            posX = Math.max(Math.min((int)motionEvent.getX(), MAX_X) - 1, 2);
            posY = Math.max(Math.min((int)motionEvent.getY(), MAX_Y) - 1, 2);

            pixel = bitmap.getPixel(posX, posY);

            posX++;
            posY++;

            colorR = Color.red(pixel);
            colorG = Color.green(pixel);
            colorB = Color.blue(pixel);

            temperature = Utils.MIN_KELVIN_TEMPERATURE + (int)(posX * coef + posY * coef);

            colorPickerIcon.setX(posX - 40);
            colorPickerIcon.setY(posY - 40);

            temperatureText.setTextColor(Color.rgb(colorR, colorG, colorB));
            temperatureText.setText(String.format(Locale.ENGLISH, "%dK", temperature));
        }

        if(motionEvent.getAction() == MotionEvent.ACTION_UP)
        {
            strip.setTemperature(temperature);
            strip.setColor(colorR, colorG, colorB);
            strip.setModeType(RGBStrip.MODE_TEMPERATURE);

            if(strip.sync())
                sendDataToActivity();
        }
        return true;
    }

    public void updateFragmentUI()
    {
        temperatureText.setTextColor(Color.rgb(254, 180, 20));
        temperatureText.setText(String.format(Locale.ENGLISH, "%dK", strip.getTemperature()));

        brightnessBar.setProgress(strip.getBrightness());
    }
    private void sendDataToActivity() { listener.syncStripMainClass(strip); }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) { }
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) { }
    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {
        if(seekBar.getId() != R.id.brightnessBar)
            return;

        strip.setBrightness(seekBar.getProgress());

        if(strip.sync())
            sendDataToActivity();
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void loadUIVariables(View view)
    {
        strip = (RGBStrip) getArguments().getSerializable(Utils.RGB_STRIP_OBJ_NAME);

        temperatureText = view.findViewById(R.id.temperatureText);
        kelvinScale =  view.findViewById(R.id.temperatureImage);
        colorPickerIcon =  view.findViewById(R.id.pickerIcon);
        brightnessBar = view.findViewById(R.id.brightnessBar);

        kelvinScale.setDrawingCacheEnabled(true);
        kelvinScale.setOnTouchListener(this);
        brightnessBar.setOnSeekBarChangeListener(this);
    }
}