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
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import com.google.android.material.navigation.NavigationView;
import com.wraith.wraithled.classes.Favourites;
import com.wraith.wraithled.classes.MoodesHandler;
import com.wraith.wraithled.classes.RGBStrip;
import com.wraith.wraithled.interfaces.SyncDataInterface;
import com.wraith.wraithled.interfaces.UIInterface_Fragment;
import com.wraith.wraithled.ui.CreateFavouriteFragment;
import com.wraith.wraithled.ui.ColorFragment;
import com.wraith.wraithled.ui.FavouritesFragment;
import com.wraith.wraithled.ui.HomeFragment;
import com.wraith.wraithled.ui.ModeFragment;
import com.wraith.wraithled.ui.ScenariousFragment;
import com.wraith.wraithled.ui.ShedulesFragment;
import com.wraith.wraithled.ui.TemperatureFragment;
import com.wraith.wraithled.ui.ThreadFragment;
import com.wraith.wraithled.ui.TimerFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SyncDataInterface, UIInterface_Fragment
{
    private RGBStrip strip;
    private Favourites favourites;
    private MoodesHandler moodesHandler;

    /*UI*/
    private Toolbar toolBar;
    private ImageView stripPower;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        strip = new RGBStrip();
        favourites = new Favourites();
        moodesHandler = new MoodesHandler();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadUIVariables(null);
        configureMenu();
        favourites.getSavedFavouriteList(getApplicationContext());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {

        Fragment fragment;
        int menuSelectedID = item.getItemId();
        FragmentTransaction fragmentTransaction;

        if(menuSelectedID == R.id.nav_home)
            fragment = new HomeFragment().newInstance(strip);

        else if(menuSelectedID == R.id.nav_modes)
            fragment = new ModeFragment().newInstance(strip, moodesHandler);

        else if(menuSelectedID == R.id.nav_temperature)
            fragment = new TemperatureFragment().newInstance(strip);

        else if(menuSelectedID == R.id.nav_color)
            fragment = new ColorFragment().newInstance(strip);

        else if(menuSelectedID == R.id.nav_thread)
            fragment = new ThreadFragment().newInstance(strip);

        else if(menuSelectedID == R.id.nav_timer)
            fragment = new TimerFragment().newInstance(strip);

        else if(menuSelectedID == R.id.nav_shedules)
            fragment = new ShedulesFragment().newInstance("muki", "muki");

        else if(menuSelectedID == R.id.nav_scenarious)
            fragment = new ScenariousFragment().newInstance("muki", "muki");

        else if(menuSelectedID == R.id.nav_favourites)
            fragment = new FavouritesFragment().newInstance(strip, favourites, moodesHandler);

        else
        {
            closeMenu();
            return false;
        }

        closeMenu();
        navigationView.setCheckedItem(menuSelectedID);
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment, fragment).commit();
        return false;
    }

    @Override
    public void onBackPressed()
    {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            closeMenu();
        else
            super.onBackPressed();
    }
    public void loadUIVariables(View view)
    {
        toolBar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawerLayout);
        stripPower = findViewById(R.id.stripStateIcon);
    }
    private void configureMenu()
    {
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolBar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        navigationView.setCheckedItem(R.id.nav_home);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void closeMenu()
    {
        drawerLayout.closeDrawers();
        toolBar.setVisibility(View.VISIBLE);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void stripPower(View view)
    {

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.EFFECT_HEAVY_CLICK));
        else
            vibrator.vibrate(50);

        strip.setPower((strip.getPowerState() == 1 ? 0 : 1));
        changePowerIcon(strip.getPowerState());
        strip.sync();
    }

    public void saveSettings(View view)
    {
        if(strip.getPowerState() == 0)
        {
            Toast.makeText(this, R.string.saveFavErr, Toast.LENGTH_SHORT).show();
            return;
        }

        Fragment fragment;

        closeMenu();
        fragment = new CreateFavouriteFragment().newInstance(strip, favourites);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment, fragment).commit();
    }
    public void syncStripMainClass(RGBStrip newStripObject)
    {
        strip = newStripObject;
    }
    public void syncFavourites(Favourites newFavourites)
    {
        favourites = newFavourites;
    }
    public void changePowerIcon(int power) { stripPower.setImageResource(power == 0 ? R.drawable.state_off : R.drawable.state_on); }
    public void toolBarState(int state) { toolBar.setVisibility(state); }
}