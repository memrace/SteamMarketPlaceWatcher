package com.sozonovalexander.steammarketplacewatcher.models;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableEmitter;
import io.reactivex.rxjava3.core.Single;
import lombok.NonNull;

/**
 * Представляет модель итема на торговой площадке.
 */
public class MarketPlaceModel {

    private final SteamMarketPlaceApi _steamMarketPlaceApi;

    @Inject
    MarketPlaceModel(SteamMarketPlaceApi steamMarketPlaceApi) {
        _steamMarketPlaceApi = steamMarketPlaceApi;
    }

    Single<ItemPriceInfo> getItemPriceIfo(@NonNull SteamAppId steamAppId, @NonNull Currency currency, @NonNull String marketHashName) {
        return _steamMarketPlaceApi.getPriceInfo(steamAppId.getId(), currency.getValue(), marketHashName);
    }

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
        return Single.create(emitter -> {
            Thread t = new Thread(() -> {
                try {
                    Document document = Jsoup.connect(uri).get();
                    final Element imageElement = Objects.requireNonNull(document.selectFirst("div.market_listing_largeimage")).selectFirst("img");
                    assert imageElement != null;
                    final String imageSrc = imageElement.attr("src");
                    final Element nameElement = document.selectFirst("span.market_listing_item_name");
                    assert nameElement != null;
                    final String itemName = nameElement.text();
                    emitter.onSuccess(new ItemMarketInfo(appId, marketHashName, imageSrc, itemName));
                } catch (IOException e) {
                    emitter.onError(new IOException("Во время загрузки произошла ошибка."));
                }
            });
            t.start();
        });
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
