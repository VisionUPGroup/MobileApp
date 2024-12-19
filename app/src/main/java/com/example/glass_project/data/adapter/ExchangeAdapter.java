package com.example.glass_project.data.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
        holder.orderCode.setText("Mã đơn hàng mới: " + item.getNewOrder().getId());

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
            showDetailDialog(item);
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
    private void showDetailDialog(ExchangeItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_exchange_detail, null);
        builder.setView(dialogView);

        // Staff Information
        TextView staffName = dialogView.findViewById(R.id.staffName);
        TextView staffEmail = dialogView.findViewById(R.id.staffEmail);
        TextView staffPhone = dialogView.findViewById(R.id.staffPhone);
        staffName.setText("Tên nhân viên: " + item.getStaff().getId() + " - " + item.getStaff().getUsername());
        staffEmail.setText("Mail: " + item.getStaff().getEmail());
        staffPhone.setText("Số điện thoại: " + item.getStaff().getPhoneNumber());

        // Detailed Information
        TextView reason = dialogView.findViewById(R.id.reason);
        TextView status = dialogView.findViewById(R.id.status);
        TextView reportId = dialogView.findViewById(R.id.reportId);
        reason.setText("Lý do: " + item.getReason());
        if (item.getStatus() == 0) {
            status.setText("Trạng thái: Không duyệt");
            status.setTextColor(context.getResources().getColor(R.color.red));
        } else {
            status.setText("Trạng thái: Đã duyệt");
            status.setTextColor(context.getResources().getColor(R.color.green));
        }

        reportId.setText("Mã đơn báo cáo: " + item.getReport().getId());

        Button viewReportButton = dialogView.findViewById(R.id.btnViewReport);
        viewReportButton.setOnClickListener(v -> {
            // Logic to view report details
            Toast.makeText(context, "Xem báo cáo ID: " + item.getReport().getId(), Toast.LENGTH_SHORT).show();
        });

        // Old Order Information
        TextView oldOrderId = dialogView.findViewById(R.id.oldOrderId);
        TextView oldEyeGlass = dialogView.findViewById(R.id.oldEyeGlass);
        TextView oldLeftLens = dialogView.findViewById(R.id.oldLeftLens);
        TextView oldRightLens = dialogView.findViewById(R.id.oldRightLens);
        oldOrderId.setText("Đơn hàng cũ: " + item.getOldOrder().getId() + " x " + item.getOldProductGlass().getQuantity());
        oldEyeGlass.setText("Tên gọng kính: " + item.getOldProductGlass().getEyeGlass().getName());
        oldLeftLens.setText("Tên tròng trái: " + item.getOldProductGlass().getLeftLen().getName());
        oldRightLens.setText("Tên tròng phải: " + item.getOldProductGlass().getRightLen().getName());

        Button oldOrderDetailButton = dialogView.findViewById(R.id.btnOldOrderDetail);
        oldOrderDetailButton.setOnClickListener(v -> {
            // Logic to view old order details
            Intent intent = new Intent(context, OrderDetailActivity.class);
            intent.putExtra("orderId", item.getOldOrder().getId());
            context.startActivity(intent);
        });

        // New Order Information
        TextView newOrderId = dialogView.findViewById(R.id.newOrderId);
        TextView newEyeGlass = dialogView.findViewById(R.id.newEyeGlass);
        TextView newLeftLens = dialogView.findViewById(R.id.newLeftLens);
        TextView newRightLens = dialogView.findViewById(R.id.newRightLens);
        newOrderId.setText("Đơn hàng mới: " + item.getNewOrder().getId() + " x " + item.getNewProductGlass().getQuantity());
        newEyeGlass.setText("Tên gọng kính: " + item.getNewProductGlass().getEyeGlass().getName());
        newLeftLens.setText("Tên tròng trái: " + item.getNewProductGlass().getLeftLen().getName());
        newRightLens.setText("Tên tròng phải: " + item.getNewProductGlass().getRightLen().getName());

        Button newOrderDetailButton = dialogView.findViewById(R.id.btnNewOrderDetail);
        newOrderDetailButton.setOnClickListener(v -> {
            // Logic to view new order details

            Intent intent = new Intent(context, OrderDetailActivity.class);
            intent.putExtra("orderId", item.getNewOrder().getId());
            context.startActivity(intent);
        });

        // Add close button
        builder.setPositiveButton("Đóng", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

}
