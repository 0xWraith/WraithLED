package com.wraith.wraithled.classes;

import static android.content.Context.WIFI_SERVICE;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.wraith.wraithled.ui.HomeFragment;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkHandler
{
    public static OkHttpClient okHttpClient = new OkHttpClient();

    public static void sendGETRequest(Object responseClass, String page)
    {
        Request request = new Request.Builder().url(Utils.NODEMCU_IP_ADRESS + page).build();

        okHttpClient.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) { sendErrResponse(responseClass); }

            @Override
            public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException
            {
                if (response.isSuccessful())
                {
                    if(responseClass instanceof HomeFragment)
                        ((HomeFragment)responseClass).NetworkResponse(Objects.requireNonNull(response.body()).string());

                    else if(responseClass instanceof RGBStrip)
                        ((RGBStrip)responseClass).NetworkResponse(Objects.requireNonNull(response.body()).string());
                }
                else
                    sendErrResponse(responseClass);
            }
        });
    }

    public static void sendPOSTRequest(RGBStrip object, String POSTString)
    {

        Log.d("sendpost", POSTString);
        RequestBody formBody = RequestBody.create(MediaType.parse("application/json"), POSTString);
        Request request = new Request.Builder().url(Utils.NODEMCU_IP_ADRESS + "update").post(formBody).build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e)
            { Log.d("kek", "onFAILUER"); }

            @Override
            public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException {
                if (response.isSuccessful())
                {
                    Log.d("Response", response.body().string());
                    object.NetworkPOSTResponse(response.code());
                }
                else
                {
                    System.out.println("Not successfully:" + response);
                    response.close();
                }
            }
        });
    }

    public static boolean checkIfConnectedCorrect(Context context)
    {
        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);

        if(!wifiManager.isWifiEnabled())
        {
            Log.d("WIFI", "Wifi is not enabled");
            return false;
        }

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ssid = wifiInfo.getSSID();

        ssid = ssid.replace("\"", "");

        return ssid.contains("WraithLED");
    }

    private static void sendErrResponse(Object responseClass)
    {
        if(responseClass instanceof HomeFragment)
            ((HomeFragment)responseClass).NetworkResponse(null);

        else if(responseClass instanceof RGBStrip)
            ((RGBStrip)responseClass).NetworkResponse(null);
    }
}
