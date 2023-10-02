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

public class ColorFragment extends Fragment implements View.OnTouchListener, SeekBar.OnSeekBarChangeListener, UIInterface_Fragment
{

    private RGBStrip strip;
    private SyncDataInterface listener;

    private int colorR, colorG, colorB;

    private TextView colorPreview;
    private ImageView colorWheel;
    private ImageView colorPicker;

    private TextView textViewChannelR, textViewChannelG, textViewChannelB;
    private SeekBar channelRBar, channelGBar, channelBBar;

    public ColorFragment newInstance(RGBStrip strip)
    {
        Bundle bundle = new Bundle();
        ColorFragment fragment = new ColorFragment();

        bundle.putSerializable(Utils.RGB_STRIP_OBJ_NAME, strip);

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_color, container, false);

        loadUIVariables(view);
        updateFragmentUI();
        return view;
    }

    private void updateFragmentUI()
    {
        int[] color = strip.getColor();

        colorR = color[0];
        colorG = color[1];
        colorB = color[2];

        channelRBar.setProgress(colorR, true);
        channelGBar.setProgress(colorG, true);
        channelBBar.setProgress(colorB, true);

        colorPreview.setTextColor(Color.rgb(colorR, colorG, colorB));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent)
    {
        if(motionEvent.getAction() == MotionEvent.ACTION_DOWN || motionEvent.getAction() == MotionEvent.ACTION_MOVE)
        {
            int pixel, posX, posY;
            Bitmap bitmap = colorWheel.getDrawingCache();

            posX = Math.max(Math.min((int)motionEvent.getX(), colorWheel.getRight()) - 1, 0);
            posY = Math.max(Math.min((int)motionEvent.getY(), colorWheel.getBottom()) - 1, 0);

            pixel = bitmap.getPixel(posX, posY);

            colorR = Color.red(pixel);
            colorG = Color.green(pixel);
            colorB = Color.blue(pixel);

            colorPicker.setX(posX - 39);
            colorPicker.setY(posY - 49);

            channelRBar.setProgress(colorR, true);
            channelGBar.setProgress(colorG, true);
            channelBBar.setProgress(colorB, true);
        }
        if(motionEvent.getAction() == MotionEvent.ACTION_UP)
        {
            strip.setModeType(RGBStrip.MODE_COLOR);
            strip.setColor(colorR, colorG, colorB);

            if(strip.sync())
                sendDataToActivity();
        }
        return true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean bb)
    {
        int ID = seekBar.getId();

        if(ID == R.id.channelRBar || ID == R.id.channelGBar || ID == R.id.channelBBar)
        {
            int r = channelRBar.getProgress();
            int g = channelGBar.getProgress();
            int b = channelBBar.getProgress();

            colorPreview.setTextColor(Color.rgb(r, g, b));

            textViewChannelR.setText(String.valueOf(r));
            textViewChannelG.setText(String.valueOf(g));
            textViewChannelB.setText(String.valueOf(b));

        }
    }
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) { }
    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {
        int ID = seekBar.getId();

        if(ID == R.id.channelRBar || ID == R.id.channelGBar || ID == R.id.channelBBar)
        {
            colorR = channelRBar.getProgress();
            colorG = channelGBar.getProgress();
            colorB = channelBBar.getProgress();

            strip.setColor(colorR, colorG, colorB);
        }

        strip.setModeType(RGBStrip.MODE_COLOR);

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
    public void onDetach() { listener = null; super.onDetach(); }
    private void sendDataToActivity() { listener.syncStripMainClass(strip); }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void loadUIVariables(View view)
    {
        strip = (RGBStrip) getArguments().getSerializable(Utils.RGB_STRIP_OBJ_NAME);

        colorPreview = view.findViewById(R.id.colorPreview);
        colorWheel =  view.findViewById(R.id.colorWheel);
        colorPicker =  view.findViewById(R.id.colorPicker);

        textViewChannelR = view.findViewById(R.id.textViewChannelR);
        textViewChannelG = view.findViewById(R.id.textViewChannelG);
        textViewChannelB = view.findViewById(R.id.textViewChannelB);

        channelRBar = view.findViewById(R.id.channelRBar);
        channelGBar = view.findViewById(R.id.channelGBar);
        channelBBar = view.findViewById(R.id.channelBBar);

        colorWheel.setDrawingCacheEnabled(true);
        colorWheel.setOnTouchListener(this);

        channelRBar.setOnSeekBarChangeListener(this);
        channelGBar.setOnSeekBarChangeListener(this);
        channelBBar.setOnSeekBarChangeListener(this);
    }
}