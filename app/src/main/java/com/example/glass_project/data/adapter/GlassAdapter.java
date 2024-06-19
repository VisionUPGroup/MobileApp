package com.example.glass_project.data.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glass_project.R;
import com.example.glass_project.data.model.Glass;
import com.example.glass_project.detail.DetailGlassActivity;

import java.util.List;

public class GlassAdapter extends RecyclerView.Adapter<GlassAdapter.GlassViewHolder> {
    private Context context;
    private List<Glass> glassList;

    public GlassAdapter(Context context, List<Glass> glassList) {
        this.context = context;
        this.glassList = glassList;
    }


    @NonNull
    @Override
    public GlassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_item_glass, parent, false);
        return new GlassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GlassViewHolder holder, int position) {
        Glass glass = glassList.get(position);
        holder.title.setText(glass.getNameGlass());
        holder.price.setText("Price: $" + glass.getPrice());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailGlassActivity.class);

            }
        });
    }


    @Override
    public int getItemCount() {
        return glassList.size();
    }

    public static class GlassViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView title;
        TextView price;


        public GlassViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageView);
            title = itemView.findViewById(R.id.titleView);
            price = itemView.findViewById(R.id.priceView);
        }

    }

}
