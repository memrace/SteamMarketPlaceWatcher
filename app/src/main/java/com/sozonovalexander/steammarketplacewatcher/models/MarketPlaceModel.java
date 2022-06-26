package com.sozonovalexander.steammarketplacewatcher.models;

import com.sozonovalexander.steammarketplacewatcher.dal.MarketPlaceDatabase;
import com.sozonovalexander.steammarketplacewatcher.dal.MarketPlaceItemEntity;
import com.sozonovalexander.steammarketplacewatcher.network.SteamMarketPlaceApi;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Представляет модель итема на торговой площадке.
 */
public class MarketPlaceModel {

    private final SteamMarketPlaceApi _steamMarketPlaceApi;
    private final MarketPlaceDatabase _marketPlaceDatabase;

    @Getter
    @Setter
    private Currency currency = Currency.USD;

    @Inject
    MarketPlaceModel(SteamMarketPlaceApi steamMarketPlaceApi, MarketPlaceDatabase marketPlaceDatabase) {
        _steamMarketPlaceApi = steamMarketPlaceApi;
        _marketPlaceDatabase = marketPlaceDatabase;
    }

    public Single<ItemPriceInfo> getItemPriceIfo(@NonNull SteamAppId steamAppId, @NonNull String marketHashName) {
        return _steamMarketPlaceApi.getPriceInfo(steamAppId.getId(), currency.getValue(), marketHashName);
    }

    public Single<ItemMarketInfo> getItemMarketInfo(@NonNull String uri) {
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

    public Single<List<MarketPlaceItem>> getItems() {
        return _marketPlaceDatabase.marketPlaceItemDao()
                .getMarketPlaceItems()
                .map(items -> items.stream()
                        .map(item -> new MarketPlaceItem(
                                item.imageUri,
                                item.name,
                                item.hashMarketName,
                                item.lowestPrice,
                                item.medianPrice,
                                item.steamAppId)).collect(Collectors.toList()));
    }

    public Completable addItem(MarketPlaceItem item) {
        return _marketPlaceDatabase.marketPlaceItemDao().addMarketPlaceItem(mapItemToEntity(item));
    }

    public Completable deleteItem(MarketPlaceItem item) {
        return _marketPlaceDatabase.marketPlaceItemDao().deleteMarketPlaceItem(mapItemToEntity(item));
    }

    public Completable updateItem(MarketPlaceItem item) {
        return _marketPlaceDatabase.marketPlaceItemDao().updateMarketPlaceItem(mapItemToEntity(item));
    }

    public Completable updateItems(List<MarketPlaceItem> items) {
        return _marketPlaceDatabase
                .marketPlaceItemDao()
                .updateMarketPlaceItems(items.stream().map(MarketPlaceModel::mapItemToEntity).collect(Collectors.toList()));
    }

    private static MarketPlaceItemEntity mapItemToEntity(MarketPlaceItem item) {
        var entity = new MarketPlaceItemEntity();
        entity.id = item.getId();
        entity.imageUri = item.getImageUri().toString();
        entity.lowestPrice = item.getLowestPrice();
        entity.medianPrice = item.getMedianPrice();
        entity.steamAppId = item.getSteamAppId();
        entity.hashMarketName = item.getHashMarketName();
        return entity;
    }
}
