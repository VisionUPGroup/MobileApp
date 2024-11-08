package com.example.glass_project.data.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glass_project.R;
import com.example.glass_project.data.model.ExamResult;
import com.example.glass_project.data.model.VisualAcuity.VisualAcuityRecord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VisualAcuityRecordAdapter extends RecyclerView.Adapter<VisualAcuityRecordAdapter.RecordViewHolder> {

    private List<VisualAcuityRecord> recordList;
    private OnItemClickListener onItemClickListener;
    private Map<Integer, List<ExamResult>> examResultsMap = new HashMap<>();
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(VisualAcuityRecord record);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public VisualAcuityRecordAdapter(List<VisualAcuityRecord> recordList, Context context) {
        this.recordList = recordList;
        this.context = context;
    }

    public void setExamResults(int recordId, List<ExamResult> examResults) {
        examResultsMap.put(recordId, examResults);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_visual_acuity_record, parent, false);
        return new RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        VisualAcuityRecord record = recordList.get(position);
        holder.bind(record);

        holder.itemView.setOnClickListener(v -> {
            List<ExamResult> examResults = examResultsMap.get(record.getId());
            if (examResults != null && !examResults.isEmpty()) {
                holder.toggleDropdown();
                holder.examResultsRecyclerView.setAdapter(new ExamResultAdapter(examResults));
            }
        });
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    class RecordViewHolder extends RecyclerView.ViewHolder {
        TextView textId, textStartDate, textStatus;
        RecyclerView examResultsRecyclerView;
        boolean isExpanded = false;

        RecordViewHolder(@NonNull View itemView) {
            super(itemView);
            textId = itemView.findViewById(R.id.textId);
            textStartDate = itemView.findViewById(R.id.textStartDate);
            textStatus = itemView.findViewById(R.id.textStatus);
            examResultsRecyclerView = itemView.findViewById(R.id.examResultsRecyclerView);
            examResultsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        }

        void bind(VisualAcuityRecord record) {
            textId.setText("ID: " + record.getId());
            textStartDate.setText("Start Date: " + record.getStartDate());
            textStatus.setText("Status: " + (record.isStatus() ? "Active" : "Inactive"));
            examResultsRecyclerView.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        }

        void toggleDropdown() {
            isExpanded = !isExpanded;
            examResultsRecyclerView.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        }
    }
}
