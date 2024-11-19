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
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault());
            try {
                Date lastCheckup = inputFormat.parse(result.getLastCheckupDate());
                Date nextCheckup = inputFormat.parse(result.getNextCheckupDate());

                measurementDate.setText("Date: " + outputFormat.format(lastCheckup));
                lastCheckupDate.setText("Last Checkup: " + outputFormat.format(lastCheckup));
                nextCheckupDate.setText("Next Checkup: " + outputFormat.format(nextCheckup));
            } catch (Exception e) {
                measurementDate.setText("Date: " + result.getLastCheckupDate());
                lastCheckupDate.setText("Last Checkup: " + result.getLastCheckupDate());
                nextCheckupDate.setText("Next Checkup: " + result.getNextCheckupDate());
            }

            eyeSide.setText("Eye Side: " + (result.getEyeSide() == 1 ? "Right" : "Left"));
            testType.setText("Test Type: " + result.getTestType());
            spherical.setText("Spherical: " + result.getSpherical());
            cylindrical.setText("Cylindrical: " + result.getCylindrical());
            axis.setText("Axis: " + result.getAxis());
            pupilDistance.setText("Pupil Distance: " + result.getPupilDistance());
            prescriptionDetails.setText("Prescription Details: " + result.getPrescriptionDetails());
            notes.setText("Notes: " + result.getNotes());
        }
    }
}

