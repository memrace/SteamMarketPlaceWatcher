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
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import lombok.Getter;

@HiltViewModel
public class MarketPlaceItemsViewModel extends ViewModel {
    private final MarketPlaceModel mModel;
    private UserSettingsEntity _settings;
    public final MutableLiveData<UserSettingsEntity> userSettings = new MutableLiveData<>();
    private final SteamMarketPlaceWatcherApplication app;
    @Getter
    private final ArrayList<MarketPlaceItem> _items = new ArrayList<>();

    @Inject
    public MarketPlaceItemsViewModel(SteamMarketPlaceWatcherApplication application, MarketPlaceModel model) {
        mModel = model;
        app = application;
        app.executorService.execute(() -> {
            var settings = model.getUserSettings(1).blockingGet();
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