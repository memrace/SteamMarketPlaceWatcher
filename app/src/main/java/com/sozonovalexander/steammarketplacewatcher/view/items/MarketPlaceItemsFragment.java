package com.sozonovalexander.steammarketplacewatcher.view.items;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.sozonovalexander.steammarketplacewatcher.R;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MarketPlaceItemsFragment extends Fragment {

    private MarketPlaceItemsViewModel mViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_market_place_items, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MarketPlaceItemsViewModel.class);
    }
}