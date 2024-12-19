package com.example.glass_project.data.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glass_project.R;
import com.example.glass_project.data.model.other.Notification;
import com.example.glass_project.product.ui.notifications.NotificationsActivity;
import com.example.glass_project.product.ui.order.history.OrderDetailActivity;
import com.example.glass_project.product.ui.report.ListReportActivity;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_NOTIFICATION = 0; // View type cho thông báo
    private static final int VIEW_TYPE_SEE_MORE = 1; // View type cho "Xem thêm"

    private final Context context;
    private final List<Notification> notifications;

    public NotificationAdapter(Context context, List<Notification> notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    @Override
    public int getItemViewType(int position) {
        // Phân biệt loại view: thông báo hoặc "Xem thêm"
        return position < notifications.size() ? VIEW_TYPE_NOTIFICATION : VIEW_TYPE_SEE_MORE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_NOTIFICATION) {
            // Inflate layout cho thông báo
            View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
            return new NotificationViewHolder(view);
        } else {
            // Inflate layout cho nút "Xem thêm"
            View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
            return new SeeMoreViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NotificationViewHolder) {
            Notification notification = notifications.get(position);
            ((NotificationViewHolder) holder).bind(notification);
        } else if (holder instanceof SeeMoreViewHolder) {
            ((SeeMoreViewHolder) holder).bind();
        }
    }

    @Override
    public int getItemCount() {
        return notifications.size(); // +1 cho nút "Xem thêm"
    }

    /**
     * ViewHolder cho thông báo
     */
    class NotificationViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final TextView bodyTextView;
        private final TextView timeTextView;  // Thêm TextView cho thời gian

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.notification_title);
            bodyTextView = itemView.findViewById(R.id.notification_body);
            timeTextView = itemView.findViewById(R.id.notification_timestamp);  // Ánh xạ TextView thời gian

            // Xử lý sự kiện click vào thông báo
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Notification notification = notifications.get(position);
                    handleNotificationClick(notification);
                }
            });
        }

        public void bind(Notification notification) {
            titleTextView.setText(notification.getTitle());
            bodyTextView.setText(notification.getBody());

            // Định dạng và hiển thị thời gian
            String timestamp = notification.getTimestamp();
            if (timestamp != null && !timestamp.isEmpty()) {
                // Giả sử timestamp là một chuỗi theo định dạng ISO 8601 (ví dụ: "2024-11-28T01:28:43.5351174Z")
                try {
                    // Chuyển đổi timestamp thành định dạng mong muốn (ví dụ: "HH:mm dd/MM/yyyy")
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS'Z'", Locale.getDefault());
                    SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
                    Date date = inputFormat.parse(timestamp);
                    if (date != null) {
                        String formattedDate = outputFormat.format(date);
                        timeTextView.setText(formattedDate);  // Hiển thị thời gian đã định dạng
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    timeTextView.setText("");  // Nếu có lỗi, không hiển thị gì
                }
            } else {
                timeTextView.setText("");  // Nếu không có thời gian, để trống
            }
        }

        // Điều hướng dựa trên nội dung thông báo
        private void handleNotificationClick(Notification notification) {
            String extraData = notification.getExtraData();
            Intent intent;

            if (extraData != null) {
                try {
                    JSONObject extraDataJson = new JSONObject(extraData);

                    if (extraDataJson.has("activity")) {
                        String activityName = extraDataJson.getString("activity");

                        if (activityName.equals("OrderDetailsActivity") && extraDataJson.has("Id")) {
                            // Điều hướng tới OrderDetailsActivity
                            intent = new Intent(context, OrderDetailActivity.class);
                            int orderId = extraDataJson.getInt("Id");
                            intent.putExtra("orderId", orderId);
                            context.startActivity(intent);

                            // Log thông báo tiếng Việt
                            Log.i("NotificationAdapter", "Mở chi tiết đơn hàng với mã: " + orderId);
                        } else if (activityName.equals("ListReportActivity") && extraDataJson.has("Id")) {
                            // Điều hướng tới ListReportActivity
                            intent = new Intent(context, ListReportActivity.class);
                            int reportId = extraDataJson.getInt("Id");
                            intent.putExtra("reportId", reportId);
                            context.startActivity(intent);

                            // Log thông báo tiếng Việt
                            Log.i("NotificationAdapter", "Mở danh sách báo cáo với mã: " + reportId);
                        } else {
                            Log.e("NotificationAdapter", "Hoạt động không được nhận diện hoặc thiếu Id");
                        }
                    } else {
                        Log.e("NotificationAdapter", "Thiếu trường 'activity' trong extraData");
                    }
                } catch (Exception e) {
                    Log.e("NotificationAdapter", "Lỗi khi phân tích extraData", e);
                }
            } else {
                Log.e("NotificationAdapter", "extraData là null");
            }
        }

    }


    /**
     * ViewHolder cho nút "Xem thêm"
     */
    class SeeMoreViewHolder extends RecyclerView.ViewHolder {
        public SeeMoreViewHolder(@NonNull View itemView) {
            super(itemView);

            // Xử lý sự kiện click vào nút "Xem thêm"
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, NotificationsActivity.class);
                context.startActivity(intent);
            });
        }

        public void bind() {
            // Không cần bind dữ liệu, chỉ cần thiết lập click listener
        }
    }
}
