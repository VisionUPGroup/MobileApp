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
import com.example.glass_project.data.model.Refraction.MeasurementResult;
import com.example.glass_project.data.model.Refraction.RefractionRecord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RefractionRecordAdapter extends RecyclerView.Adapter<RefractionRecordAdapter.ViewHolder> {

    private List<RefractionRecord> records;
    private Context context;
    private Map<Integer, List<MeasurementResult>> measurementResultsMap = new HashMap<>();

    public RefractionRecordAdapter(List<RefractionRecord> records, Context context) {
        this.records = records;
        this.context = context;
    }

    public void setMeasurementResults(int recordId, List<MeasurementResult> results) {
        measurementResultsMap.put(recordId, results);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_refraction_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RefractionRecord record = records.get(position);
        holder.bind(record);

        // Set MeasurementResultAdapter for the RecyclerView
        if (measurementResultsMap.containsKey(record.getId())) {
            List<MeasurementResult> measurementResults = measurementResultsMap.get(record.getId());
            MeasurementResultAdapter adapter = new MeasurementResultAdapter(measurementResults);
            holder.measurementRecyclerView.setAdapter(adapter);
        }

        // Toggle visibility of RecyclerView
        holder.itemView.setOnClickListener(v -> {
            if (holder.measurementRecyclerView.getVisibility() == View.GONE) {
                holder.measurementRecyclerView.setVisibility(View.VISIBLE);
            } else {
                holder.measurementRecyclerView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textId, textStartDate, textStatus, textKioskName;
        RecyclerView measurementRecyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textId = itemView.findViewById(R.id.textId);
            textStartDate = itemView.findViewById(R.id.textStartDate);
            textStatus = itemView.findViewById(R.id.textStatus);
            textKioskName = itemView.findViewById(R.id.textKioskName);
            measurementRecyclerView = itemView.findViewById(R.id.measurementRecyclerView);

            measurementRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        }

        public void bind(RefractionRecord record) {
            textId.setText("ID: " + record.getId());
            textStartDate.setText("Start Time: " + record.getStartTime());
            textStatus.setText("Status: " + (record.isStatus() ? "Active" : "Inactive"));

            if (!record.getKiosks().isEmpty()) {
                textKioskName.setText("Kiosk: " + record.getKiosks().get(0).getName());
            }
        }
    }
}

