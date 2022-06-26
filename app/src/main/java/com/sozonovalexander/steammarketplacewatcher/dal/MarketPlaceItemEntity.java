package com.sozonovalexander.steammarketplacewatcher.dal;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.sozonovalexander.steammarketplacewatcher.models.SteamAppId;

@Entity
public class MarketPlaceItemEntity {
    @PrimaryKey
    public int id;

    @ColumnInfo(name = "image_uri")
    public String imageUri;

    @ColumnInfo()
    public String name;

    @ColumnInfo(name = "hash_market_name")
    public String hashMarketName;

    @ColumnInfo(name = "lowest_price")
    public String lowestPrice;

    @ColumnInfo(name = "median_price")
    public String medianPrice;

    @ColumnInfo(name = "steam_app_id")
    public SteamAppId steamAppId;
}
