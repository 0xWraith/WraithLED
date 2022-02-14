package com.wraith.wraithled;

import java.util.ArrayList;

public interface SyncDataInterface {
    void syncStripMainClass(RGBStrip newStripObject);
    void syncFavourites(ArrayList<SavedFavourites> newFavouritesList);
    void changePowerIcon(boolean power);
}
