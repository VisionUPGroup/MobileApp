package com.example.glass_project.data.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glass_project.R;
import com.example.glass_project.data.model.Lens;
import com.example.glass_project.detail.EnterPrescriptionActivity;

import java.util.List;

public class LensItemAdapter extends RecyclerView.Adapter<LensItemAdapter.ViewHolder> {
    private final Context context;
    private final List<Lens> lensList;

    public LensItemAdapter(List<Lens> lensList, Context context) {
        this.lensList = lensList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_lens_item, parent, false);
        return new LensItemAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Lens lens = lensList.get(position);
        Log.i("Lens Item", "Data: " + lens.getLensName() + " " + lens.getLensDescription() + " " + lens.getLensPrice());

        holder.tvName.setText(lens.getLensName());
        holder.tvDescription.setText(lens.getLensDescription());
        holder.tvPrice.setText("$" + lens.getLensPrice());

        holder.constraint.setOnClickListener(v -> {
            Log.i("Lens Item", "Clicked: " + lens.getLensName());

            Intent intent = new Intent(context, EnterPrescriptionActivity.class);
            intent.putExtra("lens_id", lens.getId());
            context.startActivity(intent);

        });

    }

    @Override
    public int getItemCount() {
        return lensList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDescription, tvPrice;
        ConstraintLayout constraint;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            constraint = itemView.findViewById(R.id.clItemLensItem);
        }
    }
}
