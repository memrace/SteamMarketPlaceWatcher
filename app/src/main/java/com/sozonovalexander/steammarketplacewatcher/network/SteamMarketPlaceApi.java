package com.sozonovalexander.steammarketplacewatcher.network;

import com.sozonovalexander.steammarketplacewatcher.models.ItemPriceInfo;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Определяет API торговой площадки.
 */
public interface SteamMarketPlaceApi {

    // http://steamcommunity.com/market/priceoverview/?appid=730&currency=3&market_hash_name=StatTrak%E2%84%A2 M4A1-S | Hyper Beast (Minimal Wear)

    /**
     * Возвращает информацию о цене итема.
     *
     * @param appId          Идентификатор игры.
     * @param currency       Валюта.
     * @param marketHashName Хэш имя торговой площадки.
     */
    @GET("priceoverview")
    Single<ItemPriceInfo> getPriceInfo(@Query("appid") int appId, @Query("currency") byte currency, @Query("market_hash_name") CharSequence marketHashName);
}
