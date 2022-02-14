package com.wraith.wraithled.ui.temperature;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.wraith.wraithled.R;
import com.wraith.wraithled.RGBStrip;
import com.wraith.wraithled.SyncDataInterface;

import java.util.Locale;

public class TemperatureFragment extends Fragment implements View.OnTouchListener, SeekBar.OnSeekBarChangeListener {

    private SyncDataInterface listener;
    private static final String DESCRIBABLE_KEY = "stripObject";
    private RGBStrip strip;


    private final int MAX_KELVIN_TEMPERATURE = 5000;
    private final int MIN_KELVIN_TEMPERATURE = 1500;

    private TextView temperatureText;
    private ImageView kelvinScale;
    private ImageView colorPickerIcon;
    private SeekBar brightnessBar;

    private int colorR, colorG, colorB, temperature;

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

    public TemperatureFragment() {

    }

    public static TemperatureFragment newInstance(RGBStrip strip) {
        TemperatureFragment fragment = new TemperatureFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(DESCRIBABLE_KEY, strip);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        strip = (RGBStrip) getArguments().getSerializable(DESCRIBABLE_KEY);

        View view = inflater.inflate(R.layout.fragment_temperature, container, false);

        temperatureText = view.findViewById(R.id.temperatureText);
        kelvinScale =  view.findViewById(R.id.temperatureImage);
        colorPickerIcon =  view.findViewById(R.id.pickerIcon);
        brightnessBar = view.findViewById(R.id.brightnessBar);

        kelvinScale.setDrawingCacheEnabled(true);
        kelvinScale.setOnTouchListener(this);
        brightnessBar.setOnSeekBarChangeListener(this);

        updateFragmentUI();

        return view;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent)
    {
        if(motionEvent.getAction() == MotionEvent.ACTION_DOWN || motionEvent.getAction() == MotionEvent.ACTION_MOVE)
        {
            Bitmap bitmap;
            bitmap = kelvinScale.getDrawingCache();

            final int MAX_X = kelvinScale.getRight();
            final int MAX_Y = kelvinScale.getBottom();

            final double coef = (double) MAX_KELVIN_TEMPERATURE / (double)(MAX_X + MAX_Y);

            int posX = (int)motionEvent.getX();
            int posY = (int)motionEvent.getY();
            int pixel;

            if(posX < 0.0 || posY < 0.0 || posX > MAX_X || posY > MAX_Y)
                return true;

            posX -= 1;
            posY -= 1;

            if (posX < 0)
                posX = 0;

            if(posY < 0)
                posY = 0;

            pixel = bitmap.getPixel(posX, posY);

            posX++;
            posY++;

            colorR = Color.red(pixel);
            colorG = Color.green(pixel);
            colorB = Color.blue(pixel);

            temperature = MIN_KELVIN_TEMPERATURE + (int)(posX * coef + posY * coef);

            colorPickerIcon.setX(posX - 40);
            colorPickerIcon.setY(posY - 40);

            temperatureText.setTextColor(Color.rgb(colorR, colorG, colorB));
            temperatureText.setText(String.format(Locale.ENGLISH, "%dK", temperature));
        }

        if(motionEvent.getAction() == MotionEvent.ACTION_UP)
        {
            strip.setModeType(RGBStrip.MODE_TEMPERATURE);
            strip.setColor(colorR, colorG, colorB);
            strip.setTemperature(temperature);

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
}