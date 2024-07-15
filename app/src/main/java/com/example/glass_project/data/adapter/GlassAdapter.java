package com.example.glass_project.data.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.glass_project.R;
import com.example.glass_project.data.model.EyeGlass;
import com.example.glass_project.detail.DetailGlassActivity;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class GlassAdapter extends RecyclerView.Adapter<GlassAdapter.GlassViewHolder> {

    private final Context context;
    private final List<EyeGlass> glassList;

    public GlassAdapter(Context context, List<EyeGlass> glassList) {
        this.context = context;
        this.glassList = glassList;
    }

    @NonNull
    @Override
    public GlassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_item_glass, parent, false);
        return new GlassViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull GlassViewHolder holder, int position) {
        EyeGlass glass = glassList.get(position);
        holder.title.setText(glass.getName());

        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
        holder.price.setText(formatter.format(glass.getPrice()) + " VND");


        holder.ratingTotal.setText(String.valueOf(glass.getRateCount()));

        if (glass.getEyeGlassImages() != null && !glass.getEyeGlassImages().isEmpty()) {
            String imageUrl = glass.getEyeGlassImages().get(0).getUrl();
            Glide.with(context)
                    .load(imageUrl)
                    .into(holder.image);
        }

        holder.itemView.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = context.getSharedPreferences("GlassDetails", Context.MODE_PRIVATE);
            @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferences.edit();

            Intent intent = new Intent(context, DetailGlassActivity.class);
            editor.putString("glass_id", String.valueOf(glass.getId()));
            editor.apply();
            intent.putExtra("glass_id", glass.getId());
            intent.putExtra("glass_type", glass.getEyeGlassTypeID());
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return glassList.size();
    }

    public static class GlassViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;
        TextView price;
        TextView ratingTotal;


        public GlassViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageView);
            title = itemView.findViewById(R.id.titleView);
            price = itemView.findViewById(R.id.priceView);
            ratingTotal = itemView.findViewById(R.id.ratingText);
        }

    }

}
