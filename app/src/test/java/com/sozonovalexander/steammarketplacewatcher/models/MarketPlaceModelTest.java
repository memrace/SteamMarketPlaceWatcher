package com.sozonovalexander.steammarketplacewatcher.models;

import com.sozonovalexander.steammarketplacewatcher.dal.MarketPlaceDatabase;
import com.sozonovalexander.steammarketplacewatcher.network.SteamMarketPlaceApi;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.net.URISyntaxException;

import io.reactivex.rxjava3.observers.TestObserver;

@RunWith(MockitoJUnitRunner.class)
public class MarketPlaceModelTest {
    @Mock(stubOnly = true)
    private SteamMarketPlaceApi _steamMarketPlaceApiMock;
    private MarketPlaceModel _marketPlaceModelTest;
    private final TestObserver<ItemMarketInfo> testObserver = new TestObserver<>();
    private final String _testUri = "https://steamcommunity.com/market/listings/730/StatTrak%E2%84%A2%20M4A1-S%20%7C%20Hyper%20Beast%20(Minimal%20Wear)";
    @Mock(stubOnly = true)
    private MarketPlaceDatabase _db;

    @Before
    public void setUp() {
        _marketPlaceModelTest = new MarketPlaceModel(_steamMarketPlaceApiMock, _db);
    }

    @Test
    public void getItemMarketInfo_positive() {
        var result = _marketPlaceModelTest.getItemMarketInfo(_testUri);
        result.blockingSubscribe(testObserver);
        testObserver.assertComplete();
        testObserver.assertNoErrors();
        testObserver.assertValue(
                itemMarketInfo -> itemMarketInfo.getMarketHashName().equals("StatTrak%E2%84%A2%20M4A1-S%20%7C%20Hyper%20Beast%20(Minimal%20Wear)")
                        && itemMarketInfo.getSteamAppId().equals(SteamAppId.CS_GO)
                        && itemMarketInfo.getImageUri().equals("https://community.cloudflare.steamstatic.com/economy/image/-9a81dlWLwJ2UUGcVs_nsVtzdOEdtWwKGZZLQHTxDZ7I56KU0Zwwo4NUX4oFJZEHLbXH5ApeO4YmlhxYQknCRvCo04DEVlxkKgpou-6kejhz2v_Nfz5H_uO1gb-Gw_alDLPIhm5D18d0i_rVyoD8j1yglB89IT6mOoWUegM-aFvX_Fe_yO3q1Ja6vsnMn3Q163YntH6Lnxfh1UpFbrdng_SACQLJQIlmyYc/360fx360f")
                        && itemMarketInfo.getName().equals("StatTrak™ M4A1-S | Hyper Beast (Minimal Wear)")
        );
    }

    @Test
    public void getItemMarketInfo_negative_illegal_uri() {
        var result = _marketPlaceModelTest.getItemMarketInfo("illegal_uri");
        result.subscribe(testObserver);
        testObserver.assertError(URISyntaxException.class);
    }

    @Test
    public void getItemMarketInfo_negative_illegal_appId() {
        var result = _marketPlaceModelTest.getItemMarketInfo(_testUri.replace("730", "test"));
        result.subscribe(testObserver);
        testObserver.assertError(NumberFormatException.class);
    }

    @Test
    public void getItemMarketInfo_negative_not_supporting_game() {
        var result = _marketPlaceModelTest.getItemMarketInfo(_testUri.replace("730", "000"));
        result.subscribe(testObserver);
        testObserver.assertError(IllegalArgumentException.class);
    }
}