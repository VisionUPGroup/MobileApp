package com.example.glass_project.data.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.glass_project.R;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    private List<String> bannerUrls;
    private Context context;

    public BannerAdapter(Context context, List<String> bannerUrls) {
        this.context = context;
        this.bannerUrls = bannerUrls;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_banner, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        String imageUrl = bannerUrls.get(position);

        // Load image using Glide
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.default_image) // Placeholder image
                .into(holder.bannerImageView);
    }

    @Override
    public int getItemCount() {
        return bannerUrls.size();
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView bannerImageView;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            bannerImageView = itemView.findViewById(R.id.bannerImageView);
        }
    }
}
