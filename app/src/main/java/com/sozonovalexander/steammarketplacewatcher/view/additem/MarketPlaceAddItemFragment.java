package com.sozonovalexander.steammarketplacewatcher.view.additem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.sozonovalexander.steammarketplacewatcher.R;
import com.sozonovalexander.steammarketplacewatcher.view.MainActivity;

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        var menu = ((MainActivity) requireActivity()).getToolbar();
        menu.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                var menuItem = menu.add(Menu.NONE, 1, Menu.NONE, "Готово");
                mViewModel.marketItem.observe(getViewLifecycleOwner(), item -> {
                    menuItem.setVisible(item != null);
                });
                menuItem.setIcon(R.drawable.ic_done_40);
                menuItem.setShowAsActionFlags(1);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == 1) {
                    mViewModel.addItem().subscribe(() -> ((MainActivity) requireActivity()).getNavController().popBackStack(), (throwable) -> {
                        // TODO
                    });
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner());

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MarketPlaceAddItemViewModel.class);
    }

}