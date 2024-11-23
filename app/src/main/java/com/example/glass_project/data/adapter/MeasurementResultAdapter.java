package com.example.glass_project.data.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glass_project.R;
import com.example.glass_project.data.model.Refraction.MeasurementResult;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MeasurementResultAdapter extends RecyclerView.Adapter<MeasurementResultAdapter.ViewHolder> {

    private List<MeasurementResult> measurementResults;

    public MeasurementResultAdapter(List<MeasurementResult> measurementResults) {
        this.measurementResults = measurementResults;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_measurement_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MeasurementResult result = measurementResults.get(position);
        holder.bind(result);
    }

    @Override
    public int getItemCount() {
        return measurementResults.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView measurementDate, eyeSide, testType, spherical, cylindrical, axis, pupilDistance, prescriptionDetails, lastCheckupDate, nextCheckupDate, notes;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            measurementDate = itemView.findViewById(R.id.measurementDate);
            eyeSide = itemView.findViewById(R.id.eyeSide);
            testType = itemView.findViewById(R.id.testType);
            spherical = itemView.findViewById(R.id.spherical);
            cylindrical = itemView.findViewById(R.id.cylindrical);
            axis = itemView.findViewById(R.id.axis);
            pupilDistance = itemView.findViewById(R.id.pupilDistance);
            prescriptionDetails = itemView.findViewById(R.id.prescriptionDetails);
            lastCheckupDate = itemView.findViewById(R.id.lastCheckupDate);
            nextCheckupDate = itemView.findViewById(R.id.nextCheckupDate);
            notes = itemView.findViewById(R.id.notes);
        }

        public void bind(MeasurementResult result) {
            // Định dạng ngày tháng
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault());

            try {
                // Chuyển đổi định dạng ngày tháng từ kết quả đo
                Date lastCheckup = inputFormat.parse(result.getLastCheckupDate());
                Date nextCheckup = inputFormat.parse(result.getNextCheckupDate());

                // Hiển thị ngày tháng sau khi định dạng
                measurementDate.setText("Ngày đo: " + outputFormat.format(lastCheckup));
                lastCheckupDate.setText("Lần kiểm tra gần nhất: " + outputFormat.format(lastCheckup));
                nextCheckupDate.setText("Lần kiểm tra tiếp theo: " + outputFormat.format(nextCheckup));
            } catch (Exception e) {
                // Trường hợp không chuyển đổi được, hiển thị dữ liệu gốc
                measurementDate.setText("Ngày đo: " + result.getLastCheckupDate());
                lastCheckupDate.setText("Lần kiểm tra gần nhất: " + result.getLastCheckupDate());
                nextCheckupDate.setText("Lần kiểm tra tiếp theo: " + result.getNextCheckupDate());
            }

            // Hiển thị dữ liệu khác
            eyeSide.setText("Bên mắt: " + (result.getEyeSide() == 1 ? "Phải" : "Trái")); // Mắt phải hoặc trái
            testType.setText("Loại kiểm tra: " + result.getTestType()); // Loại bài kiểm tra
            spherical.setText("Chỉ số cầu (SPH): " + result.getSpherical()); // Chỉ số cầu
            cylindrical.setText("Chỉ số trụ (CYL): " + result.getCylindrical()); // Chỉ số trụ
            axis.setText("Trục: " + result.getAxis()); // Trục
            pupilDistance.setText("Khoảng cách đồng tử: " + result.getPupilDistance()); // Khoảng cách đồng tử
            prescriptionDetails.setText("Chi tiết đơn thuốc: " + result.getPrescriptionDetails()); // Chi tiết đơn thuốc
            notes.setText("Ghi chú: " + result.getNotes()); // Ghi chú
        }

    }
}

