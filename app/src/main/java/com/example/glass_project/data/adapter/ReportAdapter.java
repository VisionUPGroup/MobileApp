// ReportAdapter.java
package com.example.glass_project.data.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.glass_project.R;
import com.example.glass_project.data.model.report.ReportData;
import com.example.glass_project.product.ui.order.history.OrderDetailActivity;
import com.example.glass_project.product.ui.report.ListReportActivity;

import java.util.ArrayList;
import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    private final Context context;
    private List<ReportData> reportDataList;

    public ReportAdapter(Context context, List<ReportData> reportDataList) {
        this.context = context;
        this.reportDataList = reportDataList != null ? reportDataList : new ArrayList<>();
    }

    @Override
    public ReportViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_report, parent, false);
        return new ReportViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ReportViewHolder holder, int position) {
        ReportData reportData = reportDataList.get(position);
        holder.bind(reportData);
        holder.btnViewDetails.setOnClickListener(v -> showDetailsDialog(context, reportData));
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
        private final TextView textViewId, textViewOrderID, textViewDescription, textViewFeedback, textViewStatus, textViewType, textviewCreateDate;
        private final TextView textViewHandlerId, textViewHandlerUsername, textviewUpdateDate;
        private final LinearLayout feedbackGroup;
        private final Button btnViewDetails;

        public ReportViewHolder(View itemView) {
            super(itemView);
            textViewId = itemView.findViewById(R.id.textview_id);
            textViewOrderID = itemView.findViewById(R.id.textview_order_id);
            textViewDescription = itemView.findViewById(R.id.textview_description);
            textViewFeedback = itemView.findViewById(R.id.textview_feedback);
            textViewStatus = itemView.findViewById(R.id.textview_status);
            textViewType = itemView.findViewById(R.id.textview_type);
            textViewHandlerId = itemView.findViewById(R.id.textview_handler_id);
            textViewHandlerUsername = itemView.findViewById(R.id.textview_handler_username);
            feedbackGroup = itemView.findViewById(R.id.feedback_group);
            textviewCreateDate = itemView.findViewById(R.id.textview_create_date);
            textviewUpdateDate = itemView.findViewById(R.id.textview_update_date);
            btnViewDetails = itemView.findViewById(R.id.btn_view_details);
        }

        public void bind(ReportData reportData) {
            textViewId.setText("Mã Báo Cáo: " + reportData.getId());
            textViewOrderID.setText("Mã Đơn Hàng: " + reportData.getOrderID());
            textViewDescription.setText("Mô Tả: " + reportData.getDescription());
            textViewFeedback.setText("Phản Hồi: " + (reportData.getFeedback() != null ? reportData.getFeedback() : "Chưa có phản hồi"));
            textViewStatus.setText(ReportAdapter.getStatusText(reportData.getStatus()));
            textViewStatus.setBackgroundResource(ReportAdapter.getStatusBackground(reportData.getStatus()));
            textViewType.setText("Loại Báo Cáo: " + ReportAdapter.getTypeText(reportData.getType()));
            textviewCreateDate.setText("Ngày Tạo: " + reportData.getFormattedCreateAt());

//            if (reportData.getHandler() != null) {
//                textViewHandlerId.setText("Mã Nhân Viên: " + reportData.getHandler().getId());
//                textViewHandlerUsername.setText("Tên Nhân Viên: " + reportData.getHandler().getUsername());
//                textviewUpdateDate.setText("Ngày Cập Nhật: " + reportData.getFormattedUpdateAt());
//                feedbackGroup.setVisibility(View.VISIBLE);
//            } else {
//                feedbackGroup.setVisibility(View.GONE);
//            }
        }
    }

    private void showDetailsDialog(Context context, ReportData reportData) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_report_details, null);
        builder.setView(dialogView);

        TextView textViewReportID = dialogView.findViewById(R.id.textview_report_id);
        TextView textViewStatus = dialogView.findViewById(R.id.textview_status);
        TextView textViewOrderID = dialogView.findViewById(R.id.textview_order_id);
        TextView textViewOrderDate = dialogView.findViewById(R.id.textview_order_date);
        TextView textViewType = dialogView.findViewById(R.id.textview_type);
        TextView textViewDescription = dialogView.findViewById(R.id.textview_description);
        ImageView imageViewEvidenceImage = dialogView.findViewById(R.id.imageview_evidence_image);
        TextView textViewProductID = dialogView.findViewById(R.id.textview_product_id);
        TextView textviewProductName = dialogView.findViewById(R.id.textview_product_name);
        TextView textViewLeftEye = dialogView.findViewById(R.id.textview_left_eye);
        TextView textViewRightEye = dialogView.findViewById(R.id.textview_right_eye);
        LinearLayout feedbackGroup = dialogView.findViewById(R.id.feedback_group);
        TextView textViewHandlerId = dialogView.findViewById(R.id.textview_handler_id);
        TextView textViewHandlerUsername = dialogView.findViewById(R.id.textview_handler_username);
        TextView textViewUpdateDate = dialogView.findViewById(R.id.textview_update_date);
        TextView textViewFeedback = dialogView.findViewById(R.id.textview_feedback);
        textViewReportID.setText("Mã Báo Cáo: " + reportData.getId());
        textViewOrderID.setText("Mã Đơn Hàng: " + reportData.getOrderID());
        textViewType.setText("Loại Báo Cáo: " + getTypeText(reportData.getType()));
        textViewDescription.setText("Mô Tả: " + reportData.getDescription());
        textViewOrderDate.setText("Ngày Tạo: " + reportData.getFormattedCreateAt());
        textViewStatus.setText(getStatusText(reportData.getStatus()));
        textViewStatus.setBackgroundResource(getStatusBackground(reportData.getStatus()));

        String imageUrl = reportData.getEvidenceImage();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.border_background)
                    .error(R.drawable.status_rejected)
                    .into(imageViewEvidenceImage);
            imageViewEvidenceImage.setOnClickListener(v -> showImageDialog(context, imageUrl));
        } else {
            imageViewEvidenceImage.setImageResource(R.drawable.default_image);
        }
        if (reportData.getHandler() != null) {
            feedbackGroup.setVisibility(View.VISIBLE);
            textViewHandlerId.setText("Mã Nhân Viên: " + reportData.getHandler().getId());
            textViewHandlerUsername.setText("Tên Nhân Viên: " + reportData.getHandler().getUsername());
            textViewFeedback.setText("Phản Hồi: \n" + (reportData.getFeedback() != null ? reportData.getFeedback() : "No feedback provided"));
            textViewUpdateDate.setText("Ngày Cập Nhật: " + (reportData.getUpdateAt() != null ? reportData.getFormattedUpdateAt() : "N/A"));
        } else {
            feedbackGroup.setVisibility(View.GONE);
        }
        // Fetch product glass payment details
        if (context instanceof ListReportActivity) {
            ((ListReportActivity) context).fetchProductGlassPayment(reportData.getOrderID(), textViewProductID, textViewLeftEye, textViewRightEye,textviewProductName);
        }
        Button btnBack = dialogView.findViewById(R.id.btn_back);
        Button btnViewOrder = dialogView.findViewById(R.id.btn_view_order);

        AlertDialog dialog = builder.create();
        btnBack.setOnClickListener(v -> dialog.dismiss());
        btnViewOrder.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderDetailActivity.class);
            intent.putExtra("orderId", reportData.getOrderID());
            context.startActivity(intent);
            dialog.dismiss();
        });

        dialog.show();
    }
    private void showImageDialog(Context context, String imageUrl) {
        // Create a custom dialog
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_full_screen_image);

        // Initialize ImageView in dialog
        ImageView fullScreenImageView = dialog.findViewById(R.id.full_screen_image_view);

        // Load the image into the ImageView using Glide
        Glide.with(context)
                .load(imageUrl)
                .into(fullScreenImageView);

        // Set dialog properties
        dialog.setCancelable(true); // You can set whether the dialog can be canceled by tapping outside
        dialog.show();
    }

    public static String getStatusText(int status) {
        switch (status) {
            case 0: return "Đang Chờ Xử Lý";
            case 1: return "Bị Từ Chối";
            case 2: return "Đã Chấp Nhận";
            default: return "Không Xác Định";
        }
    }

    public static String getTypeText(int type) {
        switch (type) {
            case 0: return "Vấn Đề Sản Phẩm";
            case 1: return "Vấn Đề Giao Hàng";
            case 2: return "Vấn Đề Khách Hàng";
            case 3: return "Dịch Vụ Khách Hàng";
            case 4: return "Khác";
            default: return "Không Xác Định";
        }
    }

    public static int getStatusBackground(int status) {
        switch (status) {
            case 0: return R.drawable.status_pending;
            case 1: return R.drawable.status_rejected;
            case 2: return R.drawable.status_accepted;
            default: return R.drawable.status_rejected;
        }
    }
}
