package com.example.glass_project.data.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glass_project.R;
import com.example.glass_project.data.model.other.ExchangeItem;
import com.example.glass_project.product.ui.order.history.OrderDetailActivity;

import java.util.List;

public class ExchangeAdapter extends RecyclerView.Adapter<ExchangeAdapter.ViewHolder> {

    private final Context context;
    private final List<ExchangeItem> exchangeItems;

    public ExchangeAdapter(Context context, List<ExchangeItem> exchangeItems) {
        this.context = context;
        this.exchangeItems = exchangeItems;
    }

    @NonNull
    @Override
    public ExchangeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_exchange, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExchangeAdapter.ViewHolder holder, int position) {
        ExchangeItem item = exchangeItems.get(position);

        // Set staff name
        holder.staffName.setText("Nhân viên: " + item.getStaff().getUsername());

        // Set order code
        holder.orderCode.setText("Mã đơn hàng: " + item.getOrder().getCode());

        // Set reason
        holder.reason.setText("Lý do: " + item.getReason());

        // Set status with conditional formatting
        if (item.getStatus() == 0) {
            holder.status.setText("Không duyệt");
            holder.status.setTextColor(context.getResources().getColor(R.color.red));
        } else {
            holder.status.setText("Đã duyệt");
            holder.status.setTextColor(context.getResources().getColor(R.color.green));
        }

        // Set detail button click listener
        holder.detailButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderDetailActivity.class);
            intent.putExtra("orderId", item.getOrder().getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return exchangeItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView staffName, orderCode, reason, status;
        Button detailButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            staffName = itemView.findViewById(R.id.staffName);
            orderCode = itemView.findViewById(R.id.orderCode);
            reason = itemView.findViewById(R.id.reason);
            status = itemView.findViewById(R.id.status);
            detailButton = itemView.findViewById(R.id.btnDetail);
        }
    }
}
