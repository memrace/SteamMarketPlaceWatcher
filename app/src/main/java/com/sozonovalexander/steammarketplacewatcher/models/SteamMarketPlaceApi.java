package com.sozonovalexander.steammarketplacewatcher.models;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SteamMarketPlaceApi {

    // http://steamcommunity.com/market/priceoverview/?appid=730&currency=3&market_hash_name=StatTrak%E2%84%A2 M4A1-S | Hyper Beast (Minimal Wear)

    @GET("priceoverview")
    Single<ItemPriceInfo> getPriceInfo(@Query("appid") int appId, @Query("currency") byte currency, @Query("market_hash_name") String marketHashName);
}
