package com.uakgul.moviedb.moviet.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class Utils {

    private Context appCoontext;


    public Utils(Context context) {
        appCoontext = context;
    }

    public boolean haveNetworkConnection() {

        boolean haveConnected  = false;
        ConnectivityManager cm = (ConnectivityManager) appCoontext.getSystemService( CONNECTIVITY_SERVICE );
        NetworkInfo[] networkInfos = cm.getAllNetworkInfo();

        for( NetworkInfo ni : networkInfos ){

            if( ni != null && ni.isConnected() ){
                haveConnected = true;
            }
        }

        return haveConnected ;
    }// end of haveNetworkConnection





}
