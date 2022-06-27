package com.sozonovalexander.steammarketplacewatcher.view.additem;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sozonovalexander.steammarketplacewatcher.R;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MarketPlaceAddItemFragment extends Fragment {

    private MarketPlaceAddItemViewModel mViewModel;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_market_place_add_item, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MarketPlaceAddItemViewModel.class);
    }

}