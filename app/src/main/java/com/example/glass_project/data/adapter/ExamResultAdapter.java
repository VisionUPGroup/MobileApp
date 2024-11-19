package com.example.glass_project.data.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.glass_project.R;
import com.example.glass_project.data.model.VisualAcuity.ExamResult;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExamResultAdapter extends RecyclerView.Adapter<ExamResultAdapter.ViewHolder> {

    private List<ExamResult> examResults;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(ExamResult examResult);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public ExamResultAdapter(List<ExamResult> examResults) {
        this.examResults = examResults;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exam_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExamResult examResult = examResults.get(position);
        holder.bind(examResult);
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(examResult);
            }
        });
    }

    @Override
    public int getItemCount() {
        return examResults.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView examDateText;
        private TextView diopterText;
        private TextView eyeSideText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            examDateText = itemView.findViewById(R.id.examDate);
            diopterText = itemView.findViewById(R.id.diopter);
            eyeSideText = itemView.findViewById(R.id.eyeSide);
        }

        public void bind(ExamResult examResult) {
            examDateText.setText("Date: " + formatDate(examResult.getExamDate()));
            diopterText.setText("Diopter: " + examResult.getDiopter());
            eyeSideText.setText("Eye Side: " + examResult.getEyeSide());
        }

        private String formatDate(String dateString) {
            try {
                // Định dạng ngày giờ từ định dạng gốc sang "dd/MM/yyyy - HH:mm"
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
                Date date = inputFormat.parse(dateString);

                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault());
                return outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
                return dateString; // Trả về dữ liệu gốc nếu có lỗi
            }
        }
    }
}
