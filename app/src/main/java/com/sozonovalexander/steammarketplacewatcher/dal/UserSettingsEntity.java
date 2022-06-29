package com.sozonovalexander.steammarketplacewatcher.dal;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.sozonovalexander.steammarketplacewatcher.models.Currency;

@Entity
public class UserSettingsEntity {

    @PrimaryKey
    public int userId = 1;

    @ColumnInfo()
    public Currency currency;
}
