package com.sozonovalexander.steammarketplacewatcher.view.items;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sozonovalexander.steammarketplacewatcher.SteamMarketPlaceWatcherApplication;
import com.sozonovalexander.steammarketplacewatcher.dal.UserSettingsEntity;
import com.sozonovalexander.steammarketplacewatcher.models.Currency;
import com.sozonovalexander.steammarketplacewatcher.models.ItemPriceInfo;
import com.sozonovalexander.steammarketplacewatcher.models.MarketPlaceItem;
import com.sozonovalexander.steammarketplacewatcher.models.MarketPlaceModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.Getter;

@HiltViewModel
public class MarketPlaceItemsViewModel extends ViewModel {
    private final MarketPlaceModel mModel;
    public final MutableLiveData<UserSettingsEntity> userSettings = new MutableLiveData<>(null);
    @Getter
    private final ArrayList<MarketPlaceItem> _items = new ArrayList<>();

    @Inject
    public MarketPlaceItemsViewModel(MarketPlaceModel model) {
        mModel = model;

        SteamMarketPlaceWatcherApplication.executorService.execute(() -> {
            var settings = model.getUserSettings(1).blockingGet();
            if (settings == null) {
                settings = new UserSettingsEntity();
                settings.currency = Currency.USD;
                mModel.addUserSettings(settings).blockingAwait();
            }
            UserSettingsEntity finalSettings = settings;
            mModel.setCurrency(finalSettings.currency);
            SteamMarketPlaceWatcherApplication.getMainHandler().post(() -> userSettings.setValue(finalSettings));
        });
    }

    public void updateCurrency(Currency pendingCurrency) {
        SteamMarketPlaceWatcherApplication.executorService.execute(() -> {
            mModel.updateUserSettings(userSettings.getValue()).blockingAwait();
            mModel.setCurrency(pendingCurrency);
            var updatedItems = _items.stream().map(oldItem -> {
                var newPrice = getNewItemPrice(oldItem).blockingGet();
                var marketPlaceItem = new MarketPlaceItem(
                        oldItem.getImageUri().toString(),
                        oldItem.getName(),
                        oldItem.getHashMarketName(),
                        newPrice.getLowestPrice(),
                        newPrice.getMedianPrice(),
                        oldItem.getSteamAppId());
                marketPlaceItem.creationDate = oldItem.creationDate;
                return marketPlaceItem;
            }).collect(Collectors.toList());
            updateItems(updatedItems).blockingAwait();
        });
    }

    public Flowable<List<MarketPlaceItem>> getUserItems() {
        return mModel.getItems().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    private Completable updateItems(List<MarketPlaceItem> items) {
        return mModel.updateItems(items).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    private Single<ItemPriceInfo> getNewItemPrice(MarketPlaceItem item) {
        return mModel.getItemPriceIfo(item.getSteamAppId(), item.getHashMarketName()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public void deleteItem(MarketPlaceItem item) {
        SteamMarketPlaceWatcherApplication.executorService.execute(() -> {
            mModel.deleteItem(item).blockingAwait();
        });
    }
}