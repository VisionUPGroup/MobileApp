package com.example.glass_project.data.adapter;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.glass_project.R;
import com.example.glass_project.data.model.other.TimelineItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder> {

    private List<TimelineItem> timelineItemList;

    public TimelineAdapter(List<TimelineItem> timelineItemList) {
        this.timelineItemList = timelineItemList;

        // Sort the list by date (newest first)
        sortTimelineItemsByDate();
        Log.d("test", "TimelineAdapter: "+timelineItemList);
    }

    private void sortTimelineItemsByDate() {
        // Define the date format used in the timelineItem date field (dd/MM/yyyy-HH:mm)
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy-HH:mm");

        // Log danh sách trước khi sắp xếp
        Log.d("TimelineAdapter", "Before sorting: " + timelineItemList);

        // Find the first item with image
        TimelineItem imageItem = null;
        for (TimelineItem item : timelineItemList) {
            if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
                imageItem = item;
                break;  // Lấy item đầu tiên có hình ảnh
            }
        }

        // Remove the item with image from the list if found
        if (imageItem != null) {
            timelineItemList.remove(imageItem);
        }

        // Sort the rest of the items by date (oldest first)
        Collections.sort(timelineItemList, new Comparator<TimelineItem>() {
            @Override
            public int compare(TimelineItem item1, TimelineItem item2) {
                try {
                    // Log các item đang so sánh
                    Log.d("TimelineAdapter", "Comparing: " + item1.getDate() + " vs " + item2.getDate());

                    // Chuẩn hóa chuỗi ngày giờ để loại bỏ phần microseconds (nếu có)
                    String dateStr1 = item1.getDate();
                    String dateStr2 = item2.getDate();

                    // Nếu có độ chính xác phần microseconds, cắt bỏ phần không cần thiết
                    if (dateStr1.length() > 16) {
                        dateStr1 = dateStr1.substring(0, 16);  // Cắt bớt nếu cần
                    }
                    if (dateStr2.length() > 16) {
                        dateStr2 = dateStr2.substring(0, 16);  // Cắt bớt nếu cần
                    }

                    // Log các chuỗi ngày giờ sau khi chuẩn hóa
                    Log.d("TimelineAdapter", "Normalized dates: " + dateStr1 + " vs " + dateStr2);

                    // Sử dụng ngày giờ chuẩn (chỉ đến phút)
                    Date date1 = dateFormat.parse(dateStr1);
                    Date date2 = dateFormat.parse(dateStr2);

                    // Log kết quả so sánh
                    Log.d("TimelineAdapter", "Result of comparison: " + date1.compareTo(date2));

                    return date1.compareTo(date2); // Sort by date (oldest first)
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });

        // Log danh sách sau khi sắp xếp
        Log.d("TimelineAdapter", "After sorting (without image item): " + timelineItemList);

        // If there was an image item, add it to the first position
        if (imageItem != null) {
            timelineItemList.add(0, imageItem);
        }

        // Log lại danh sách sau khi thêm item có ảnh vào đầu
        Log.d("TimelineAdapter", "Final list after adding image item: " + timelineItemList);
    }






    @Override
    public TimelineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.timeline_item_layout, parent, false);
        return new TimelineViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TimelineViewHolder holder, int position) {
        TimelineItem timelineItem = timelineItemList.get(position);

        // Set title and description
        holder.titleTextView.setText(timelineItem.getTitle());

        // Check if description is null or empty
        if (timelineItem.getDescription() == null || timelineItem.getDescription().isEmpty()) {
            holder.descriptionTextView.setVisibility(View.GONE);
        } else {
            holder.descriptionTextView.setVisibility(View.VISIBLE);
            holder.descriptionTextView.setText(timelineItem.getDescription());
        }

        // Define the possible date formats
        SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        SimpleDateFormat inputFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy-HH:mm");

        try {
            Date date = null;
            // Try to parse with the first format
            try {
                date = inputFormat1.parse(timelineItem.getDate());
            } catch (ParseException e) {
                // If the first format fails, try the second format
                date = inputFormat2.parse(timelineItem.getDate());
            }

            // If date is successfully parsed, format it and set it to the TextView
            if (date != null) {
                String formattedDate = outputFormat.format(date);
                holder.dateTextView.setText(formattedDate);
            } else {
                holder.dateTextView.setText("Invalid date");  // Fallback text if parsing fails
            }
        } catch (ParseException e) {
            e.printStackTrace();
            holder.dateTextView.setText("Invalid date");  // Fallback text in case of an error
        }

        // If the item is the first one, change the color and dot style
        if (position == 0) {
            holder.titleTextView.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.black));
            holder.dateTextView.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.black));
            holder.descriptionTextView.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.black));

            holder.timelineDot.setImageResource(R.drawable.dotactive);
        } else {
            holder.titleTextView.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.gray));
            holder.dateTextView.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.gray));
            holder.descriptionTextView.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.gray));

            holder.timelineDot.setImageResource(R.drawable.dotunactive);
        }

        // If there is an image (imageUrl), load it with Glide
        if (timelineItem.getImageUrl() != null && !timelineItem.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(timelineItem.getImageUrl())
                    .into(holder.imageView);
            holder.imageView.setVisibility(View.VISIBLE);
            holder.dateTextView.setVisibility(View.GONE);

            // Add a click listener to the image to open the image in a dialog
            holder.imageView.setOnClickListener(v -> {
                showImageDialog(holder.itemView.getContext(), timelineItem.getImageUrl());
            });

        } else {
            holder.imageView.setVisibility(View.GONE);
        }
    }

    // Method to show the image in a dialog
    private void showImageDialog(Context context, String imageUrl) {
        // Create a custom dialog
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_full_screen_image);

        // Initialize ImageView in dialog
        ImageView fullScreenImageView = dialog.findViewById(R.id.full_screen_image_view);

        // Load the image into the ImageView using Glide
        Glide.with(context)
                .load(imageUrl)
                .into(fullScreenImageView);

        // Set dialog properties
        dialog.setCancelable(true); // You can set whether the dialog can be canceled by tapping outside
        dialog.show();
    }





    @Override
    public int getItemCount() {
        return timelineItemList.size();
    }

    public static class TimelineViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, descriptionTextView, dateTextView;
        ImageView imageView, timelineDot;

        public TimelineViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.timeline_title);
            descriptionTextView = itemView.findViewById(R.id.timeline_description);
            dateTextView = itemView.findViewById(R.id.timeline_date);
            imageView = itemView.findViewById(R.id.timeline_image);
            timelineDot = itemView.findViewById(R.id.timeline_dot);  // Khai báo ImageView cho dot

            // Set fixed size for the image
            ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
            layoutParams.width = 200; // Set width to 200px
            layoutParams.height = 200; // Set height to 200px
            imageView.setLayoutParams(layoutParams);
        }
    }

}
