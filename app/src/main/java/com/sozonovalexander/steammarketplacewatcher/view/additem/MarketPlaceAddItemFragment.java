package com.sozonovalexander.steammarketplacewatcher.view.additem;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputLayout;
import com.sozonovalexander.steammarketplacewatcher.R;
import com.sozonovalexander.steammarketplacewatcher.view.FragmentObserver;
import com.sozonovalexander.steammarketplacewatcher.view.MainActivity;

import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MarketPlaceAddItemFragment extends FragmentObserver {
    private TextInputLayout mUrlInput;
    private MarketPlaceAddItemViewModel mViewModel;
    private Button mFindBtn;
    private TextView mNameView;
    private ImageView mItemImage;
    private TextView mMedianPriceView;
    private TextView mLowestPriceView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_market_place_add_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUrlInput = view.findViewById(R.id.item_url_input);
        mFindBtn = view.findViewById(R.id.find_btn);
        mNameView = view.findViewById(R.id.name_text_view);
        mItemImage = view.findViewById(R.id.image_view);
        mMedianPriceView = view.findViewById(R.id.median_price_view);
        mLowestPriceView = view.findViewById(R.id.lowest_price_view);
        validateUrlInputChanges();
        handleFindButton();
        handleAppBarMenu();
        handleMarketItemChanges();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MarketPlaceAddItemViewModel.class);
    }

    void handleAppBarMenu() {
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
                    subscribeOnCompletableWithLifecycle(mViewModel.addItem(), () -> ((MainActivity) requireActivity()).getNavController().popBackStack(), (throwable) -> {
                        // TODO
                    });
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner());
    }

    private void handleFindButton() {
        mViewModel.canFind.observe(getViewLifecycleOwner(), flag -> mFindBtn.setEnabled(flag));
        mFindBtn.setOnClickListener(view -> {
            mViewModel.findInfoAndPriceByUrl(Objects.requireNonNull(mUrlInput.getEditText()).getText().toString());
        });
    }

    private void handleMarketItemChanges() {
        mViewModel.marketItem.observe(getViewLifecycleOwner(), item -> {
            if (item != null) {
                mNameView.setText(item.getName());
                Glide.with(this).load(item.getImageUri().toString()).placeholder(R.drawable.ic_loading_48dp).error(R.drawable.ic_broken_image).into(mItemImage);
                mMedianPriceView.setText(String.format("Средняя цена: %s", item.getMedianPrice()));
                mLowestPriceView.setText(String.format("Минимальная цена: %s", item.getLowestPrice()));
            }
        });
    }

    private void validateUrlInputChanges() {
        var editText = Objects.requireNonNull(mUrlInput.getEditText(), "Не найдено поле ввода ссылки итема.");
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                var isValid = editable.toString().contains("https://steamcommunity.com/market/listings");
                mViewModel.canFind.setValue(isValid);
                if (isValid) {
                    mUrlInput.setError(null);
                } else {
                    mUrlInput.setError("Введите ссылку на товар на торговой площадке Steam.");
                }
            }
        });
    }

}