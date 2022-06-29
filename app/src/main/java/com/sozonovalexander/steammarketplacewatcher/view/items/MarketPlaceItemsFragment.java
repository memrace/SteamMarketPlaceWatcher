package com.sozonovalexander.steammarketplacewatcher.view.items;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputLayout;
import com.sozonovalexander.steammarketplacewatcher.R;
import com.sozonovalexander.steammarketplacewatcher.models.Currency;
import com.sozonovalexander.steammarketplacewatcher.view.FragmentObserver;
import com.sozonovalexander.steammarketplacewatcher.view.MainActivity;

import java.util.Arrays;
import java.util.stream.Collectors;

import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class MarketPlaceItemsFragment extends FragmentObserver {

    private MarketPlaceItemsViewModel mViewModel;
    private ArrayAdapter<String> adapter;
    private TextInputLayout mAppBarMenu;
    private AutoCompleteTextView mSelectionView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        var view = inflater.inflate(R.layout.fragment_market_place_items, container, false);
        mAppBarMenu = ((MainActivity) requireActivity()).getAppBarMenu();
        mAppBarMenu.setVisibility(View.VISIBLE);
        mSelectionView = (AutoCompleteTextView) mAppBarMenu.getEditText();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        var items = Arrays.stream(Currency.values()).map(Enum::name).collect(Collectors.toList());
        adapter = new ArrayAdapter<>(requireContext(), R.layout.list_item, items);
        mSelectionView.setAdapter(adapter);
        mSelectionView.setOnItemClickListener((adapterView, view1, i, l) -> mViewModel.updateCurrency(Currency.values()[i]));
        mViewModel.userSettings.observe(getViewLifecycleOwner(), (s) -> mSelectionView.setText(s.currency.name(), false));
        subscribeOnFlowableWithLifecycle(mViewModel.getUserItems(), (result) -> {
            mViewModel.set_items(result);
        }, (error) -> {
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MarketPlaceItemsViewModel.class);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAppBarMenu.setVisibility(View.GONE);
    }
}