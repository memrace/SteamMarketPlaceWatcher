package com.sozonovalexander.steammarketplacewatcher.dal;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(version = 2, entities = {MarketPlaceItemEntity.class, UserSettingsEntity.class})
public abstract class MarketPlaceDatabase extends RoomDatabase {
    public abstract MarketPlaceItemDao marketPlaceItemDao();

    public abstract UserSettingsDao userSettingsDao();
}

