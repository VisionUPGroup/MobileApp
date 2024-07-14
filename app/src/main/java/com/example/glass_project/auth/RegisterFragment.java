package com.example.glass_project.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.glass_project.R;
import com.example.glass_project.config.repositories.AuthRepositories;
import com.example.glass_project.config.services.AuthServices;
import com.example.glass_project.data.GoogleSignInCallback;
import com.example.glass_project.data.model.LoginResponse;
import com.example.glass_project.data.model.request.RegisterRequest;
import com.example.glass_project.product.ProductsActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;

import retrofit2.Call;

public class RegisterFragment extends Fragment {

    private static final int RC_SIGN_IN = 9001;
    private EditText username;
    private EditText password;
    private AuthServices authServices;
    private GoogleSignInClient googleSignInClient;
    private GoogleSignInCallback googleSignInCallback;

    public RegisterFragment() {
        // Required empty public constructor
    }

    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

        googleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        authServices = AuthRepositories.getAuthServices();

        Button register = view.findViewById(R.id.btn_sign_up);
        Button registerGoogle = view.findViewById(R.id.btnGoogleLogin);

        username = view.findViewById(R.id.editUsername);
        password = view.findViewById(R.id.editPassword);
        EditText email = view.findViewById(R.id.editEmailAddress);

        register.setOnClickListener(v -> registerUser(username.getText().toString(), password.getText().toString(), email.getText().toString()));
        registerGoogle.setOnClickListener(v -> registerGoogleUser(new GoogleSignInCallback() {
            @Override
            public void onGoogleSignInSuccess(String username, String email) {
                registerUser(username, "123456", email);
            }

            @Override
            public void onGoogleSignInFailure(String errorMessage) {
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        }));

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            try {
                GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
                if (account != null) {
                    String email = account.getEmail();
                    String displayName = account.getDisplayName();

                    Log.d("RegisterFragment", "Email: " + email);
                    Log.d("RegisterFragment", "Display Name: " + displayName);

                    // Gọi callback với thông tin đăng nhập thành công
                    if (googleSignInCallback != null) {
                        googleSignInCallback.onGoogleSignInSuccess(displayName, email);
                    }
                }
            } catch (ApiException e) {
                Log.w("RegisterFragment", "Google sign in failed", e);
                Toast.makeText(getActivity(), "Google sign in failed", Toast.LENGTH_SHORT).show();
                // Gọi callback với lỗi đăng nhập
                if (googleSignInCallback != null) {
                    googleSignInCallback.onGoogleSignInFailure("Google sign in failed");
                }
            }
        } else {
            Log.d("RegisterFragment", "onActivityResult: requestCode mismatch");
        }
    }

    private void registerGoogleUser(GoogleSignInCallback callback) {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

        // Lưu lại callback để sử dụng trong onActivityResult
        this.googleSignInCallback = callback;
    }

    private void registerUser(String username, String password, String email) {
        RegisterRequest register = new RegisterRequest(username, password, email);

        Call<LoginResponse> call = authServices.register(register);

        call.enqueue(new retrofit2.Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull retrofit2.Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();
                    if (loginResponse != null) {
                        saveUserDetails(String.valueOf(loginResponse.getId()), loginResponse.getUsername(), loginResponse.getEmail());
                        Toast.makeText(getActivity(), "Registration successful", Toast.LENGTH_SHORT).show();
                        navigateToMainActivity();
                    } else {
                        Log.e("RegisterFragment", "Error registering user");
                        Toast.makeText(getActivity(), "Error registering user", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("RegisterFragment", "Response not successful: " + response.errorBody().toString());
                    Toast.makeText(getActivity(), "Response not successful", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                Log.e("RegisterFragment", "Error registering user", t);
                Toast.makeText(getActivity(), "Error registering user", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToMainActivity() {
        Log.d("RegisterFragment", "Navigating to ProductsActivity");
        Intent intent = new Intent(getActivity(), ProductsActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    private void saveUserDetails(String id, String username, String email) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("id", id);
        editor.putString("username", username);
        editor.putString("email", email);
        editor.apply();
    }
}


