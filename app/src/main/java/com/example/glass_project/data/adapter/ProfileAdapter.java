package com.example.glass_project.data.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.glass_project.R;
import com.example.glass_project.data.model.profile.Profile;
import com.example.glass_project.product.ui.profile.ProfileDetailActivity;

import java.util.List;

public class ProfileAdapter extends ArrayAdapter<Profile> {
    private Context context;

    public ProfileAdapter(Context context, List<Profile> profiles) {
        super(context, 0, profiles);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_profile, parent, false);
        }

        Profile profile = getItem(position);

        TextView id = convertView.findViewById(R.id.textId);
        //TextView fullName = convertView.findViewById(R.id.textFullName);
        TextView phoneNumber = convertView.findViewById(R.id.textPhoneNumber);
        //TextView address = convertView.findViewById(R.id.textAddress);
        //ImageView imageView = convertView.findViewById(R.id.imageUrl);

        id.setText(+ profile.getId() +" - "+ profile.getFullName());
        //fullName.setText(profile.getFullName());
        phoneNumber.setText( profile.getPhoneNumber());
        //address.setText("Address: " + profile.getAddress());

//        if (profile.getUrlImage() != null && !profile.getUrlImage().isEmpty()) {
//            Glide.with(context)
//                    .load(profile.getUrlImage())
//                    .placeholder(R.drawable.default_image)
//                    .into(imageView);
//        } else {
//            imageView.setImageResource(R.drawable.default_image);
//        }

        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProfileDetailActivity.class);
            intent.putExtra("profile", profile);
            context.startActivity(intent);
        });

        return convertView;
    }
}
