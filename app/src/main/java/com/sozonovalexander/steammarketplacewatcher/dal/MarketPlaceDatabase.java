package com.sozonovalexander.steammarketplacewatcher.dal;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {MarketPlaceItemEntity.class}, version = 1)
public abstract class MarketPlaceDatabase extends RoomDatabase {
    public abstract MarketPlaceItemDao marketPlaceItemDao();
}
