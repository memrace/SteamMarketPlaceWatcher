package com.sozonovalexander.steammarketplacewatcher;

import android.app.Application;
import android.os.Handler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dagger.hilt.android.HiltAndroidApp;
import lombok.Getter;

@HiltAndroidApp
public class SteamMarketPlaceWatcherApplication extends Application {
    public static final ExecutorService executorService = Executors.newFixedThreadPool(4);
    @Getter
    private static Handler mainHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        mainHandler = new Handler(this.getMainLooper());
    }
}