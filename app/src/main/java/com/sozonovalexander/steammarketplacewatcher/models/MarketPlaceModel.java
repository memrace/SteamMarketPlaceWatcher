package com.sozonovalexander.steammarketplacewatcher.models;

import java.net.URISyntaxException;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableEmitter;
import io.reactivex.rxjava3.core.Single;
import lombok.NonNull;

public class MarketPlaceModel {

    private final SteamMarketPlaceApi _steamMarketPlaceApi;

    @Inject
    MarketPlaceModel(@NonNull SteamMarketPlaceApi steamMarketPlaceApi) {
        _steamMarketPlaceApi = steamMarketPlaceApi;
    }

    Single<ItemPriceInfo> getItemPriceIfo(@NonNull SteamAppId steamAppId, @NonNull Currency currency, @NonNull String marketHashName) {
        return _steamMarketPlaceApi.getPriceInfo(steamAppId.getId(), currency.getValue(), marketHashName);
    }

    // example uri https://steamcommunity.com/market/listings/730/StatTrak%E2%84%A2%20M4A1-S%20%7C%20Hyper%20Beast%20(Minimal%20Wear)
    Single<ItemMarketInfo> getItemMarketInfo(@NonNull String uri) {
        if (!uri.contains("https://steamcommunity.com/market/listings")) {
            return Single.error(new URISyntaxException(uri, "Неправильная ссылка."));
        }
        var noHttpsUri = uri.substring(8);
        var blocks = noHttpsUri.split("/");
        var parsedAppId = 0;
        try {
            parsedAppId = Integer.parseInt(blocks[3]);
        } catch (NumberFormatException e) {
            return Single.error(e);
        }

        var marketHashName = blocks[4];
        SteamAppId appId;
        switch (parsedAppId) {
            case 730:
                appId = SteamAppId.CS_GO;
                break;
            case 570:
                appId = SteamAppId.DOTA_2;
                break;
            default:
                return Single.error(new IllegalArgumentException("Приложение поддерживает только CS:GO и Dota 2."));

        }
        // todo parse image && name;
        return Single.create(emitter -> emitter.onSuccess(new ItemMarketInfo(appId, marketHashName, "")));
    }

    Single<List<MarketPlaceItem>> getItems() {
        return Single.create(emitter -> emitter.onSuccess(List.of()));
    }

    Completable addItem(MarketPlaceItem item) {
        return Completable.create(CompletableEmitter::onComplete);
    }

    Completable deleteItem(String id) {
        return Completable.create(CompletableEmitter::onComplete);
    }

    Completable updateItems() {
        return Completable.create(CompletableEmitter::onComplete);
    }
}
