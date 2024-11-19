package com.example.glass_project.data.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.glass_project.R;
import com.example.glass_project.data.model.rating.RatingEyeGlass;

import java.util.List;

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.RatingViewHolder> {

    private final Context context;
    private final List<RatingEyeGlass> ratingList;

    public RatingAdapter(Context context, List<RatingEyeGlass> ratingList) {
        this.context = context;
        this.ratingList = ratingList;
    }

    @NonNull
    @Override
    public RatingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_rating, parent, false);
        return new RatingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RatingViewHolder holder, int position) {
        RatingEyeGlass rating = ratingList.get(position);
        holder.txtName.setText(rating.getEyeGlassName());
        holder.ratingBar.setRating(rating.getRating());

        // Load image using Glide
        Glide.with(context)
                .load(rating.getImageUrl())
                .into(holder.imgProduct);

        holder.ratingBar.setOnRatingBarChangeListener((ratingBar, value, fromUser) -> {
            if (fromUser) {
                rating.setRating(value);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ratingList.size();
    }

    static class RatingViewHolder extends RecyclerView.ViewHolder {
        TextView txtName;
        ImageView imgProduct;
        RatingBar ratingBar;

        public RatingViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
}
