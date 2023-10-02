package com.wraith.wraithled.interfaces;

import com.wraith.wraithled.classes.Favourites;
import com.wraith.wraithled.classes.RGBStrip;

public interface SyncDataInterface
{
    void syncStripMainClass(RGBStrip newStripObject);
    void syncFavourites(Favourites newFavouritesList);
    void changePowerIcon(int power);
    void toolBarState(int state);
}
