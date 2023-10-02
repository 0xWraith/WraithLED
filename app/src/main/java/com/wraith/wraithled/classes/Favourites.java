package com.wraith.wraithled.classes;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.wraith.wraithled.MainActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class Favourites implements Serializable
{
    private ArrayList<FavouritesInfo> favourites;

    public void saveFav(Context context)
    {
        FileOutputStream fileOutputStream = null;

        try
        {
            fileOutputStream = context.openFileOutput(Utils.FAVOURITES_FILE_NAME, Context.MODE_PRIVATE);
            fileOutputStream.write(new Gson().toJson(getFavourites()).getBytes());
            fileOutputStream.close();

        }
        catch (Exception exception) { exception.printStackTrace(); }

    }
    public boolean getSavedFavouriteList(Context context)
    {
        FileInputStream fileInputStream;
        favourites = new ArrayList<>();

        String ret = "";

        try
        {
            Gson gson = new Gson();
            FavouritesInfo[] savedFavourites;

            InputStream inputStream = context.openFileInput(Utils.FAVOURITES_FILE_NAME);

            if(inputStream != null)
            {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null )
                {
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();
                savedFavourites = gson.fromJson(stringBuilder.toString(), FavouritesInfo[].class);
                favourites = new ArrayList<>(Arrays.asList(savedFavourites));
            }
        }
        catch (FileNotFoundException e) { Log.e("[FAV-LOAD]", "File not found: " + e.toString()); return false; }
        catch (IOException e) { Log.e("[FAV-LOAD]", "Can not read file: " + e.toString()); return false; }

        return true;
    }
    public ArrayList<FavouritesInfo> getFavourites() { return favourites; }
}
