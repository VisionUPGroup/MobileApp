package com.example.glass_project.data.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glass_project.R;
import com.example.glass_project.data.model.kiosk.Kiosk;

import java.util.List;

public class KioskAdapter extends RecyclerView.Adapter<KioskAdapter.KioskViewHolder> {

    private List<Kiosk> kiosks;
    private OnItemClickListener listener;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(Kiosk kiosk);
    }

    public KioskAdapter(List<Kiosk> kiosks, OnItemClickListener listener, Context context) {
        this.kiosks = kiosks;
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public KioskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.kiosk_item, parent, false);
        return new KioskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KioskViewHolder holder, int position) {
        Kiosk kiosk = kiosks.get(position);

        // Cập nhật thông tin trong các TextView
        holder.nameTextView.setText(kiosk.getName());
        holder.addressTextView.setText("Địa chi: " + kiosk.getAddress());
        holder.phoneNumberTextView.setText("SĐT: " +kiosk.getPhoneNumber());
        holder.emailTextView.setText("Mail: " +kiosk.getEmail());

        // Sự kiện bấm vào item để xem chi tiết
        holder.itemView.setOnClickListener(v -> listener.onItemClick(kiosk));

        // Sự kiện gọi điện
        holder.btnCall.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + kiosk.getPhoneNumber()));
            context.startActivity(intent);
        });

        // Sự kiện gửi email
        holder.btnEmail.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + kiosk.getEmail()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return kiosks.size();
    }

    static class KioskViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView addressTextView;
        TextView phoneNumberTextView;
        TextView emailTextView;
        ImageButton btnCall;
        ImageButton btnEmail;

        public KioskViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.kiosk_name);
            addressTextView = itemView.findViewById(R.id.kiosk_address);
            phoneNumberTextView = itemView.findViewById(R.id.kiosk_phone_number);
            emailTextView = itemView.findViewById(R.id.kiosk_email);
            btnCall = itemView.findViewById(R.id.btn_call);
            btnEmail = itemView.findViewById(R.id.btn_email);
        }
    }
}
