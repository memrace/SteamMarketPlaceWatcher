package com.sozonovalexander.steammarketplacewatcher.models;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;

public class ItemPriceInfo {
    @Getter
    @SerializedName("lowest_price")
    private final String lowestPrice;
    @Getter
    @SerializedName("median_price")
    private final String medianPrice;

    ItemPriceInfo(String lowestPrice, String medianPrice) {
        this.lowestPrice = lowestPrice;
        this.medianPrice = medianPrice;
    }
}
