package com.sozonovalexander.steammarketplacewatcher.view.additem;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sozonovalexander.steammarketplacewatcher.SteamMarketPlaceWatcherApplication;
import com.sozonovalexander.steammarketplacewatcher.models.MarketPlaceItem;
import com.sozonovalexander.steammarketplacewatcher.models.MarketPlaceModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class MarketPlaceAddItemViewModel extends ViewModel {
    private final MarketPlaceModel mModel;
    final MutableLiveData<MarketPlaceItem> marketItem = new MutableLiveData<>(null);
    private final SteamMarketPlaceWatcherApplication app;

    @Inject
    public MarketPlaceAddItemViewModel(MarketPlaceModel model, SteamMarketPlaceWatcherApplication application) {
        mModel = model;
        app = application;
    }


    Completable addItem() {
        var item = marketItem.getValue();
        if (item == null)
            return Completable.error(new IllegalStateException("Итем торговой площадки не найден."));
        else
            return mModel.addItem(item).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    void findInfoAndPriceByUrl(String url) {
        app.executorService.execute(() -> {
            try {
                var info = mModel.getItemMarketInfo(url).blockingGet();
                var price = mModel.getItemPriceIfo(info.getSteamAppId(), info.getMarketHashName()).blockingGet();
                var item = new MarketPlaceItem(
                        info.getImageUri(),
                        info.getName(),
                        info.getMarketHashName(),
                        price.getLowestPrice(),
                        price.getMedianPrice(),
                        info.getSteamAppId());
                app.getMainHandler().post(() -> marketItem.setValue(item));
            } catch (Throwable throwable) {
                // TODO
            }
        });
    }
}