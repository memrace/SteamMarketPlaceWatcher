package com.sozonovalexander.steammarketplacewatcher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.sozonovalexander.steammarketplacewatcher.dal.MarketPlaceDatabase;
import com.sozonovalexander.steammarketplacewatcher.dal.MarketPlaceItemDao;
import com.sozonovalexander.steammarketplacewatcher.dal.MarketPlaceItemEntity;
import com.sozonovalexander.steammarketplacewatcher.dal.UserSettingsDao;
import com.sozonovalexander.steammarketplacewatcher.dal.UserSettingsEntity;
import com.sozonovalexander.steammarketplacewatcher.models.Currency;
import com.sozonovalexander.steammarketplacewatcher.models.SteamAppId;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class MarketPlaceDatabaseTest {
    private MarketPlaceItemDao marketPlaceItemDao;
    private UserSettingsDao userSettingsDao;
    private MarketPlaceDatabase db;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, MarketPlaceDatabase.class).build();
        marketPlaceItemDao = db.marketPlaceItemDao();
        userSettingsDao = db.userSettingsDao();
    }

    @After
    public void closeDb() {
        db.close();
    }

    private MarketPlaceItemEntity getTestItem(int param) {
        MarketPlaceItemEntity item = new MarketPlaceItemEntity();
        item.id = param;
        item.steamAppId = SteamAppId.CS_GO;
        item.imageUri = String.valueOf(param);
        item.name = String.valueOf(param);
        item.lowestPrice = String.valueOf(param);
        item.medianPrice = String.valueOf(param);
        item.hashMarketName = String.valueOf(param);
        return item;
    }

    @Test
    public void writeSettingsAndRead() {
        var settings = new UserSettingsEntity();
        settings.currency = Currency.USD;
        userSettingsDao.insertUserSettings(settings).blockingAwait(1000, TimeUnit.MILLISECONDS);
        var settingsFromDb = userSettingsDao.getUserSettings(settings.userId).blockingGet();
        assertEquals(settings.currency, settingsFromDb.currency);
    }

    @Test
    public void updateSettings() {
        var settings = new UserSettingsEntity();
        settings.currency = Currency.USD;
        userSettingsDao.insertUserSettings(settings).blockingAwait(1000, TimeUnit.MILLISECONDS);
        settings.currency = Currency.RUB;
        userSettingsDao.updateUserSettings(settings).blockingAwait(1000, TimeUnit.MILLISECONDS);
        var settingsFromDb = userSettingsDao.getUserSettings(settings.userId).blockingGet();
        assertEquals(settings.currency, settingsFromDb.currency);
    }

    @Test
    public void getSettings_failure_no_settings() {
        var settingsFromDb = userSettingsDao.getUserSettings(1).blockingGet();
        assertNull(settingsFromDb);
    }

    @Test
    public void writeItemAndReadInList() {
        var item = getTestItem(1);
        marketPlaceItemDao.addMarketPlaceItem(item).blockingAwait(1000, TimeUnit.MILLISECONDS);
        List<MarketPlaceItemEntity> items = marketPlaceItemDao.getMarketPlaceItems().blockingFirst();
        assertTrue(items.stream().anyMatch(i -> i.id == item.id));
    }

    @Test
    public void updateItem() {
        var item = getTestItem(2);
        marketPlaceItemDao.addMarketPlaceItem(item).blockingAwait(1000, TimeUnit.MILLISECONDS);
        item.name = "22";
        marketPlaceItemDao.updateMarketPlaceItem(item).blockingAwait(1000, TimeUnit.MILLISECONDS);
        List<MarketPlaceItemEntity> items = marketPlaceItemDao.getMarketPlaceItems().blockingFirst();
        var updatedItem = items.stream().filter(i -> i.id == 2).findFirst().get();
        assertEquals("22", updatedItem.name);
    }

    @Test
    public void deleteItem() {
        var item = getTestItem(2);
        marketPlaceItemDao.addMarketPlaceItem(item).blockingAwait(1000, TimeUnit.MILLISECONDS);
        List<MarketPlaceItemEntity> items = marketPlaceItemDao.getMarketPlaceItems().blockingFirst();
        var beforeSize = items.size();
        marketPlaceItemDao.deleteMarketPlaceItem(items.get(0)).blockingAwait(1000, TimeUnit.MILLISECONDS);
        items = marketPlaceItemDao.getMarketPlaceItems().blockingFirst();
        var afterSize = items.size();
        assertNotEquals(beforeSize, afterSize);
    }
}
