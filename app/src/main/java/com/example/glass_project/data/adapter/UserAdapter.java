package com.example.glass_project.data.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glass_project.data.model.User;
import com.example.glass_project.databinding.ItemContainerUserBinding;
import com.example.glass_project.product.ui.message.listeners.UsersListener;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder>{

    private final List<User> userList;
    private final UsersListener usersListener;

    public UserAdapter(List<User> userList, UsersListener usersListener) {
        this.userList = userList;
        this.usersListener = usersListener;
    }
    class UserViewHolder extends RecyclerView.ViewHolder{

        ItemContainerUserBinding binding;
        UserViewHolder(ItemContainerUserBinding itemContainerUserBinding){
            super(itemContainerUserBinding.getRoot());
            binding = itemContainerUserBinding;
        }
        void setUserData (User user) {
            binding.textMessage.setText(user.name);
            binding.textEmail.setText(user.email);
            binding.imgProfile.setImageBitmap(getuserImage(user.image));
            binding.getRoot().setOnClickListener(v -> usersListener.onUserCliked(user));
        }
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerUserBinding itemContainerUserBinding = ItemContainerUserBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new UserViewHolder(itemContainerUserBinding);
    }

    @Override
    public void onBindViewHolder( UserViewHolder holder, int position) {
        holder.setUserData(userList.get(position));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    private Bitmap getuserImage(String encodeImage) {
        byte[] bytes = Base64.decode(encodeImage, Base64.DEFAULT);
        return  BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

}
