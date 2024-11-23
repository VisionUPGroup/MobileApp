package com.example.glass_project.data.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glass_project.R;

import java.util.List;

public class ProcessAdapter extends RecyclerView.Adapter<ProcessAdapter.ViewHolder> {

    private List<String> processList;
    private String selectedProcess = ""; // Process hiện tại
    private ProcessClickListener processClickListener;

    public ProcessAdapter(List<String> processList, ProcessClickListener processClickListener) {
        this.processList = processList;
        this.processClickListener = processClickListener;
    }

    public void setSelectedProcess(String selectedProcess) {
        this.selectedProcess = selectedProcess;
        notifyDataSetChanged(); // Làm mới toàn bộ giao diện
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_process, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String process = processList.get(position);
        holder.txtProcess.setText(process);

        // Đổi màu nếu Process được chọn
        if (process.equals(selectedProcess)) {
            holder.txtProcess.setBackgroundResource(R.drawable.selected_process_background); // Thay bằng drawable
            holder.txtProcess.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.white));
        } else {
            holder.txtProcess.setBackgroundResource(R.drawable.unselected_process_background); // Thay bằng drawable
            holder.txtProcess.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.black));
        }

        // Xử lý sự kiện click
        holder.itemView.setOnClickListener(v -> {
            if (!process.equals(selectedProcess)) { // Nếu khác Process hiện tại
                selectedProcess = process;
                notifyDataSetChanged(); // Làm mới giao diện
                processClickListener.onProcessClick(process); // Gửi sự kiện click
            }
        });
    }

    @Override
    public int getItemCount() {
        return processList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtProcess;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtProcess = itemView.findViewById(R.id.txtProcess); // TextView của Process
        }
    }

    public interface ProcessClickListener {
        void onProcessClick(String process);
    }
}

