package com.example.glass_project.product.ui.order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.glass_project.DTO.CartDTO.CartDetailResponse;
import com.example.glass_project.R;
import com.example.glass_project.model.Order;
import com.example.glass_project.model.ProductGlass;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class CustomAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<CartDetailResponse> items;
    private LayoutInflater inflater;

    public CustomAdapter(Context context, ArrayList<CartDetailResponse> items) {
        this.context = context;
        this.items = items;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return this.items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.glass_item, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.imageViewGlass);
        TextView textGlassName = convertView.findViewById(R.id.tvGlassName);
        TextView textLensName = convertView.findViewById(R.id.tvLensName);
        TextView textPrice = convertView.findViewById(R.id.tvPrice);
        TextView textQuantity = convertView.findViewById(R.id.tvQuantity);


        CartDetailResponse item = items.get(position);
        if (item.getEyeGlassImages() != null && !item.getEyeGlassImages().isEmpty()){
            Glide.with(context).load(item.getEyeGlassImages().get(0).getUrl()).into(imageView);
        }

        textGlassName.setText(item.getEyeGlassName());
        textQuantity.setText(String.valueOf(item.getQuantity()));
        textLensName.setText(item.getLensName());
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
        textPrice.setText(formatter.format(item.getTotalPriceProductGlass()) + " VND");
        return convertView;
    }
}
