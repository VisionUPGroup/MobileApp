package com.example.glass_project.data.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glass_project.R;
import com.example.glass_project.data.model.eyeCheck.Exam;

import java.util.List;

public class ExamAdapter extends RecyclerView.Adapter<ExamAdapter.ExamViewHolder> {
    private final List<Exam> examList;
    private int selectedPosition = -1;
    private final OnExamSelectedListener onExamSelectedListener;

    public interface OnExamSelectedListener {
        void onExamSelected(int examId);
    }

    public ExamAdapter(List<Exam> examList, OnExamSelectedListener onExamSelectedListener) {
        this.examList = examList;
        this.onExamSelectedListener = onExamSelectedListener;
    }

    @NonNull
    @Override
    public ExamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exam_detail, parent, false);
        return new ExamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExamViewHolder holder, int position) {
        Exam exam = examList.get(position);
        holder.typeTextView.setText(exam.getType());
        holder.statusTextView.setText(exam.isStatus() ? "Available" : "Not Available");

        holder.radioButton.setChecked(position == selectedPosition);
        holder.radioButton.setOnClickListener(v -> {
            selectedPosition = holder.getAdapterPosition();
            notifyDataSetChanged();
            onExamSelectedListener.onExamSelected(exam.getId());
        });
    }

    @Override
    public int getItemCount() {
        return examList.size();
    }

    static class ExamViewHolder extends RecyclerView.ViewHolder {
        TextView typeTextView, statusTextView;
        RadioButton radioButton;

        public ExamViewHolder(@NonNull View itemView) {
            super(itemView);
            typeTextView = itemView.findViewById(R.id.textExamType);
            statusTextView = itemView.findViewById(R.id.textExamStatus);
            radioButton = itemView.findViewById(R.id.radioSelectExam);
        }
    }
}
