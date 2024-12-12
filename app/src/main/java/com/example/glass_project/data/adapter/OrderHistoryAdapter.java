package com.example.glass_project.data.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glass_project.R;
import com.example.glass_project.config.Config;
import com.example.glass_project.data.model.order.OrderHistoryItem;
import com.example.glass_project.product.ui.order.history.ListOrderHistoryActivity;
import com.example.glass_project.product.ui.order.history.OrderDetailActivity;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {

    private Context context;
    private List<OrderHistoryItem> orderList;
    private DecimalFormat decimalFormat = new DecimalFormat("#,###");

    public OrderHistoryAdapter(Context context, List<OrderHistoryItem> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderHistoryItem order = orderList.get(position);

        // Set thông tin đơn hàng
        holder.txtOrderId.setText("Mã đơn: " + order.getId());
        holder.txtOrderStatus.setText(getOrderStatus(order.getProcess()));
        holder.txtOrderTime.setText(order.getFormattedOrderTime());
        holder.txtTotalAmount.setText("Tiền cần thanh toán: đ" + decimalFormat.format(order.getRemainingAmount()));

        // Hiển thị trạng thái Đặt cọc nếu có
        holder.txtIsDeposit.setVisibility(order.isDeposit() ? View.VISIBLE : View.GONE);

        holder.btnSumit.setVisibility(order.getProcess() == 3 ? View.VISIBLE : View.GONE);

        // Cài đặt RecyclerView cho danh sách sản phẩm trong đơn hàng
        OrderDetailHistoryAdapter orderDetailAdapter = new OrderDetailHistoryAdapter(context, order.getOrderDetails());
        holder.recyclerViewProducts.setLayoutManager(new LinearLayoutManager(context));
        holder.recyclerViewProducts.setAdapter(orderDetailAdapter);

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, OrderDetailActivity.class);
            intent.putExtra("orderId", order.getId());
            intent.putExtra("totalAmount", order.getTotal());
            intent.putExtra("status", order.getProcess());
            context.startActivity(intent);
        });
        holder.btnSumit.setOnClickListener(v -> {
            confirmOrder(order.getId());
        });
    }
    private void confirmOrder(int orderId) {
        new Thread(() -> {
            try {
                SharedPreferences sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                String accessToken = sharedPreferences.getString("accessToken", "");

                if (accessToken.isEmpty()) {
                    ((ListOrderHistoryActivity) context).runOnUiThread(() -> Toast.makeText(context, "Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show());
                    return;
                }

                String BaseUrl = Config.getBaseUrl();
                URL url = new URL(BaseUrl + "/api/orders/confirm/" + orderId);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("PUT");
                connection.setRequestProperty("Authorization", "Bearer " + accessToken);
                connection.setRequestProperty("accept", "*/*");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    ((ListOrderHistoryActivity) context).runOnUiThread(() -> Toast.makeText(context, "Đơn hàng đã được xác nhận!", Toast.LENGTH_SHORT).show());
                } else {
                    ((ListOrderHistoryActivity) context).runOnUiThread(() -> Toast.makeText(context, "Lỗi khi xác nhận đơn hàng.", Toast.LENGTH_SHORT).show());
                }

                connection.disconnect();
            } catch (Exception e) {
                Log.e("OrderHistoryAdapter", "confirmOrder: ", e);
                ((ListOrderHistoryActivity) context).runOnUiThread(() -> Toast.makeText(context, "Lỗi khi xác nhận đơn hàng.", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
    @Override
    public int getItemCount() {
        return orderList.size();
    }

    private String getOrderStatus(int process) {
        switch (process) {
            case 0: return "Đang chờ xử lý";
            case 1: return "Đang xử lý";
            case 2: return "Đang giao hàng";
            case 3: return "Đã giao hàng";
            case 4: return "Hoàn thành";
            case 5: return "Đã hủy";
            default: return "Unknown";
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtOrderId, txtOrderStatus, txtOrderTime, txtTotalAmount, txtIsDeposit;
        RecyclerView recyclerViewProducts;
        Button btnSumit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtOrderId = itemView.findViewById(R.id.txtOrderId);
            txtOrderStatus = itemView.findViewById(R.id.txtOrderStatus);
            txtOrderTime = itemView.findViewById(R.id.txtOrderTime);
            txtTotalAmount = itemView.findViewById(R.id.txtTotalAmount);
            txtIsDeposit = itemView.findViewById(R.id.txtIsDeposit);
            recyclerViewProducts = itemView.findViewById(R.id.recyclerViewProducts);
            btnSumit = itemView.findViewById(R.id.btnSumit);
        }
    }
}
