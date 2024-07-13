package com.example.glass_project.data.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.glass_project.DTO.CartDTO.CartDetailResponse;
import com.example.glass_project.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.ViewHolder> {

    private final Context context;
    private final List<CartDetailResponse> cartDetailsList;

    public OrderDetailAdapter(Context context, List<CartDetailResponse> cartDetailsList) {
        this.context = context;
        this.cartDetailsList = cartDetailsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_detail, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartDetailResponse cartDetail = cartDetailsList.get(position);

        Log.d("CartDetail", "onBindViewHolder: " + cartDetail.getEyeGlassName());

        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
        String formattedEyeGlassPrice = formatter.format(cartDetail.getEyeGlassPrice());
        String formattedLensPrice = formatter.format(cartDetail.getLensPrice());

        holder.textGlassName.setText(cartDetail.getEyeGlassName());
        holder.textGlassPrice.setText(String.format("Price: %s VND", formattedEyeGlassPrice));
        holder.textLensType.setText(String.format("Lens: %s x %d", cartDetail.getLensName(), cartDetail.getQuantity()));
        holder.textLensPrice.setText(String.format("Lens Price: %s VND", formattedLensPrice));



        if (cartDetail.getEyeGlassImages() != null && !cartDetail.getEyeGlassImages().isEmpty()) {
            String imageUrl = cartDetail.getEyeGlassImages().get(0).getUrl();
            Glide.with(context)
                    .load(imageUrl)
                    .into(holder.imageGlasses);
        }

        // Uncomment if needed
        // holder.buttonDelete.setOnClickListener(v -> {
        //     cartDetailsList.remove(position);
        //     notifyItemRemoved(position);
        // });

        // holder.buttonMinus.setOnClickListener(v -> {
        //     int quantity = cartDetail.getQuantity();
        //     if (quantity > 1) {
        //         cartDetail.setQuantity(quantity - 1);
        //         notifyItemChanged(position);
        //     }
        // });

        // holder.buttonPlus.setOnClickListener(v -> {
        //     int quantity = cartDetail.getQuantity();
        //     cartDetail.setQuantity(quantity + 1);
        //     notifyItemChanged(position);
        // });
    }

    @Override
    public int getItemCount() {
        return cartDetailsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageGlasses;
        TextView textGlassName, textLensType, textGlassPrice, textLensPrice;
        Button buttonDelete, buttonMinus, buttonPlus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageGlasses = itemView.findViewById(R.id.imageGlasses);
            textGlassName = itemView.findViewById(R.id.textGlassName);
            textLensType = itemView.findViewById(R.id.textLensType);
            textGlassPrice = itemView.findViewById(R.id.textGlassPrice);
            textLensPrice = itemView.findViewById(R.id.textLensPrice);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            buttonMinus = itemView.findViewById(R.id.buttonMinus);
            buttonPlus = itemView.findViewById(R.id.buttonPlus);
        }
    }
}
