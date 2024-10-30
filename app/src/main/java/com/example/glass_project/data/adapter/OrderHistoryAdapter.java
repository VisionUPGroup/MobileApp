package com.example.glass_project.data.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.glass_project.DTO.OrderDetailDTO.OrderDetailDTO;
import com.example.glass_project.R;
import com.example.glass_project.data.model.OrderHistoryItem;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderHistoryAdapter extends ArrayAdapter<OrderHistoryItem> {

    private Context mContext;
    private List<OrderHistoryItem> mOrderHistoryItems;
    private LayoutInflater mInflater;

    public OrderHistoryAdapter(Context context, List<OrderHistoryItem> orderHistoryItems) {
        super(context, 0, orderHistoryItems);
        mContext = context;
        mOrderHistoryItems = orderHistoryItems;
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = mInflater.inflate(R.layout.item_history_order, parent, false);
        }

        OrderHistoryItem currentItem = mOrderHistoryItems.get(position);

        // Bind data to views in item_history_order.xml
        TextView textViewOrderId = listItemView.findViewById(R.id.textViewOrderId);
        TextView textViewOrderDate = listItemView.findViewById(R.id.textViewOrderDate);
        TextView textViewSenderAddress = listItemView.findViewById(R.id.textViewSenderAddress);
        TextView textViewReceiverAddress = listItemView.findViewById(R.id.textViewReceiverAddress);
        TextView textViewCode = listItemView.findViewById(R.id.textViewCode);
        TextView textViewTotal = listItemView.findViewById(R.id.textViewTotal);
        TextView textViewProcess = listItemView.findViewById(R.id.textViewProcess);
        TextView textViewOrderDetails = listItemView.findViewById(R.id.textViewOrderDetails);
        Button buttonCompleteOrder = listItemView.findViewById(R.id.buttonCompleteOrder);

        if (currentItem != null) {
            textViewOrderId.setText("Order ID: " + currentItem.getId());
            textViewOrderDate.setText("Order Date: " + formatOrderDate(currentItem.getOrderDate()));
            textViewSenderAddress.setText("Sender Address: " + currentItem.getSenderAddress());
            textViewReceiverAddress.setText("Receiver Address: " + currentItem.getReceiverAddress());
            textViewCode.setText("Code: " + currentItem.getCode());
            textViewTotal.setText("Total: " + formatCurrency(currentItem.getTotal()) + " VND");
            textViewProcess.setText("Process: " + formatProcess(currentItem.getProcess()));
            textViewOrderDetails.setText(formatOrderDetails(currentItem.getOrderDetails()));

            // Set color based on process status
            int color = getColorForProcess(currentItem.getProcess());
            textViewProcess.setTextColor(color);

            // Set button visibility based on process status
            if (currentItem.getProcess() == 3) {
                buttonCompleteOrder.setVisibility(View.VISIBLE);
                buttonCompleteOrder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new CompleteOrderTask(currentItem).execute();
                    }
                });
            } else {
                buttonCompleteOrder.setVisibility(View.GONE);
            }
        }

        return listItemView;
    }

    private String formatOrderDate(String orderDate) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            Date date = inputFormat.parse(orderDate);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return orderDate; // Return original orderDate in case of parsing error
        }
    }

    private String formatProcess(int process) {
        switch (process) {
            case 0:
                return "Pending";
            case 1:
                return "Processing";
            case 2:
                return "Shipping";
            case 3:
                return "Delivered";
            case 4:
                return "Completed";
            case 5:
                return "Canceled";
            default:
                return "Unknown";
        }
    }

    private String formatOrderDetails(List<OrderDetailDTO> orderDetails) {
        StringBuilder details = new StringBuilder();
        for (OrderDetailDTO detail : orderDetails) {
            details.append("\n")
                    .append("Eye Glass Name: ").append(detail.getEyeGlassName()).append("\n")
                    .append("Eye Glass Price: ").append(formatCurrency(detail.getEyeGlassPrice())).append(" VND\n")
                    .append("Lens Name: ").append(detail.getLensName()).append("\n")
                    .append("Lens Price: ").append(formatCurrency(detail.getLensPrice())).append(" VND\n")
                    .append("Quantity: ").append(detail.getQuantity()).append("\n");
        }
        return details.toString();
    }

    private int getColorForProcess(int process) {
        Resources resources = mContext.getResources();
        switch (process) {
            case 0:
                return resources.getColor(R.color.pendingColor); // Define pendingColor in colors.xml
            case 1:
                return resources.getColor(R.color.processingColor); // Define processingColor in colors.xml
            case 2:
                return resources.getColor(R.color.shippingColor); // Define shippingColor in colors.xml
            case 3:
                return resources.getColor(R.color.deliveredColor); // Define deliveredColor in colors.xml
            case 4:
                return resources.getColor(R.color.completedColor); // Define completedColor in colors.xml
            case 5:
                return resources.getColor(R.color.canceledColor); // Define canceledColor in colors.xml
            default:
                return Color.BLACK; // Default color
        }
    }

    private String formatCurrency(double amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return formatter.format(amount).replace(NumberFormat.getCurrencyInstance().getCurrency().getSymbol(), "").trim();
    }

    private class CompleteOrderTask extends AsyncTask<Void, Void, Boolean> {
        private OrderHistoryItem order;

        public CompleteOrderTask(OrderHistoryItem order) {
            this.order = order;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL("https://visionup.azurewebsites.net/api/Order");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("PUT");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setDoOutput(true);

                String jsonInputString = String.format(
                        "{\"id\": %d, \"status\": true, \"senderAddress\": \"%s\", \"receiverAddress\": \"%s\", \"code\": \"%s\", \"process\": 4}",
                        order.getId(),
                        order.getSenderAddress(),
                        order.getReceiverAddress(),
                        order.getCode()
                );

                try (OutputStream os = urlConnection.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = urlConnection.getResponseCode();
                return responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_NO_CONTENT;

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                // Update the process status to "Completed"
                order.setProcess(4);
                notifyDataSetChanged();
            } else {
                // Handle the failure case (e.g., show a toast message)
            }
        }
    }
}
