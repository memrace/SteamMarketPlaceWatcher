package com.sozonovalexander.steammarketplacewatcher.models;

import lombok.Getter;

public class ItemMarketInfo {
    @Getter
    private final SteamAppId _steamAppId;
    @Getter
    private final String _marketHashName;
    @Getter
    private final String _imageUri;

    ItemMarketInfo(SteamAppId steamAppId, String marketHashName, String imageUri) {
        _steamAppId = steamAppId;
        _marketHashName = marketHashName;
        _imageUri = imageUri;
    }
}
