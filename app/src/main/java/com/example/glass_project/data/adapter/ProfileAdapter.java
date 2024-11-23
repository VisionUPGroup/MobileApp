package com.example.glass_project.data.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.glass_project.R;
import com.example.glass_project.data.model.profile.Profile;
import com.example.glass_project.product.ui.RefractionRecord.RefractionRecordActivity;
import com.example.glass_project.product.ui.VisualAcuityRecord.VisualAcuityRecordActivity;
import com.example.glass_project.product.ui.profile.ProfileFragment;
import com.example.glass_project.product.ui.profile.UpdateProfileDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProfileAdapter extends ArrayAdapter<Profile> {
    private final Context context;
    private final ProfileFragment profileFragment;
    private List<Profile> profileList;

    public ProfileAdapter(Context context, ProfileFragment profileFragment, List<Profile> profiles) {
        super(context, 0, profiles);
        this.context = context;
        this.profileFragment = profileFragment;
        this.profileList = profiles;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_profile, parent, false);
        }

        // Get profile for the current position
        Profile profile = getItem(position);

        // Bind Views
        TextView textId = convertView.findViewById(R.id.textId);
        TextView textPhoneNumber = convertView.findViewById(R.id.textPhoneNumber);
        TextView textAge = convertView.findViewById(R.id.textAge);
        ImageView dropdownIcon = convertView.findViewById(R.id.dropdownIcon);
        LinearLayout dropdownContent = convertView.findViewById(R.id.dropdownContent);
        CardView cardView = convertView.findViewById(R.id.cardView);

        // Set Data
        if (profile != null) {
            textId.setText(profile.getId() + " - " + profile.getFullName());
            textPhoneNumber.setText(profile.getPhoneNumber());

            // Calculate and set age
            int age = calculateAge(profile.getBirthday());
            textAge.setText("Tuổi: " + age);

            // Kiểm tra trạng thái và ẩn nếu `status` là false
            if (!profile.isStatus()) {
                convertView.setVisibility(View.GONE);
                convertView.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
            } else {
                convertView.setVisibility(View.VISIBLE);
                convertView.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
        }

        // Toggle Dropdown Content
        cardView.setOnClickListener(v -> {
            if (dropdownContent.getVisibility() == View.GONE) {
                dropdownContent.setVisibility(View.VISIBLE);
                dropdownIcon.setRotation(180); // Rotate icon downwards
                cardView.setCardBackgroundColor(context.getResources().getColor(R.color.gray1));
            } else {
                dropdownContent.setVisibility(View.GONE);
                dropdownIcon.setRotation(0); // Reset icon rotation
                cardView.setCardBackgroundColor(context.getResources().getColor(R.color.white));
            }
        });

        // Handle Click Events for Dropdown Options
        TextView detail1 = convertView.findViewById(R.id.detail1);
        TextView detail2 = convertView.findViewById(R.id.detail2);
        TextView detail3 = convertView.findViewById(R.id.detail3);
        detail1.setOnClickListener(v -> {
            UpdateProfileDialogFragment dialog = UpdateProfileDialogFragment.newInstance(profile);
            dialog.setTargetFragment(profileFragment, 0); // Truyền chính xác Fragment hiện tại
            dialog.show(profileFragment.getParentFragmentManager(), "UpdateProfileDialog");
        });
        detail2.setOnClickListener(v -> {
            Intent intent = new Intent(context, VisualAcuityRecordActivity.class);
            intent.putExtra("profileID", profile.getId());
            context.startActivity(intent);
        });
        detail3.setOnClickListener(v -> {
            Intent intent = new Intent(context, RefractionRecordActivity.class);
            intent.putExtra("profileID", profile.getId());
            context.startActivity(intent);
        });

        return convertView;
    }

    public void updateProfiles(List<Profile> updatedProfiles) {
        this.profileList = updatedProfiles;
        notifyDataSetChanged();
    }

    private int calculateAge(String birthday) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date birthDate = dateFormat.parse(birthday);
            Calendar birthDay = Calendar.getInstance();
            birthDay.setTime(birthDate);

            Calendar today = Calendar.getInstance();

            int age = today.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);

            if (today.get(Calendar.DAY_OF_YEAR) < birthDay.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }

            return age;
        } catch (Exception e) {
            e.printStackTrace();
            return 0; // Nếu có lỗi, trả về 0
        }
    }
}
