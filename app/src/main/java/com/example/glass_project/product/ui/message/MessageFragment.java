package com.example.glass_project.product.ui.message;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.glass_project.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.glass_project.R;
import com.example.glass_project.databinding.FragmentMessageBinding;
import com.example.glass_project.product.ui.message.activity.UserActivity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class MessageFragment extends Fragment {
    private FragmentMessageBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MessageViewModel messageViewModel =
                new ViewModelProvider(this).get(MessageViewModel.class);

        binding = FragmentMessageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        preferenceManager = new PreferenceManager(getActivity().getApplicationContext());

        if (binding == null || preferenceManager == null) {
            showToast("Error initializing view");
            return root;
        }

        loadUserDetails();
        getToken();
        setListeners();

        return root;
    }

    private void setListeners() {
        binding.fabNewChat.setOnClickListener(v ->
                startActivity(new Intent(getActivity().getApplicationContext(), UserActivity.class)));
    }

    private void loadUserDetails() {
        String name = preferenceManager.getString(Constants.KEY_NAME);
        if (name != null && !name.isEmpty()) {
            binding.textMessage.setText(name);
        } else {
            showToast("Error loading user name");
            binding.textMessage.setText("Unknown User"); // Đặt giá trị mặc định nếu không có tên
        }

        String image = preferenceManager.getString(Constants.KEY_IMAGE);
        if (image != null && !image.isEmpty()) {
            try {
                byte[] bytes = Base64.decode(image, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                binding.imgProfile.setImageBitmap(bitmap);
            } catch (IllegalArgumentException e) {
                showToast("Error decoding image");
            }
        } else {
            showToast("Error loading user image");
            binding.imgProfile.setImageResource(R.drawable.ic_face_6_24); // Đặt hình ảnh mặc định nếu không có hình ảnh
        }
    }

    private void showToast(String message) {
        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken)
                .addOnFailureListener(e -> showToast("Unable to get FCM token"));
    }

    private void updateToken(String token) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = preferenceManager.getString(Constants.KEY_USER_ID);

        if (userId != null && !userId.isEmpty()) {
            DocumentReference documentReference = db.collection(Constants.KEY_COLECTION_USERS).document(userId);
            documentReference.update(Constants.KEY_FCM_TOKEN, token)
                    .addOnFailureListener(e -> showToast("Unable to update token"));
        } else {
            showToast("User ID not found");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}