package com.sozonovalexander.steammarketplacewatcher.models;

import lombok.Getter;

/**
 * Представляет информацию об итеме на торговой площадке.
 */
public class ItemMarketInfo {
    @Getter
    private final SteamAppId steamAppId;
    @Getter
    private final CharSequence marketHashName;
    @Getter
    private final String imageUri;
    @Getter
    private final String name;

    ItemMarketInfo(SteamAppId steamAppId, CharSequence marketHashName, String imageUri, String name) {
        this.steamAppId = steamAppId;
        this.marketHashName = marketHashName;
        this.imageUri = imageUri;
        this.name = name;
    }
}
