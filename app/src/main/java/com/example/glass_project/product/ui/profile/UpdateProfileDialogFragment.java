package com.example.glass_project.product.ui.profile;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.glass_project.R;
import com.example.glass_project.auth.baseUrl;
import com.example.glass_project.data.model.profile.Profile;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

public class UpdateProfileDialogFragment extends DialogFragment {

    private Profile profile;
    private OnProfileUpdatedListener listener;

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

        Button btnSave = view.findViewById(R.id.btnSave);
        Button btnCancel = view.findViewById(R.id.btnCancel);

        etFullName.setText(profile.getFullName());
        etPhoneNumber.setText(profile.getPhoneNumber());
        etAddress.setText(profile.getAddress());
        etBirthday.setText(profile.getBirthday());

        btnSave.setOnClickListener(v -> {
            if (TextUtils.isEmpty(etFullName.getText().toString())) {
                etFullName.setError("Full name is required");
                return;
            }

            profile.setFullName(etFullName.getText().toString());
            profile.setPhoneNumber(etPhoneNumber.getText().toString());
            profile.setAddress(etAddress.getText().toString());
            profile.setBirthday(etBirthday.getText().toString());

            new UpdateProfileTask().execute(profile);
        });
        etBirthday.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view1, year1, month1, dayOfMonth) -> {
                        String selectedDate = String.format("%04d-%02d-%02d", year1, month1 + 1, dayOfMonth);
                        etBirthday.setText(selectedDate);
                    },
                    year,
                    month,
                    day
            );
            datePickerDialog.show();
        });
        btnCancel.setOnClickListener(v -> dismiss());

        return new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setView(view)
                .create();
    }

    private class UpdateProfileTask extends AsyncTask<Profile, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Profile... profiles) {
            try {
                Profile updatedProfile = profiles[0];
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
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
                jsonParam.put("urlImage", updatedProfile.getUrlImage());
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
                Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                if (listener != null) {
                    listener.onProfileUpdated();
                }
                dismiss();
            } else {
                Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
