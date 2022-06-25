package com.sozonovalexander.steammarketplacewatcher;

import com.sozonovalexander.steammarketplacewatcher.models.SteamMarketPlaceApi;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(ActivityComponent.class)
public class HiltModule {

    @Provides
    public static SteamMarketPlaceApi provideSteamMarketPlaceApi() {
        return new Retrofit.Builder()
                .baseUrl("https://steamcommunity.com/market/")
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(SteamMarketPlaceApi.class);
    }
}