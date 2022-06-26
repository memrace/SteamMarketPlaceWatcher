package com.sozonovalexander.steammarketplacewatcher;

import android.app.Application;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class SteamMarketPlaceWatcher extends Application {
    final ExecutorService executorService = Executors.newFixedThreadPool(4);
}
