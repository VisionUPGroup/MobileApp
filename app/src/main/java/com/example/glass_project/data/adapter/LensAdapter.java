package com.example.glass_project.data.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glass_project.R;
import com.example.glass_project.data.model.LensType;

import java.util.List;

public class LensAdapter extends RecyclerView.Adapter<LensAdapter.ViewHolder> {

    private final List<LensType> lensTypeList;
    private final Context context;
    private int expandedPosition = -1;

    public LensAdapter(List<LensType> lensTypeList, Context context) {
        this.lensTypeList = lensTypeList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_lens, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LensType lensType = lensTypeList.get(position);

        if (!lensType.isStatus()) {
            holder.recyclerView.setVisibility(View.GONE);
            holder.linearLayoutCompat.setVisibility(View.GONE);
            return; // Early return nếu statusLensType là false
        }

        // Nếu statusLensType là true, tiếp tục thiết lập dữ liệu và hiển thị
        String tvHeader = lensType.getDescription();
        String[] parts = tvHeader.split("\\.", 2);
        String title = parts[0].trim();
        String subtitle = (parts.length > 1) ? parts[1].trim() : "";

        holder.tvHeader.setText(title);
        holder.tvSubHeader.setText(subtitle);
        holder.recyclerView.setVisibility(View.VISIBLE);
        holder.linearLayoutCompat.setVisibility(View.VISIBLE);

        boolean isExpanded = position == expandedPosition;
        holder.recyclerView.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.linearLayoutCompat.setBackgroundResource(isExpanded ? R.drawable.rounded_background : R.drawable.rounded_default_background);
        holder.tvHeader.setTextColor(isExpanded ? context.getResources().getColor(R.color.white) : context.getResources().getColor(R.color.blue));
        holder.tvSubHeader.setTextColor(isExpanded ? context.getResources().getColor(R.color.white) : context.getResources().getColor(R.color.gray));

        holder.linearLayoutCompat.setOnClickListener(v -> {
            expandedPosition = isExpanded ? -1 : position;
            notifyDataSetChanged();
        });

        LensItemAdapter lensItemAdapter = new LensItemAdapter(lensType.getLens(), context);
        holder.recyclerView.setAdapter(lensItemAdapter);

    }

    @Override
    public int getItemCount() {
        return lensTypeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvHeader, tvSubHeader;
        RecyclerView recyclerView;
        LinearLayoutCompat linearLayoutCompat;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHeader = itemView.findViewById(R.id.tvHeader);
            recyclerView = itemView.findViewById(R.id.recyclerViewDetails);
            tvSubHeader = itemView.findViewById(R.id.tvSubtitle);
            linearLayoutCompat = itemView.findViewById(R.id.linearLayoutHeader);
        }
    }
}
