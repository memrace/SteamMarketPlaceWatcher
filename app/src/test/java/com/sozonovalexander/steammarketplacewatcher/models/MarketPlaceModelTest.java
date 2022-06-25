package com.sozonovalexander.steammarketplacewatcher.models;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.net.URISyntaxException;

import io.reactivex.rxjava3.observers.TestObserver;

@RunWith(MockitoJUnitRunner.class)
public class MarketPlaceModelTest {
    @Mock
    private SteamMarketPlaceApi _steamMarketPlaceApiMock;
    private MarketPlaceModel _marketPlaceModelTest;
    private final String _testUri = "https://steamcommunity.com/market/listings/730/StatTrak%E2%84%A2%20M4A1-S%20%7C%20Hyper%20Beast%20(Minimal%20Wear)";

    @Before
    public void setUp() {
        _steamMarketPlaceApiMock = Mockito.mock(SteamMarketPlaceApi.class);
        _marketPlaceModelTest = new MarketPlaceModel(_steamMarketPlaceApiMock);
    }

    @Test
    public void getItemMarketInfo_positive() {
        var result = _marketPlaceModelTest.getItemMarketInfo(_testUri);
        TestObserver<ItemMarketInfo> testObserver = new TestObserver<>();
        result.subscribe(testObserver);
        testObserver.assertComplete();
        testObserver.assertNoErrors();
        testObserver.assertValue(itemMarketInfo -> itemMarketInfo.getMarketHashName().equals("StatTrak%E2%84%A2%20M4A1-S%20%7C%20Hyper%20Beast%20(Minimal%20Wear)") && itemMarketInfo.getSteamAppId().equals(SteamAppId.CS_GO));
    }

    @Test
    public void getItemMarketInfo_negative_illegal_uri() {
        var result = _marketPlaceModelTest.getItemMarketInfo("illegal_uri");
        TestObserver<ItemMarketInfo> testObserver = new TestObserver<>();
        result.subscribe(testObserver);
        testObserver.assertError(URISyntaxException.class);
    }

    @Test
    public void getItemMarketInfo_negative_illegal_appId() {
        var result = _marketPlaceModelTest.getItemMarketInfo(_testUri.replace("730", "test"));
        TestObserver<ItemMarketInfo> testObserver = new TestObserver<>();
        result.subscribe(testObserver);
        testObserver.assertError(NumberFormatException.class);
    }

    @Test
    public void getItemMarketInfo_negative_not_supporting_game() {
        var result = _marketPlaceModelTest.getItemMarketInfo(_testUri.replace("730", "000"));
        TestObserver<ItemMarketInfo> testObserver = new TestObserver<>();
        result.subscribe(testObserver);
        testObserver.assertError(IllegalArgumentException.class);
    }
}