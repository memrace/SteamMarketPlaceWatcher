package com.sozonovalexander.steammarketplacewatcher.models;

import java.io.Serializable;
import java.net.URI;

import lombok.Getter;

/**
 * Представляет итем на торговой площадке.
 */
public class MarketPlaceItem implements Serializable {
    @Getter
    private final int id;
    @Getter
    private final URI imageUri;
    @Getter
    private final String name;
    @Getter
    private final String hashMarketName;
    @Getter
    private final String lowestPrice;
    @Getter
    private final String medianPrice;
    @Getter
    private final SteamAppId steamAppId;

    public MarketPlaceItem(String imageUri,
                           String name,
                           String hashMarketName,
                           String lowestPrice,
                           String medianPrice,
                           SteamAppId steamAppId) {
        this.imageUri = URI.create(imageUri);
        this.name = name;
        this.hashMarketName = hashMarketName;
        this.lowestPrice = lowestPrice;
        this.medianPrice = medianPrice;
        this.steamAppId = steamAppId;
        this.id = (hashMarketName + steamAppId.getId()).hashCode();
    }
}

