package com.example.glass_project.data.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.glass_project.R;
import com.example.glass_project.data.model.order.EyeGlassImage;
import com.example.glass_project.data.model.order.OrderDetail;

import java.text.DecimalFormat;
import java.util.List;

public class OrderDetailHistoryAdapter extends RecyclerView.Adapter<OrderDetailHistoryAdapter.ViewHolder> {

    private Context context;
    private List<OrderDetail> orderDetailHistories;
    private DecimalFormat decimalFormat = new DecimalFormat("#,###");

    public OrderDetailHistoryAdapter(Context context, List<OrderDetail> orderDetailHistories) {
        this.context = context;
        this.orderDetailHistories = orderDetailHistories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderDetail detail = orderDetailHistories.get(position);

        // Set tên sản phẩm, số lượng và giá
        holder.txtProductName.setText(detail.getProductGlass().getEyeGlass().getName());
        holder.txtProductQuantity.setText("x" + detail.getQuantity());
        holder.txtProductPrice.setText("đ" + decimalFormat.format(detail.getProductGlass().getTotal()));

        // Lấy ảnh sản phẩm từ danh sách ảnh
        List<EyeGlassImage> images = detail.getProductGlass().getEyeGlass().getEyeGlassImages();
        if (images != null && !images.isEmpty()) {
            String imageUrl = images.get(0).getUrl();
            Glide.with(context)
                    .load(imageUrl)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.default_image)
                            .transform(new RoundedCorners(16))) // Bo góc 16dp
                    .into(holder.imgProduct);
        } else {
            holder.imgProduct.setImageResource(R.drawable.default_avt);
        }
    }

    @Override
    public int getItemCount() {
        return orderDetailHistories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtProductName, txtProductQuantity, txtProductPrice;
        ImageView imgProduct;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtProductQuantity = itemView.findViewById(R.id.txtProductQuantity);
            txtProductPrice = itemView.findViewById(R.id.txtProductPrice);
            imgProduct = itemView.findViewById(R.id.imgProduct);
        }
    }
}
