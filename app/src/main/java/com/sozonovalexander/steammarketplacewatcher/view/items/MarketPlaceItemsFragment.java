package com.sozonovalexander.steammarketplacewatcher.view.items;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
    private TextInputLayout mSelectCurrency;
    private AutoCompleteTextView mSelectionView;
    private FloatingActionButton fab;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        var view = inflater.inflate(R.layout.fragment_market_place_items, container, false);
        mSelectCurrency = ((MainActivity) requireActivity()).getSelectCurrency();
        mSelectCurrency.setVisibility(View.VISIBLE);
        mSelectionView = (AutoCompleteTextView) mSelectCurrency.getEditText();
        fab = view.findViewById(R.id.add_new_market_item_button);
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
            //  mViewModel.set_items(result);
        }, (error) -> {
        });

        fab.setOnClickListener(button -> {
            ((MainActivity) requireActivity()).getNavController().navigate(R.id.action_goToAddNewItem);
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MarketPlaceItemsViewModel.class);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSelectCurrency.setVisibility(View.GONE);
    }
}