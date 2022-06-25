package com.sozonovalexander.steammarketplacewatcher.models;

import java.net.URI;

import lombok.Getter;

public class MarketPlaceItem {
    @Getter
    private final String id;
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
    private final Currency currency;

    public MarketPlaceItem(String id,
                           String imageUri,
                           String name,
                           String hashMarketName,
                           String lowestPrice,
                           String medianPrice,
                           Currency currency) {
        this.id = id;
        this.imageUri = URI.create(imageUri);
        this.name = name;
        this.hashMarketName = hashMarketName;
        this.lowestPrice = lowestPrice;
        this.medianPrice = medianPrice;
        this.currency = currency;
    }
}

