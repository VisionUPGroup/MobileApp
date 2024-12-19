package com.example.glass_project.product.ui.other;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;

import com.example.glass_project.R;
import com.example.glass_project.config.Config;
import com.example.glass_project.data.model.order.OrderDetail;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class CreateReportDialog {

    private final Context context;
    private final int orderId;
    private final List<OrderDetail> orderDetailsList;
    private final ActivityResultLauncher<Intent> imagePickerLauncher;

    private Uri selectedImageUri;
    private AlertDialog dialog;
    private ImageView imageViewUploaded;

    public CreateReportDialog(Context context, int orderId, List<OrderDetail> orderDetailsList, ActivityResultLauncher<Intent> imagePickerLauncher) {
        this.context = context;
        this.orderId = orderId;
        this.orderDetailsList = orderDetailsList;
        this.imagePickerLauncher = imagePickerLauncher;
    }

    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_create_report, null);
        builder.setView(dialogView);

        EditText editTextOrderId = dialogView.findViewById(R.id.editText_orderID);
        EditText editTextDescription = dialogView.findViewById(R.id.editText_description);
        Spinner spinnerOrderDetails = dialogView.findViewById(R.id.spinner_order_details);
        Spinner spinnerType = dialogView.findViewById(R.id.spinner_type);
        Button btnUploadImage = dialogView.findViewById(R.id.btn_upload_image);
        imageViewUploaded = dialogView.findViewById(R.id.imageView_uploaded);
        Button btnBack = dialogView.findViewById(R.id.btn_back);
        Button btnSubmit = dialogView.findViewById(R.id.btn_submit);
        TextView tvProduct = dialogView.findViewById(R.id.textView_product_label);
        editTextOrderId.setText(String.valueOf(orderId));
        editTextOrderId.setEnabled(false);

        // Spinner Type
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item,
                new String[]{"Vấn đề sản phẩm", "Vấn đề giao hàng", "Vấn đề khách hàng", "Dịch vụ khách hàng", "Khác"});
        spinnerType.setAdapter(typeAdapter);

        // Spinner OrderDetails
        ArrayAdapter<String> orderAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item);
        for (OrderDetail detail : orderDetailsList) {
            orderAdapter.add(detail.getProductGlass().getId() + " - " + detail.getProductGlass().getEyeGlass().getName());
        }
        spinnerOrderDetails.setAdapter(orderAdapter);
        spinnerOrderDetails.setVisibility(View.GONE);
        tvProduct.setVisibility(View.GONE);

        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerOrderDetails.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
                tvProduct.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Upload Image
        btnUploadImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        // Submit Button
        btnSubmit.setOnClickListener(v -> {
            String description = editTextDescription.getText().toString();
            int type = getTypeValue(spinnerType.getSelectedItem().toString());

            if (description.isEmpty() || selectedImageUri == null) {
                Toast.makeText(context, "Tất cả các trường là bắt buộc!", Toast.LENGTH_SHORT).show();
                return;
            }

            final int productGlassId; // Định nghĩa productGlassId là final
            if (type == 0) { // Nếu chọn "Vấn đề sản phẩm", lấy productGlassId từ spinner_order_details
                int position = spinnerOrderDetails.getSelectedItemPosition();
                if (position < 0) {
                    Toast.makeText(context, "Hãy chọn sản phẩm từ danh sách!", Toast.LENGTH_SHORT).show();
                    return;
                }
                productGlassId = orderDetailsList.get(position).getProductGlass().getId();
            } else {
                productGlassId = -1; // Gán giá trị mặc định nếu không phải "Vấn đề sản phẩm"
            }

            // Hiển thị hộp thoại xác nhận
            new AlertDialog.Builder(context)
                    .setMessage("Bạn có chắc chắn muốn báo cáo?")
                    .setPositiveButton("Báo cáo", (dialogInterface, i) -> {
                        // Disable all fields to prevent editing after submission
                        disableAllFields(editTextDescription, spinnerOrderDetails, spinnerType, btnUploadImage, btnSubmit, btnBack, tvProduct);

                        // Gọi API upload
                        new UploadReportTask().execute(orderId, productGlassId, description, selectedImageUri, type);
                    })
                    .setNegativeButton("Hủy", (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                    })
                    .show();
        });



        btnBack.setOnClickListener(v -> dialog.dismiss());

        dialog = builder.create();
        dialog.show();
    }
    private void disableAllFields(EditText descriptionField, Spinner orderDetailsSpinner, Spinner typeSpinner,
                                  Button uploadImageButton, Button submitButton, Button backButton,TextView tvProduct) {
        descriptionField.setEnabled(false);
        orderDetailsSpinner.setEnabled(false);
        typeSpinner.setEnabled(false);
        uploadImageButton.setEnabled(false);
        submitButton.setEnabled(false);
        backButton.setEnabled(false);

    }

    public void setImageUri(Uri uri) {
        this.selectedImageUri = uri;
        imageViewUploaded.setImageURI(uri);
        imageViewUploaded.setVisibility(View.VISIBLE);
    }

    private int getTypeValue(String type) {
        switch (type) {
            case "Vấn đề sản phẩm":
                return 0;
            case "Vấn đề giao hàng":
                return 1;
            case "Vấn đề khách hàng":
                return 2;
            case "Dịch vụ khách hàng":
                return 3;
            default:
                return 4;
        }
    }

    private class UploadReportTask extends AsyncTask<Object, Void, String> {
        @Override
        protected String doInBackground(Object... params) {
            int orderId = (int) params[0];
            int productGlassId = (int) params[1];
            String description = (String) params[2];
            Uri imageUri = (Uri) params[3];
            int type = (int) params[4];

            try {
                SharedPreferences sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                String accessToken = sharedPreferences.getString("accessToken", "");

                URL url = new URL(Config.getBaseUrl() + "/api/reports");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Authorization", "Bearer " + accessToken);
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=*****");
                connection.setDoOutput(true);

                OutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                writeMultipartData(outputStream, orderId, productGlassId, description, imageUri, type);
                outputStream.flush();
                outputStream.close();
                int responseCode = connection.getResponseCode();
                InputStream responseStream;
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    responseStream = connection.getInputStream();
                } else {
                    responseStream = connection.getErrorStream();
                }
                String responseBody = convertStreamToString(responseStream);
                return responseBody;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                // Kiểm tra thông báo trong kết quả trả về
                if (result.contains("Order has been processed for exchange")) {
                    Toast.makeText(context, "Đơn hàng đã được xử lý để đổi hàng!", Toast.LENGTH_SHORT).show();
                } else if (result.contains("Requested exchange quantity exceeds available quantity for this product glass")) {
                    Toast.makeText(context, "Số lượng yêu cầu đổi vượt quá số lượng có sẵn của sản phẩm này!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Báo cáo đã được gửi thành công!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            } else {
                Toast.makeText(context, "Lỗi khi gửi báo cáo! Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
            }
        }

    }
    private String convertStreamToString(InputStream inputStream) {
        try {
            java.util.Scanner scanner = new java.util.Scanner(inputStream).useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private void writeMultipartData(OutputStream outputStream, int orderId, int productGlassId,
                                    String description, Uri imageUri, int type) throws Exception {
        String boundary = "*****";
        String twoHyphens = "--";
        String lineEnd = "\r\n";
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

        // OrderID
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"OrderID\"" + lineEnd + lineEnd);
        dataOutputStream.writeBytes(String.valueOf(orderId) + lineEnd);

        // Description
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"Description\"" + lineEnd + lineEnd);
        dataOutputStream.writeBytes(description + lineEnd);

        // ProductGlassID (chỉ thêm khi Type = 0)
        if (type == 0) {
            dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
            dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"ProductGlassID\"" + lineEnd + lineEnd);
            dataOutputStream.writeBytes(String.valueOf(productGlassId) + lineEnd);
        }

        // Type
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"Type\"" + lineEnd + lineEnd);
        dataOutputStream.writeBytes(String.valueOf(type) + lineEnd);

        // Image
        String fileName = getFileNameFromUri(imageUri);
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"Image\"; filename=\"image.jpg\"" + lineEnd);
        dataOutputStream.writeBytes("Content-Type: image/jpeg" + lineEnd + lineEnd);

        InputStream fileInputStream = context.getContentResolver().openInputStream(imageUri);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
            dataOutputStream.write(buffer, 0, bytesRead);
        }
        fileInputStream.close();

        dataOutputStream.writeBytes(lineEnd);

        // Kết thúc multipart
        dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
        dataOutputStream.flush();
        dataOutputStream.close();
    }

    private String getFileNameFromUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
                    if (index >= 0) {
                        result = cursor.getString(index);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

}
