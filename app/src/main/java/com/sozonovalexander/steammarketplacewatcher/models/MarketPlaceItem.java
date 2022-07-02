package com.sozonovalexander.steammarketplacewatcher.models;

import java.io.Serializable;
import java.net.URI;
import java.util.Date;
import java.util.Objects;

import lombok.Getter;
import lombok.NonNull;

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
    public Date creationDate = new Date();

    public MarketPlaceItem(@NonNull String imageUri,
                           @NonNull String name,
                           @NonNull String hashMarketName,
                           @NonNull String lowestPrice,
                           @NonNull String medianPrice,
                           @NonNull SteamAppId steamAppId) {
        this.imageUri = URI.create(imageUri);
        this.name = name;
        this.hashMarketName = hashMarketName;
        this.lowestPrice = lowestPrice;
        this.medianPrice = medianPrice;
        this.steamAppId = steamAppId;
        this.id = (hashMarketName + steamAppId.getId()).hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarketPlaceItem that = (MarketPlaceItem) o;
        return id == that.id && imageUri.equals(that.imageUri) && name.equals(that.name) && hashMarketName.equals(that.hashMarketName) && lowestPrice.equals(that.lowestPrice) && medianPrice.equals(that.medianPrice) && steamAppId == that.steamAppId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, imageUri, name, hashMarketName, lowestPrice, medianPrice, steamAppId);
    }

    public String getItemUrl() {
        return String.format("https://steamcommunity.com/market/listings/%s/%s", steamAppId, hashMarketName);
    }
}

