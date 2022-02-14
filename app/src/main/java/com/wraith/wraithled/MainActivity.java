package com.wraith.wraithled;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wraith.wraithled.ui.cfavourite.CreateFavouriteFragment;
import com.wraith.wraithled.ui.color.ColorFragment;
import com.wraith.wraithled.ui.favourites.FavouritesFragment;
import com.wraith.wraithled.ui.home.HomeFragment;
import com.wraith.wraithled.ui.mode.ModeFragment;
import com.wraith.wraithled.ui.scenarious.ScenariousFragment;
import com.wraith.wraithled.ui.shedules.ShedulesFragment;
import com.wraith.wraithled.ui.temperature.TemperatureFragment;
import com.wraith.wraithled.ui.thread.ThreadFragment;
import com.wraith.wraithled.ui.timer.TimerFragment;

import java.io.FileInputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SyncDataInterface
{

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolBar;

    ImageView stripPower;

    private RGBStrip strip;
    private ArrayList<SavedFavourites> favourites;

    public final static String FAVOURITES_FILE_NAME = "favourites.json";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        strip = new RGBStrip();

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nav_view);
        toolBar = findViewById(R.id.toolbar);

        navigationView.bringToFront();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolBar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        navigationView.setCheckedItem(R.id.nav_home);
        navigationView.setNavigationItemSelectedListener(this);

        stripPower = (ImageView) findViewById(R.id.stripStateIcon);

        changePowerIcon(strip.getPowerState());
        getSavedFavouriteList();
    }

    @Override
    public void onBackPressed()
    {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
            toolBar.setVisibility(View.VISIBLE);
        }
        else
            super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {

        int menuSelectedID = item.getItemId();

        Fragment fragment = null;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();


        if(menuSelectedID == R.id.nav_home)
            fragment = new HomeFragment();

        else if(menuSelectedID == R.id.nav_modes)
            fragment = new ModeFragment().newInstance(strip);

        else if(menuSelectedID == R.id.nav_temperature)
            fragment = new TemperatureFragment().newInstance(strip);

        else if(menuSelectedID == R.id.nav_color)
            fragment = new ColorFragment().newInstance(strip);

        else if(menuSelectedID == R.id.nav_thread)
            fragment = new ThreadFragment().newInstance("muki", "muki");

        else if(menuSelectedID == R.id.nav_timer)
            fragment = new TimerFragment().newInstance(strip);

        else if(menuSelectedID == R.id.nav_shedules)
            fragment = new ShedulesFragment().newInstance("muki", "muki");

        else if(menuSelectedID == R.id.nav_scenarious)
            fragment = new ScenariousFragment().newInstance("muki", "muki");

        else if(menuSelectedID == R.id.nav_favourites)
            fragment = new FavouritesFragment().newInstance(strip, favourites);

        else
            fragment = new HomeFragment();

        navigationView.setCheckedItem(menuSelectedID);

        fragmentTransaction.replace(R.id.nav_host_fragment, fragment).commit();

        drawerLayout.closeDrawers();
        toolBar.setVisibility(View.VISIBLE);

        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void stripPower(View view)
    {

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.EFFECT_TICK));
        else
            vibrator.vibrate(500);

        strip.setPower(!strip.getPowerState());
        changePowerIcon(strip.getPowerState());

        strip.sync();
    }

    public void saveSettings(View view)
    {
        if(!strip.getPowerState())
        {
            Toast.makeText(this, "Невозможно сохранить текущее состояние пока лента выключена", Toast.LENGTH_SHORT).show();
            return;
        }

        Fragment fragment = new CreateFavouriteFragment().newInstance(strip, favourites);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.nav_host_fragment, fragment).commit();

        drawerLayout.closeDrawers();
        toolBar.setVisibility(View.VISIBLE);
    }

    private void getSavedFavouriteList()
    {
        FileInputStream fileInputStream;

        try
        {
            fileInputStream = openFileInput(MainActivity.FAVOURITES_FILE_NAME);
            byte byteArray[] = new byte[fileInputStream.available()];

            fileInputStream.read(byteArray);
            String text = new String (byteArray);

            Gson gson = new Gson();

            SavedFavourites[] savedFavourites = gson.fromJson(text, SavedFavourites[].class);
            favourites = new ArrayList<SavedFavourites>(Arrays.asList(savedFavourites));

            fileInputStream.close();
        }
        catch (Exception ex) { favourites = new ArrayList<SavedFavourites>(); }
    }
    public void syncStripMainClass(RGBStrip newStripObject) { strip = newStripObject; }
    public void syncFavourites(ArrayList<SavedFavourites> newFavouritesList) { favourites = newFavouritesList; }
    public void changePowerIcon(boolean power) { stripPower.setImageResource(!power ? R.drawable.state_off : R.drawable.state_on); }
}