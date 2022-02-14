package com.wraith.wraithled.ui.color;

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
import android.widget.TextView;

import com.wraith.wraithled.R;
import com.wraith.wraithled.RGBStrip;
import com.wraith.wraithled.SyncDataInterface;

import java.util.Locale;

public class ColorFragment extends Fragment implements View.OnTouchListener {

    private RGBStrip strip;
    private SyncDataInterface listener;
    private static final String DESCRIBABLE_KEY = "stripObject";

    TextView colorPreview;
    ImageView colorWheel;
    ImageView colorPicker;

    private int colorR, colorG, colorB;

    public ColorFragment() { }

    public ColorFragment newInstance(RGBStrip strip)
    {
        ColorFragment fragment = new ColorFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(DESCRIBABLE_KEY, strip);
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

        strip = (RGBStrip) getArguments().getSerializable(DESCRIBABLE_KEY);

        colorPreview = view.findViewById(R.id.colorPreview);
        colorWheel =  view.findViewById(R.id.colorWheel);
        colorPicker =  view.findViewById(R.id.colorPicker);

        colorWheel.setDrawingCacheEnabled(true);
        colorWheel.setOnTouchListener(this);

        updateFragmentUI();

        return view;
    }

    private void updateFragmentUI()
    {

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent)
    {
        if(motionEvent.getAction() == MotionEvent.ACTION_DOWN || motionEvent.getAction() == MotionEvent.ACTION_MOVE)
        {
            Bitmap bitmap = colorWheel.getDrawingCache();

            final int MAX_X = colorWheel.getRight();
            final int MAX_Y = colorWheel.getBottom();

            int posX = (int)motionEvent.getX();
            int posY = (int)motionEvent.getY();
            int pixel;

            posX = Math.min(posX, MAX_X) - 1;
            posY = Math.min(posY, MAX_Y) - 1;

            posX = Math.max(posX, 0);
            posY = Math.max(posY, 0);

            pixel = bitmap.getPixel(posX, posY);

            posX++;
            posY++;

            colorR = Color.red(pixel);
            colorG = Color.green(pixel);
            colorB = Color.blue(pixel);

            colorPicker.setX(posX - 40);
            colorPicker.setY(posY - 50);

            colorPreview.setTextColor(Color.rgb(colorR, colorG, colorB));
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
    public void onAttach(Context context)
    {
        super.onAttach(context);

        if (context instanceof SyncDataInterface)
            listener = (SyncDataInterface) context;
    }
    @Override
    public void onDetach() { listener = null; super.onDetach(); }
    private void sendDataToActivity() { listener.syncStripMainClass(strip); }

}