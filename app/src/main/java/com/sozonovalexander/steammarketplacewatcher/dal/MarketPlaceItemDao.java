package com.sozonovalexander.steammarketplacewatcher.dal;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface MarketPlaceItemDao {

    @Query("SELECT * FROM MarketPlaceItemEntity")
    Single<List<MarketPlaceItemEntity>> getMarketPlaceItems();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable addMarketPlaceItem(MarketPlaceItemEntity item);

    @Update
    Completable updateMarketPlaceItem(MarketPlaceItemEntity item);

    @Update
    Completable updateMarketPlaceItems(List<MarketPlaceItemEntity> items);

    @Delete
    Completable deleteMarketPlaceItem(MarketPlaceItemEntity item);
}
