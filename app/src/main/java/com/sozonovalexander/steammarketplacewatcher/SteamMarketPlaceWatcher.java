package com.sozonovalexander.steammarketplacewatcher;

import android.app.Application;
import android.os.Handler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class SteamMarketPlaceWatcher extends Application {
    public final ExecutorService executorService = Executors.newFixedThreadPool(4);
    private Handler handler;

    public Handler getMainHandler() {
        if (handler == null)
            return new Handler(this.getMainLooper());
        else return handler;
    }
}