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
        private TextView textViewId, textViewOrderID, textViewDescription, textViewFeedback, textViewStatus, textViewType;
        private TextView textViewHandlerId, textViewHandlerUsername;
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
        }

        public void bind(ReportData reportData) {
            textViewId.setText("ID: " + reportData.getId());
            textViewOrderID.setText("Order ID: " + reportData.getOrderID());
            textViewDescription.setText("Description: " + reportData.getDescription());
            textViewFeedback.setText("Feedback: " + (reportData.getFeedback() != null ? reportData.getFeedback() : "No feedback"));
            textViewStatus.setText(getStatusText(reportData.getStatus()));
            textViewStatus.setBackgroundResource(getStatusBackground(reportData.getStatus()));
            textViewType.setText("Type: " + getTypeText(reportData.getType()));

            // Hiển thị thông tin Handler
            if (reportData.getHandler() != null) {
                textViewHandlerId.setText("Mã Nhân Viên: " + reportData.getHandler().getId());
                textViewHandlerUsername.setText("Nhân Viên: " + reportData.getHandler().getUsername());
                textViewFeedback.setText("Phản hồi: " + (reportData.getFeedback() != null ? reportData.getFeedback() : "No feedback"));

                // Make feedback group visible
                feedbackGroup.setVisibility(View.VISIBLE);
            } else {
                // Hide feedback group if handler is null
                feedbackGroup.setVisibility(View.GONE);
            }
        }

        private String getStatusText(int status) {
            switch (status) {
                case 0:
                    return "Pending";
                case 1:
                    return "Rejected";
                case 2:
                    return "Accepted";
                default:
                    return "Unknown";
            }
        }

        private String getTypeText(int type) {
            switch (type) {
                case 0:
                    return "Product Issue";
                case 1:
                    return "Delivery Issue";
                case 2:
                    return "Customer Issue";
                case 3:
                    return "Customer Service";
                case 4:
                    return "Other";
                default:
                    return "Unknown";
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
