package com.sozonovalexander.steammarketplacewatcher.dal;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;

@Dao
public interface UserSettingsDao {

    @Query("SELECT * FROM UserSettingsEntity WHERE userId =:userId")
    Maybe<UserSettingsEntity> getUserSettings(int userId);

    @Insert
    Completable insertUserSettings(UserSettingsEntity settings);

    @Update
    Completable updateUserSettings(UserSettingsEntity settings);
}
