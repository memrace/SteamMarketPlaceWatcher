package com.sozonovalexander.steammarketplacewatcher.view.items;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.sozonovalexander.steammarketplacewatcher.R;
import com.sozonovalexander.steammarketplacewatcher.models.Currency;
import com.sozonovalexander.steammarketplacewatcher.models.MarketPlaceItem;
import com.sozonovalexander.steammarketplacewatcher.view.FragmentObserver;
import com.sozonovalexander.steammarketplacewatcher.view.MainActivity;

import java.util.Arrays;
import java.util.stream.Collectors;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MarketPlaceItemsFragment extends FragmentObserver implements MarketPlaceItemsAdapter.OnMarketItemClickListener {

    private MarketPlaceItemsViewModel mViewModel;
    private TextInputLayout mSelectCurrency;
    private AutoCompleteTextView mSelectionView;
    private SwipeRefreshLayout mRefreshLayout;
    private FloatingActionButton mFab;
    private final MarketPlaceItemsAdapter mItemsAdapter = new MarketPlaceItemsAdapter(new MarketPlaceItemsAdapter.MarketItemDiffUtil(), this);

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        var view = inflater.inflate(R.layout.fragment_market_place_items, container, false);
        mSelectCurrency = ((MainActivity) requireActivity()).getSelectCurrency();
        mSelectCurrency.setVisibility(View.VISIBLE);
        mSelectionView = (AutoCompleteTextView) mSelectCurrency.getEditText();
        mFab = view.findViewById(R.id.add_new_market_item_button);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRefreshLayout = view.findViewById(R.id.swipe_refresh);
        mRefreshLayout.setOnRefreshListener(() -> subscribeOnCompletableWithLifecycle(mViewModel.refreshItems(), () -> mRefreshLayout.setRefreshing(false), throwable -> {
        }));
        var items = Arrays.stream(Currency.values()).map(Enum::name).collect(Collectors.toList());
        ArrayAdapter<String> mCurrencyAdapter = new ArrayAdapter<>(requireContext(), R.layout.list_item, items);
        mSelectionView.setAdapter(mCurrencyAdapter);
        mSelectionView.setOnItemClickListener((adapterView, view1, i, l) -> mViewModel.updateCurrency(Currency.values()[i]));
        mViewModel.userSettings.observe(getViewLifecycleOwner(), (s) -> {
            if (s != null)
                mSelectionView.setText(s.currency.name(), false);
        });
        final RecyclerView itemsRecycler = view.findViewById(R.id.items_recycler);
        itemsRecycler.setAdapter(mItemsAdapter);
        subscribeOnFlowableWithLifecycle(mViewModel.getUserItems(), list -> {
            mViewModel.set_items(list);
            mItemsAdapter.submitList(list);
        }, (error) -> {
        });
        mFab.setOnClickListener(button -> {
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

    @Override
    public void onGoToMarketButtonClick(MarketPlaceItem item) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(item.getItemUrl()));
        startActivity(i);
    }

    @Override
    public void onCardClick(MarketPlaceItem item, View v) {
        showMenu(v, item);
    }

    private void showMenu(View v, MarketPlaceItem item) {
        PopupMenu popup = new PopupMenu(requireContext(), v, Gravity.CENTER);
        popup.getMenuInflater().inflate(R.menu.item_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId() == R.id.delete_item) {
                mViewModel.deleteItem(item);
            }
            return false;
        });
        popup.show();
    }
}