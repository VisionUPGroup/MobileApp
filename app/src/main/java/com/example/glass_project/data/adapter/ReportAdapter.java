package com.example.glass_project.data.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.glass_project.R;
import com.example.glass_project.data.model.report.ReportData;

import java.util.ArrayList;
import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    private Context context;
    private List<ReportData> reportDataList;

    public ReportAdapter(Context context, List<ReportData> reportDataList) {
        this.context = context;
        this.reportDataList = reportDataList != null ? reportDataList : new ArrayList<>();
    }

    @Override
    public ReportViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the item view
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_report, parent, false);
        return new ReportViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ReportViewHolder holder, int position) {
        ReportData reportData = reportDataList.get(position);

        holder.bind(reportData);
    }

    @Override
    public int getItemCount() {
        return reportDataList != null ? reportDataList.size() : 0;
    }

    public void updateData(List<ReportData> newData) {
        if (newData != null) {
            this.reportDataList = newData;
            notifyDataSetChanged();
        }
    }

    public static class ReportViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewId, textViewOrderID, textViewDescription, textViewFeedback, textViewStatus, textViewType,textviewCreateDate;
        private TextView textViewHandlerId, textViewHandlerUsername,textviewUpdateDate;
        private LinearLayout feedbackGroup;

        public ReportViewHolder(View itemView) {
            super(itemView);
            textViewId = itemView.findViewById(R.id.textview_id);
            textViewOrderID = itemView.findViewById(R.id.textview_order_id);
            textViewDescription = itemView.findViewById(R.id.textview_description);
            textViewFeedback = itemView.findViewById(R.id.textview_feedback);
            textViewStatus = itemView.findViewById(R.id.textview_status);
            textViewType = itemView.findViewById(R.id.textview_type);
            textViewHandlerId = itemView.findViewById(R.id.textview_handler_id); // Handler ID
            textViewHandlerUsername = itemView.findViewById(R.id.textview_handler_username); // Handler Username
            feedbackGroup = itemView.findViewById(R.id.feedback_group); // Group view feedback, handler info, etc.
            textviewCreateDate = itemView.findViewById(R.id.textview_create_date);
            textviewUpdateDate = itemView.findViewById(R.id.textview_update_date);

        }

        public void bind(ReportData reportData) {
            textViewId.setText("ID: " + reportData.getId());
            textViewOrderID.setText("Mã Đơn Hàng: " + reportData.getOrderID());
            textViewDescription.setText("Mô Tả: " + reportData.getDescription());
            textViewFeedback.setText("Phản Hồi: " + (reportData.getFeedback() != null ? reportData.getFeedback() : "Chưa có phản hồi"));
            textViewStatus.setText(getStatusText(reportData.getStatus()));
            textViewStatus.setBackgroundResource(getStatusBackground(reportData.getStatus()));
            textViewType.setText("Loại Báo Cáo: " + getTypeText(reportData.getType()));
            textviewCreateDate.setText("Ngày Tạo: " + reportData.getFormattedCreateAt());

            // Hiển thị thông tin người xử lý (Handler)
            if (reportData.getHandler() != null) {
                textViewHandlerId.setText("Mã Nhân Viên: " + reportData.getHandler().getId());
                textViewHandlerUsername.setText("Tên Nhân Viên: " + reportData.getHandler().getUsername());
                textViewFeedback.setText("Phản Hồi: " + (reportData.getFeedback() != null ? reportData.getFeedback() : "Chưa có phản hồi"));
                if (reportData.getUpdateAt() != null) {
                    textviewUpdateDate.setText("Ngày Cập Nhật: " + reportData.getFormattedUpdateAt());
                }
                textviewUpdateDate.setVisibility(View.GONE);
                // Hiển thị nhóm phản hồi
                feedbackGroup.setVisibility(View.VISIBLE);
            } else {
                // Ẩn nhóm phản hồi nếu không có người xử lý
                feedbackGroup.setVisibility(View.GONE);
            }
        }

        private String getStatusText(int status) {
            switch (status) {
                case 0:
                    return "Đang Chờ Xử Lý";
                case 1:
                    return "Bị Từ Chối";
                case 2:
                    return "Đã Chấp Nhận";
                default:
                    return "Không Xác Định";
            }
        }

        private String getTypeText(int type) {
            switch (type) {
                case 0:
                    return "Vấn Đề Sản Phẩm";
                case 1:
                    return "Vấn Đề Giao Hàng";
                case 2:
                    return "Vấn Đề Khách Hàng";
                case 3:
                    return "Dịch Vụ Khách Hàng";
                case 4:
                    return "Khác";
                default:
                    return "Không Xác Định";
            }
        }

        private int getStatusBackground(int status) {
            switch (status) {
                case 0:  // Pending
                    return R.drawable.status_pending;
                case 1:  // Rejected
                    return R.drawable.status_rejected;
                case 2:  // Accepted
                    return R.drawable.status_accepted;
                default:
                    return R.drawable.status_rejected;
            }
        }
    }
}
