package com.sozonovalexander.steammarketplacewatcher;

import android.content.Context;

import androidx.room.Room;

import com.sozonovalexander.steammarketplacewatcher.dal.MarketPlaceDatabase;
import com.sozonovalexander.steammarketplacewatcher.dal.Migrations;
import com.sozonovalexander.steammarketplacewatcher.network.SteamMarketPlaceApi;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class HiltCommonModule {

    @Provides
    public static SteamMarketPlaceApi provideSteamMarketPlaceApi() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        return new Retrofit.Builder()
                .baseUrl("https://steamcommunity.com/market/")
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(SteamMarketPlaceApi.class);
    }

    @Provides
    public static MarketPlaceDatabase provideMarketPlaceDatabase(@ApplicationContext Context app) {
        return Room.databaseBuilder(app, MarketPlaceDatabase.class, "marketplace-database").addMigrations(Migrations.MIGRATION_1_2).build();
    }

    @Provides
    public static SteamMarketPlaceWatcherApplication provideMarketPlaceWatcher(@ApplicationContext Context application) {
        return (SteamMarketPlaceWatcherApplication) application;
    }
}
