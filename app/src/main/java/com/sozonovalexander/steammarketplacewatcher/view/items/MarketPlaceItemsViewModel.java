package com.sozonovalexander.steammarketplacewatcher.view.items;

import android.net.Uri;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sozonovalexander.steammarketplacewatcher.SteamMarketPlaceWatcher;
import com.sozonovalexander.steammarketplacewatcher.dal.UserSettingsEntity;
import com.sozonovalexander.steammarketplacewatcher.models.Currency;
import com.sozonovalexander.steammarketplacewatcher.models.ItemPriceInfo;
import com.sozonovalexander.steammarketplacewatcher.models.MarketPlaceItem;
import com.sozonovalexander.steammarketplacewatcher.models.MarketPlaceModel;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import lombok.Getter;
import lombok.Setter;

@HiltViewModel
public class MarketPlaceItemsViewModel extends ViewModel {
    private final MarketPlaceModel mModel;
    private UserSettingsEntity _settings;
    public final MutableLiveData<UserSettingsEntity> userSettings = new MutableLiveData<>();
    private final SteamMarketPlaceWatcher app;
    @Getter
    @Setter
    private List<MarketPlaceItem> _items;

    @Inject
    public MarketPlaceItemsViewModel(SteamMarketPlaceWatcher application, MarketPlaceModel model) {
        mModel = model;
        app = application;
        app.executorService.execute(() -> {
            var settings = model.getUserSettings(1).blockingGet();
            var info = model.getItemMarketInfo(Uri.parse("https://steamcommunity.com/market/listings/730/StatTrak%E2%84%A2%20M4A1-S%20%7C%20Hyper%20Beast%20(Minimal%20Wear)")).blockingGet();
            var price = model.getItemPriceIfo(info.getSteamAppId(), info.getMarketHashName()).blockingGet();
            var item = new MarketPlaceItem(info.getImageUri(), info.getName(), info.getMarketHashName(), price.getLowestPrice(), price.getMedianPrice(), info.getSteamAppId());
            model.addItem(item).blockingAwait();
            if (settings == null) {
                _settings = new UserSettingsEntity();
                _settings.currency = Currency.USD;
                mModel.addUserSettings(_settings).blockingAwait();
                mModel.setCurrency(_settings.currency);
            } else {
                _settings = settings;
            }
            app.getMainHandler().post(() -> userSettings.setValue(_settings));
        });
    }


    public void updateCurrency(Currency pendingCurrency) {
        app.executorService.execute(() -> {
            _settings.currency = pendingCurrency;
            mModel.updateUserSettings(_settings).blockingAwait();
            mModel.setCurrency(_settings.currency);
            if (_items != null) {
                var updatedItems = _items.stream().map(oldItem -> {
                    var newPrice = getNewItemPrice(oldItem).blockingGet();
                    return new MarketPlaceItem(
                            oldItem.getImageUri().toString(),
                            oldItem.getName(),
                            oldItem.getHashMarketName(),
                            newPrice.getLowestPrice(),
                            newPrice.getMedianPrice(),
                            oldItem.getSteamAppId());
                }).collect(Collectors.toList());
                updateItems(updatedItems).blockingAwait();
            }
        });
    }

    public Flowable<List<MarketPlaceItem>> getUserItems() {
        return mModel.getItems();
    }

    private Completable updateItems(List<MarketPlaceItem> items) {
        return mModel.updateItems(items);
    }

    private Single<ItemPriceInfo> getNewItemPrice(MarketPlaceItem item) {
        return mModel.getItemPriceIfo(item.getSteamAppId(), item.getHashMarketName());
    }
}