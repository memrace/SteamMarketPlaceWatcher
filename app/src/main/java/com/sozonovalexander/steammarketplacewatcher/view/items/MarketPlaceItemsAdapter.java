package com.sozonovalexander.steammarketplacewatcher.view.items;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.android.material.card.MaterialCardView;
import com.sozonovalexander.steammarketplacewatcher.R;
import com.sozonovalexander.steammarketplacewatcher.models.MarketPlaceItem;

import java.util.Objects;

public class MarketPlaceItemsAdapter extends ListAdapter<MarketPlaceItem, MarketPlaceItemsAdapter.MarketPlaceItemsViewHolder> {
    private final OnMarketItemClickListener onMarketItemClickListener;

    static class MarketItemDiffUtil extends DiffUtil.ItemCallback<MarketPlaceItem> {

        @Override
        public boolean areItemsTheSame(@NonNull MarketPlaceItem oldItem, @NonNull MarketPlaceItem newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull MarketPlaceItem oldItem, @NonNull MarketPlaceItem newItem) {
            return oldItem.equals(newItem);
        }
    }

    public interface OnMarketItemClickListener {
        void onGoToMarketButtonClick(MarketPlaceItem item);

        void onCardClick(MarketPlaceItem item, View v);
    }

    public MarketPlaceItemsAdapter(@NonNull MarketItemDiffUtil diff, @NonNull OnMarketItemClickListener listener) {
        super(diff);
        onMarketItemClickListener = listener;
    }

    @NonNull
    @Override
    public MarketPlaceItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.market_item_view_holder, parent, false);
        MarketPlaceItemsViewHolder holder = new MarketPlaceItemsViewHolder(view);
        holder.mGoToMarketView.setOnClickListener(btn -> {
            var position = holder.getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                var item = getCurrentList().get(position);
                onMarketItemClickListener.onGoToMarketButtonClick(item);
            }
        });
        holder.mCardView.setOnClickListener(card -> {
            var position = holder.getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                var item = getCurrentList().get(position);
                onMarketItemClickListener.onCardClick(item, view);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MarketPlaceItemsViewHolder holder, int position) {
        holder.bind(getCurrentList().get(position));
    }

    public static class MarketPlaceItemsViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView mCardView = itemView.findViewById(R.id.card_view);
        private final ImageView mImageView = itemView.findViewById(R.id.image_view);
        private final Button mGoToMarketView = itemView.findViewById(R.id.go_to_market_button);
        private final TextView mNameView = itemView.findViewById(R.id.name_text_view);
        private final TextView mMedianView = itemView.findViewById(R.id.median_price_view);
        private final TextView mLowestView = itemView.findViewById(R.id.lowest_price_view);
        private final RequestManager mGlideRequestManager = Glide.with(itemView);

        public MarketPlaceItemsViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        private void bind(MarketPlaceItem item) {
            mNameView.setText(item.getName());
            mMedianView.setText(String.format("Avg price: %s", Objects.requireNonNullElse(item.getMedianPrice(), "Отсутствует")));
            mLowestView.setText(String.format("Min price: %s", Objects.requireNonNullElse(item.getLowestPrice(), "Отсутствует")));
            mGlideRequestManager.load(item.getImageUri().toString()).placeholder(R.drawable.ic_loading_48dp).error(R.drawable.ic_broken_image).into(mImageView);
        }
    }
}
