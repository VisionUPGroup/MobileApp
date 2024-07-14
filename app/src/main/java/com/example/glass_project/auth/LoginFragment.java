package com.example.glass_project.auth;

import android.annotation.SuppressLint;
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
import com.example.glass_project.data.model.Login;
import com.example.glass_project.data.model.LoginResponse;
import com.example.glass_project.product.ProductsActivity;
import com.example.glass_project.product.ui.notifications.NotificationsActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {


    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth auth;
    private EditText username, password;
    private AuthServices apiService;


    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        // Inflate the layout for this fragment
        auth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = auth.getCurrentUser();
        apiService = AuthRepositories.getAuthServices();

        if (currentUser != null) {
            Intent intent = new Intent(getActivity(), ProductsActivity.class);
            startActivity(intent);
            requireActivity().finish();
        }

        // Get input fields
        username = view.findViewById(R.id.editTextTextEmailAddress);
        password = view.findViewById(R.id.editTextTextPassword);

        // Get buttons
        Button btnLogin = view.findViewById(R.id.button);
        Button btnGoogleLogin = view.findViewById(R.id.btnGoogleLogin);

        // Set on click listeners
        btnGoogleLogin.setOnClickListener(v -> signIn());
        btnLogin.setOnClickListener(v -> login());

        return view;
    }

    private void login() {
        String email = username.getText().toString();
        String pass = password.getText().toString();

        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Login login = new Login(email, pass);
        apiService.login(login).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getActivity(), "Login successful", Toast.LENGTH_SHORT).show();
                    LoginResponse loginResponse = response.body();

                    Log.e("LoginFragment", "Login successful: " + loginResponse.getPassword());
//                    saveToken();
                    saveUserDetails(String.valueOf(loginResponse.getId()), loginResponse.getUsername(), loginResponse.getEmail());

                    navigateToMainActivity();
                } else {
                    Toast.makeText(getActivity(), "Login failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable throwable) {
                Toast.makeText(getActivity(), "An error occurred: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            try {
                GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(getActivity(), "Google sign in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        Toast.makeText(getActivity(), "Signed in as " + user.getDisplayName(), Toast.LENGTH_SHORT).show();

                        // Lưu token từ Google SignIn vào SharedPreferences
                        saveUserDetails(user.getUid(), user.getDisplayName(), user.getEmail());

                        navigateToMainActivity();
                    } else {
                        Toast.makeText(getActivity(), "Authentication failed", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void navigateToMainActivity() {
        Log.d("LoginFragment", "Navigating to ProductsActivity");
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