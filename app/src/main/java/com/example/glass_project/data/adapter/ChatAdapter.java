package com.example.glass_project.data.adapter;

import android.graphics.Bitmap;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glass_project.databinding.ItemContainerReceivedMessageBinding;
import com.example.glass_project.databinding.ItemContainerSentMessageBinding;
import com.example.glass_project.product.ui.message.ChatMesssage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private  final List<ChatMesssage> chatMesssageList;
    private final Bitmap receiverProfileImage;
    private final String senderId;

    public ChatAdapter(List<ChatMesssage> chatMesssageList, Bitmap receiverProfileImage, String senderId) {
        this.chatMesssageList = chatMesssageList;
        this.receiverProfileImage = receiverProfileImage;
        this.senderId = senderId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerSentMessageBinding binding;

        SentMessageViewHolder(ItemContainerSentMessageBinding itemContainerSentMessageBinding) {
            super(itemContainerSentMessageBinding.getRoot());
            binding = itemContainerSentMessageBinding;

        }
        void  setData(ChatMesssage chatMesssage) {
            binding.tvMessage.setText(chatMesssage.message);
            binding.tvDatetime.setText(chatMesssage.dateTime);
        }
    }
    static class ReceiverMessageHolder extends RecyclerView.ViewHolder {
        private final ItemContainerReceivedMessageBinding binding;
        ReceiverMessageHolder(ItemContainerReceivedMessageBinding itemContainerReceivedMessageBinding) {
            super(itemContainerReceivedMessageBinding.getRoot());
            binding = itemContainerReceivedMessageBinding;
        }
        void setData(ChatMesssage chatMesssage, Bitmap receiverProfileImage) {
            binding.tvMessagechat.setText(chatMesssage.message);
            binding.tvDatetimechat.setText(chatMesssage.dateTime);
            binding.imgProfilechat.setImageBitmap(receiverProfileImage);
        }
    }
}
