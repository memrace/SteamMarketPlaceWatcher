package com.sozonovalexander.steammarketplacewatcher.models;

import com.sozonovalexander.steammarketplacewatcher.dal.MarketPlaceDatabase;
import com.sozonovalexander.steammarketplacewatcher.dal.MarketPlaceItemEntity;
import com.sozonovalexander.steammarketplacewatcher.dal.UserSettingsEntity;
import com.sozonovalexander.steammarketplacewatcher.network.SteamMarketPlaceApi;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.scopes.ActivityRetainedScoped;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Представляет модель итема на торговой площадке.
 */
@ActivityRetainedScoped
public class MarketPlaceModel {

    private final SteamMarketPlaceApi _steamMarketPlaceApi;
    private final MarketPlaceDatabase _marketPlaceDatabase;

    @Getter
    @Setter
    private Currency currency = Currency.USD;

    @Inject
    public MarketPlaceModel(SteamMarketPlaceApi steamMarketPlaceApi, MarketPlaceDatabase marketPlaceDatabase) {
        _steamMarketPlaceApi = steamMarketPlaceApi;
        _marketPlaceDatabase = marketPlaceDatabase;
    }

    public Maybe<UserSettingsEntity> getUserSettings(int userId) {
        return _marketPlaceDatabase.userSettingsDao().getUserSettings(userId);
    }

    public Completable addUserSettings(UserSettingsEntity settings) {
        return _marketPlaceDatabase.userSettingsDao().insertUserSettings(settings);
    }

    public Completable updateUserSettings(UserSettingsEntity settings) {
        return _marketPlaceDatabase.userSettingsDao().updateUserSettings(settings);
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
        String marketHashName;
        try {
            marketHashName = URLDecoder.decode(blocks[4], StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            return Single.error(e);
        }
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
                emitter.onError(e);
            }
        });
    }

    public Flowable<List<MarketPlaceItem>> getItems() {
        return _marketPlaceDatabase.marketPlaceItemDao()
                .getMarketPlaceItems()
                .map(items -> items.stream()
                        .map(item -> {
                            var marketPlaceItem = new MarketPlaceItem(
                                    item.imageUri,
                                    item.name,
                                    item.hashMarketName,
                                    item.lowestPrice,
                                    item.medianPrice,
                                    item.steamAppId);
                            marketPlaceItem.creationDate = item.creationDate;
                            return marketPlaceItem;
                        }).collect(Collectors.toList()));
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
        entity.creationDate = item.creationDate;
        entity.name = item.getName();
        entity.imageUri = item.getImageUri().toString();
        entity.lowestPrice = item.getLowestPrice();
        entity.medianPrice = item.getMedianPrice();
        entity.steamAppId = item.getSteamAppId();
        entity.hashMarketName = item.getHashMarketName();
        return entity;
    }
}
