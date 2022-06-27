package com.sozonovalexander.steammarketplacewatcher;

import android.app.Application;

import androidx.room.Room;

import com.sozonovalexander.steammarketplacewatcher.dal.MarketPlaceDatabase;
import com.sozonovalexander.steammarketplacewatcher.models.MarketPlaceModel;
import com.sozonovalexander.steammarketplacewatcher.network.SteamMarketPlaceApi;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.android.scopes.ViewModelScoped;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(ActivityComponent.class)
public class HiltCommonModule {

    @Provides
    @Singleton
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
    @Singleton
    public static MarketPlaceDatabase provideMarketPlaceDatabase(@ApplicationContext Application app) {
        return Room.databaseBuilder(app, MarketPlaceDatabase.class, "marketplace-database").build();
    }

}
