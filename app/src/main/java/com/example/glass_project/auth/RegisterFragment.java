package com.example.glass_project.auth;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.glass_project.R;
import com.example.glass_project.product.ProductsActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterFragment extends Fragment {

    private EditText username, password, email, phoneNumber;
    private ImageView imgTogglePassword;
    private boolean isPasswordVisible = false;
    private static final String TAG = "RegisterFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        username = view.findViewById(R.id.editUsername);
        password = view.findViewById(R.id.editPassword);
        email = view.findViewById(R.id.editEmailAddress);
        phoneNumber = view.findViewById(R.id.editPhoneNumber);
        Button registerButton = view.findViewById(R.id.btn_sign_up);
        imgTogglePassword = view.findViewById(R.id.imgTogglePassword);

        // Toggle password visibility
        imgTogglePassword.setOnClickListener(v -> togglePasswordVisibility());

        // Register button click listener
        registerButton.setOnClickListener(v -> {
            if (validateInputs()) {
                registerUser(
                        username.getText().toString(),
                        password.getText().toString(),
                        email.getText().toString(),
                        phoneNumber.getText().toString()
                );
            }
        });

        return view;
    }

    // Toggle password visibility method
    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            imgTogglePassword.setImageResource(R.drawable.baseline_remove_red_eye_24); // Closed eye icon
        } else {
            password.setInputType(InputType.TYPE_CLASS_TEXT);
            imgTogglePassword.setImageResource(R.drawable.eye_open); // Open eye icon
        }
        isPasswordVisible = !isPasswordVisible;
        password.setSelection(password.length());
    }

    // Validate inputs method
    private boolean validateInputs() {
        String usernameInput = username.getText().toString().trim();
        String passwordInput = password.getText().toString().trim();
        String emailInput = email.getText().toString().trim();
        String phoneInput = phoneNumber.getText().toString().trim();

        if (usernameInput.isEmpty()) {
            username.setError("Username is required");
            return false;
        }

        if (emailInput.isEmpty()) {
            email.setError("Email is required");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            email.setError("Please enter a valid email address");
            return false;
        }

        if (phoneInput.isEmpty()) {
            phoneNumber.setError("Phone number is required");
            return false;
        } else if (!Patterns.PHONE.matcher(phoneInput).matches() || phoneInput.length() != 10) {
            phoneNumber.setError("Please enter a valid 10-digit phone number");
            return false;
        }

        if (passwordInput.isEmpty()) {
            password.setError("Password is required");
            return false;
        } else if (passwordInput.length() < 6) {
            password.setError("Password should be at least 6 characters");
            return false;
        }

        return true;
    }

    // Call API to register the user
    private void registerUser(String username, String password, String email, String phoneNumber) {
        new RegisterUserTask().execute(username, password, email, phoneNumber);
    }

    // AsyncTask to handle API request in background
    private class RegisterUserTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String username = params[0];
            String password = params[1];
            String email = params[2];
            String phoneNumber = params[3];

            try {
                String BaseUrl = baseUrl.BASE_URL;
                String baseUrl = BaseUrl + "/api/accounts/register";
                URL url = new URL(baseUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setDoOutput(true);

                // Request body
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("username", username);
                jsonBody.put("password", password);
                jsonBody.put("email", email);
                jsonBody.put("phoneNumber", phoneNumber);

                // Write data to output stream
                try (OutputStream os = urlConnection.getOutputStream()) {
                    byte[] input = jsonBody.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                    return "Registration successful";
                } else {
                    // Read error response from the server
                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream()));
                    StringBuilder errorMessage = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        errorMessage.append(line);
                    }
                    return "Registration failed: " + errorMessage.toString();
                }

            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage(), e);
                return "Error during registration";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(requireContext(), result, Toast.LENGTH_SHORT).show();
            if (result.equals("Registration successful")) {
                navigateToMainActivity();
            }
        }
    }

    // Navigate to ProductsActivity after successful registration
    private void navigateToMainActivity() {
        Intent intent = new Intent(requireContext(), ProductsActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }
}
