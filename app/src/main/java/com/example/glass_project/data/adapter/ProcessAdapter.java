package com.example.glass_project.data.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glass_project.R;

import java.util.List;

public class ProcessAdapter extends RecyclerView.Adapter<ProcessAdapter.ProcessViewHolder> {

    private final List<String> processList;
    private final OnProcessClickListener listener;

    public ProcessAdapter(List<String> processList, OnProcessClickListener listener) {
        this.processList = processList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProcessViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_process, parent, false);
        return new ProcessViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProcessViewHolder holder, int position) {
        String process = processList.get(position);
        holder.processText.setText(process);
        holder.itemView.setOnClickListener(v -> listener.onProcessClick(process));
    }

    @Override
    public int getItemCount() {
        return processList.size();
    }

    public interface OnProcessClickListener {
        void onProcessClick(String process);
    }

    public static class ProcessViewHolder extends RecyclerView.ViewHolder {
        TextView processText;

        public ProcessViewHolder(View itemView) {
            super(itemView);
            processText = itemView.findViewById(R.id.processText);
        }
    }
}
