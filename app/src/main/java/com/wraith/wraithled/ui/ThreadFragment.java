package com.wraith.wraithled.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import com.wraith.wraithled.MainActivity;
import com.wraith.wraithled.R;
import com.wraith.wraithled.classes.Favourites;
import com.wraith.wraithled.classes.RGBStrip;
import com.wraith.wraithled.classes.Utils;
import com.wraith.wraithled.interfaces.SyncDataInterface;
import com.wraith.wraithled.interfaces.UIInterface_Fragment;

public class ThreadFragment extends Fragment implements UIInterface_Fragment, View.OnClickListener {

    private RGBStrip strip;
    private Favourites favourites;
    private SyncDataInterface listener;

    private int[] circleState = new int[] {-1, -1, -1, -1};
    private final FrameLayout[] colorCircles = new FrameLayout[4];

    public ThreadFragment newInstance(RGBStrip strip)
    {
        Bundle bundle = new Bundle();
        ThreadFragment fragment = new ThreadFragment();

        bundle.putSerializable(Utils.RGB_STRIP_OBJ_NAME, strip);

        fragment.setArguments(bundle);
        return fragment;
    }

    private boolean checkCircles()
    {
        for (int b : circleState)
        {
            if (b == -1)
                return false;
        }
        return true;
    }

    private void changeCircleColor(FrameLayout circle, int idx, int color)
    {
        Context context = getContext();
        GradientDrawable gradientDrawable = new GradientDrawable();

        circleState[idx] = color;

        gradientDrawable.setColor(color);
        gradientDrawable.setShape(GradientDrawable.OVAL);
        gradientDrawable.setStroke(Utils.convertDPToPx(context, 2), ContextCompat.getColor(context, androidx.cardview.R.color.cardview_dark_background));

        circle.setBackground(gradientDrawable);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_thread, container, false);
        loadUIVariables(view);
        return view;
    }

    @Override
    public void loadUIVariables(View view)
    {
        strip = (RGBStrip) getArguments().getSerializable(Utils.RGB_STRIP_OBJ_NAME);
        colorCircles[0] = view.findViewById(R.id.firstColor);
        colorCircles[1] = view.findViewById(R.id.secondColor);
        colorCircles[2] = view.findViewById(R.id.thirdColor);
        colorCircles[3] = view.findViewById(R.id.fourdColor);

        for (FrameLayout colorCircle : colorCircles)
            colorCircle.setOnClickListener(this);
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

    @Override
    public void onClick(View view)
    {
        for (int i = 0; i < colorCircles.length; i++)
        {
            if (view != colorCircles[i])
                continue;

            @SuppressLint("InflateParams") View inputMenu = LayoutInflater.from(getContext()).inflate(R.layout.fragment_thread_input, null);
            PopupWindow popup = new PopupWindow(inputMenu, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            popup.setFocusable(true);
            popup.setAnimationStyle(R.style.PopupAnimation);
            popup.showAtLocation(view, Gravity.CENTER, 0, 0);

            Button saveColor = inputMenu.findViewById(R.id.saveColor);

            int finalI = i;
            saveColor.setOnClickListener(view1 -> {
                EditText hexInput = inputMenu.findViewById(R.id.hex_input);
                String hexCode = hexInput.getText().toString();
                changeCircleColor(colorCircles[finalI], finalI, Utils.hexToRGB(hexCode));

                if(checkCircles())
                {
                    strip.setModeType(RGBStrip.MODE_THREAD);
                    strip.setThreadRGB(circleState);

                    if(strip.sync())
                        sendDataToActivity();
                }

                popup.dismiss();
            });
            break;
        }
    }
}