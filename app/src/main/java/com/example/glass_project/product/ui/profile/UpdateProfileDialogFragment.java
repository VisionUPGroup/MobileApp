package com.example.glass_project.product.ui.profile;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.example.glass_project.R;
import com.example.glass_project.config.baseUrl;
import com.example.glass_project.data.model.profile.Profile;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateProfileDialogFragment extends DialogFragment {

    private Profile profile;
    private OnProfileUpdatedListener listener;
    private Uri selectedImageUri;
    private boolean isEditing = false;
    ImageView imgProfileImage;
    public interface OnProfileUpdatedListener {
        void onProfileUpdated();
    }

    public static UpdateProfileDialogFragment newInstance(Profile profile) {
        UpdateProfileDialogFragment fragment = new UpdateProfileDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("profile", profile);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnProfileUpdatedListener) {
            listener = (OnProfileUpdatedListener) context;
        } else if (getTargetFragment() instanceof OnProfileUpdatedListener) {
            listener = (OnProfileUpdatedListener) getTargetFragment();
        } else {
            throw new RuntimeException("Must implement OnProfileUpdatedListener");
        }
    }

    @Nullable
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_update_profile, null);

        profile = (Profile) getArguments().getSerializable("profile");

        EditText etFullName = view.findViewById(R.id.etFullName);
        EditText etPhoneNumber = view.findViewById(R.id.etPhoneNumber);
        EditText etAddress = view.findViewById(R.id.etAddress);
        EditText etBirthday = view.findViewById(R.id.etBirthday);
        imgProfileImage = view.findViewById(R.id.imgProfileImage);
        Button btnSelectImage = view.findViewById(R.id.btnSelectImage);
        Button btnSave = view.findViewById(R.id.btnSave);
        Button btnCancel = view.findViewById(R.id.btnCancel);
        Button btnDelete = view.findViewById(R.id.btnDelete);

        // Disable các EditText ban đầu
        setEditTextsEnabled(false, etFullName, etPhoneNumber, etAddress, etBirthday);

        etFullName.setText(profile.getFullName());
        etPhoneNumber.setText(profile.getPhoneNumber());
        etAddress.setText(profile.getAddress());
        etBirthday.setText(profile.getBirthday());

        // Hiển thị hình ảnh
        if (profile.getUrlImage() != null && !profile.getUrlImage().isEmpty()) {
            Glide.with(requireContext())
                    .load(profile.getUrlImage())
                    .placeholder(R.drawable.default_image)
                    .error(R.drawable.default_image)
                    .into(imgProfileImage);
        } else {
            imgProfileImage.setImageResource(R.drawable.default_image);
        }

        // Nút chọn ảnh
        btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 101);
        });

        // Nút "Cập nhật" để bật EditText và nút Save
        btnSave.setOnClickListener(v -> {
            if (!isEditing) {
                // Nếu chưa trong trạng thái chỉnh sửa, bật các EditText để chỉnh sửa
                isEditing = true;
                setEditTextsEnabled(true, etFullName, etPhoneNumber, etAddress, etBirthday);
                btnSave.setText("Lưu"); // Thay đổi nhãn nút thành "Lưu"
            } else {
                // Nếu đang trong trạng thái chỉnh sửa, gọi API cập nhật
                String fullName = etFullName.getText().toString().trim();
                String phoneNumber = etPhoneNumber.getText().toString().trim();
                String address = etAddress.getText().toString().trim();
                String birthday = etBirthday.getText().toString().trim();

                if (!validateInputs(fullName, phoneNumber, address, birthday, etFullName, etPhoneNumber, etAddress, etBirthday)) {
                    return;
                }

                profile.setFullName(fullName);
                profile.setPhoneNumber(phoneNumber);
                profile.setAddress(address);
                profile.setBirthday(birthday);

                new UpdateProfileTask().execute(profile);
            }
        });


        btnCancel.setOnClickListener(v -> dismiss());
        btnDelete.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc chắn muốn xóa hồ sơ này không?")
                    .setPositiveButton("Xóa", (dialog, which) -> new DeleteProfileTask().execute(profile.getId()))
                    .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        return new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setView(view)
                .create();
    }

    private void setEditTextsEnabled(boolean enabled, EditText... editTexts) {
        for (EditText editText : editTexts) {
            editText.setEnabled(enabled);
        }
    }
    private class DeleteProfileTask extends AsyncTask<Integer, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Integer... profileIds) {
            try {
                SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                String accessToken = sharedPreferences.getString("accessToken", "");
                if (accessToken.isEmpty()) return false;

                String BaseUrl = baseUrl.BASE_URL;
                URL url = new URL(BaseUrl + "/api/accounts/profiles/" + profileIds[0]);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("DELETE");
                connection.setRequestProperty("Authorization", "Bearer " + accessToken);

                int responseCode = connection.getResponseCode();
                return responseCode == HttpURLConnection.HTTP_NO_CONTENT || responseCode == HttpURLConnection.HTTP_OK;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == requireActivity().RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            if (imgProfileImage != null) {
                imgProfileImage.setImageURI(selectedImageUri); // Hiển thị ảnh đã chọn từ thư viện
            } else {
                Toast.makeText(getContext(), "Không thể hiển thị hình ảnh, hãy thử lại.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private boolean validateInputs(String fullName, String phoneNumber, String address, String birthday, EditText etFullName, EditText etPhoneNumber, EditText etAddress, EditText etBirthday) {
        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Họ và tên không được để trống");
            return false;
        }

        if (TextUtils.isEmpty(phoneNumber) || !Patterns.PHONE.matcher(phoneNumber).matches()) {
            etPhoneNumber.setError("Số điện thoại không hợp lệ");
            return false;
        }

        if (TextUtils.isEmpty(address)) {
            etAddress.setError("Địa chỉ không được để trống");
            return false;
        }

        if (TextUtils.isEmpty(birthday)) {
            etBirthday.setError("Ngày sinh không được để trống");
            return false;
        }

        return true;
    }

    private class UpdateProfileTask extends AsyncTask<Profile, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Profile... profiles) {
            try {
                Profile updatedProfile = profiles[0];
                SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                String accessToken = sharedPreferences.getString("accessToken", "");

                if (accessToken.isEmpty()) {
                    return false;
                }

                String BaseUrl = baseUrl.BASE_URL;
                URL url = new URL(BaseUrl + "/api/profiles");

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("PUT");
                urlConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setDoOutput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("id", updatedProfile.getId());
                jsonParam.put("accountID", updatedProfile.getAccountID());
                jsonParam.put("fullName", updatedProfile.getFullName());
                jsonParam.put("phoneNumber", updatedProfile.getPhoneNumber());
                jsonParam.put("address", updatedProfile.getAddress());
                jsonParam.put("birthday", updatedProfile.getBirthday());
                jsonParam.put("status", updatedProfile.isStatus());

                OutputStream os = urlConnection.getOutputStream();
                os.write(jsonParam.toString().getBytes("UTF-8"));
                os.close();

                int responseCode = urlConnection.getResponseCode();
                return responseCode == HttpURLConnection.HTTP_OK;

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                if (selectedImageUri != null) {
                    // Chỉ gọi upload ảnh từ main thread
                    new UploadImageTask(profile.getId()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, selectedImageUri);
                } else {
                    Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    if (listener != null) {
                        listener.onProfileUpdated();
                    }
                    dismiss();
                }
            } else {
                Toast.makeText(getContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private class UploadImageTask extends AsyncTask<Uri, Void, Boolean> {
        private final int profileId;

        public UploadImageTask(int profileId) {
            this.profileId = profileId;
        }

        @Override
        protected Boolean doInBackground(Uri... uris) {
            try {
                Uri imageUri = uris[0];
                SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                String accessToken = sharedPreferences.getString("accessToken", "");

                if (accessToken.isEmpty()) {
                    return false;
                }

                String BaseUrl = baseUrl.BASE_URL;
                URL url = new URL(BaseUrl + "/api/accounts/profiles/upload_image");

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Authorization", "Bearer " + accessToken);
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=boundary");
                connection.setDoOutput(true);

                String boundary = "boundary";
                DataOutputStream os = new DataOutputStream(connection.getOutputStream());

                os.writeBytes("--" + boundary + "\r\n");
                os.writeBytes("Content-Disposition: form-data; name=\"Id\"\r\n\r\n" + profileId + "\r\n");

                os.writeBytes("--" + boundary + "\r\n");
                os.writeBytes("Content-Disposition: form-data; name=\"Image\"; filename=\"image.jpg\"\r\n");
                os.writeBytes("Content-Type: image/jpeg\r\n\r\n");

                InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri);
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                inputStream.close();

                os.writeBytes("\r\n--" + boundary + "--\r\n");
                os.flush();
                os.close();

                int responseCode = connection.getResponseCode();
                return responseCode == HttpURLConnection.HTTP_OK;

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(getContext(), "Tải lên ảnh thành công", Toast.LENGTH_SHORT).show();
                if (listener != null) {
                    listener.onProfileUpdated();
                }
                dismiss();
            } else {
                Toast.makeText(getContext(), "Tải lên ảnh thất bại", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
