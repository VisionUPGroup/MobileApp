package com.example.glass_project.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
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
import com.example.glass_project.config.repositories.AuthRepositories;
import com.example.glass_project.config.services.AuthServices;
import com.example.glass_project.data.GoogleSignInCallback;
import com.example.glass_project.data.model.Login;
import com.example.glass_project.data.model.LoginResponse;
import com.example.glass_project.data.model.request.RegisterRequest;
import com.example.glass_project.product.ProductsActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {

    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth auth;
    private EditText username, password;
    private AuthServices apiService;
    private GoogleSignInClient googleSignInClient;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private GoogleSignInCallback googleSignInCallback;
    private ImageView imgTogglePassword;
    private boolean isPasswordVisible = false;

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


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

        googleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        auth = FirebaseAuth.getInstance();
        apiService = AuthRepositories.getAuthServices();

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            navigateToMainActivity();
        }

        username = view.findViewById(R.id.editTextTextEmailAddress);
        password = view.findViewById(R.id.editTextTextPassword);
        imgTogglePassword = view.findViewById(R.id.imgTogglePassword);

        Button btnLogin = view.findViewById(R.id.button);
        Button btnGoogleLogin = view.findViewById(R.id.btnGoogleLogin);
        imgTogglePassword.setOnClickListener(v -> togglePasswordVisibility());

        // Set click listener for Google login button
        btnGoogleLogin.setOnClickListener(v -> loginWithGoogle(new GoogleSignInCallback() {
            @Override
            public void onGoogleSignInSuccess(String username, String email) {
                // Handle Google sign in success
                login(username, "123456", email);
            }

            @Override
            public void onGoogleSignInFailure(String errorMessage) {
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        }));

        // Set click listener for Email/Password login button
        btnLogin.setOnClickListener(v -> {

            login(username.getText().toString(), password.getText().toString(), "");
        });

        return view;
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            imgTogglePassword.setImageResource(R.drawable.baseline_remove_red_eye_24);
        } else {
            password.setInputType(InputType.TYPE_CLASS_TEXT);
            imgTogglePassword.setImageResource(R.drawable.eye_open); // Add an icon for eye off
        }
        isPasswordVisible = !isPasswordVisible;
        password.setSelection(password.length()); // Move the cursor to the end
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

    private void loginWithGoogle(GoogleSignInCallback callback) {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

        // Lưu lại callback để sử dụng trong onActivityResult
        this.googleSignInCallback = callback;
    }

    private void loginAccount(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Login login = new Login(username, password);

        Call<LoginResponse> call = apiService.login(login);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                LoginResponse loginResponse = response.body();

                if (response.isSuccessful() && loginResponse != null) {
                    Toast.makeText(getActivity(), "Login successful", Toast.LENGTH_SHORT).show();
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


    private void login(String username, String pass, String email) {
        if (username.isEmpty() || pass.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Login login = new Login(username, pass);
        apiService.login(login).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (!response.isSuccessful() && response.body() == null) {
                    RegisterRequest registerRequest = new RegisterRequest(username, pass, email);

                    register(registerRequest);
                }

                if (response.body() != null && response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Login successful", Toast.LENGTH_SHORT).show();
                    LoginResponse loginResponse = response.body();
                    saveDeviceTokenToFirestore(loginResponse.getEmail());
                    Log.d("LoginFragment", "Login successful: " + loginResponse.getUsername() + " " + loginResponse.getEmail() + " " + loginResponse.getId());

                    saveUserDetails(String.valueOf(loginResponse.getId()), loginResponse.getUsername(), loginResponse.getEmail());
                    navigateToMainActivity();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable throwable) {
                Toast.makeText(getActivity(), "An error occurred: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void register(RegisterRequest registerRequest) {
        apiService.register(registerRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getActivity(), "Register successful", Toast.LENGTH_SHORT).show();
                    LoginResponse loginResponse = response.body();

                    saveUserDetails(String.valueOf(loginResponse.getId()), loginResponse.getUsername(), loginResponse.getEmail());
                    saveDeviceTokenToFirestore(loginResponse.getEmail());
                    navigateToMainActivity();
                } else {
                    Toast.makeText(getActivity(), "Register failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable throwable) {
                Toast.makeText(getActivity(), "An error occurred: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Method to initiate Google sign in flow
//    private void signIn(GoogleSignInCallback callback) {
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
////                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();
//
//        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
//        Intent signInIntent = googleSignInClient.getSignInIntent();
//        startActivityForResult(signInIntent, RC_SIGN_IN);
//
//        // Save the callback to handle Google sign in result
//        this.googleSignInCallback = callback;
//    }

    // Handle Google sign in result
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == RC_SIGN_IN) {
//            try {
//                GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
//                if (account != null) {
//                    firebaseAuthWithGoogle(account.getIdToken()); // Authenticate with Firebase using Google credentials
//                    if (googleSignInCallback != null) {
//                        googleSignInCallback.onGoogleSignInSuccess(account.getDisplayName(), account.getEmail()); // Callback on success
//                    }
//                }
//            } catch (ApiException e) {
//                Toast.makeText(getActivity(), "Google sign in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                if (googleSignInCallback != null) {
//                    googleSignInCallback.onGoogleSignInFailure("Google sign in failed"); // Callback on failure
//                }
//            }
//        }
//    }

//    // Authenticate with Firebase using Google credentials
//    private void firebaseAuthWithGoogle(String idToken) {
//        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
//        auth.signInWithCredential(credential)
//                .addOnCompleteListener(requireActivity(), task -> {
//                    if (task.isSuccessful()) {
//                        FirebaseUser user = auth.getCurrentUser();
//                        Toast.makeText(getActivity(), "Signed in as " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
//
//                        // Save DEVICE_TOKEN to Firestore
////                        saveDeviceTokenToFirestore(user.getUid());
//
//                        navigateToMainActivity();
//                    } else {
//                        Toast.makeText(getActivity(), "Authentication failed", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }

    // Handle login with email and password

    // Save FCM token to Firestore
    private void saveDeviceTokenToFirestore(String userId) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("LoginFragment", "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    String token = task.getResult();
                    if (token != null) {
                        Query query = db.collection("DeviceToken").whereEqualTo("token", token).limit(1);
                        query.get().addOnCompleteListener(queryTask -> {
                            if (queryTask.isSuccessful()) {
                                QuerySnapshot snapshot = queryTask.getResult();
                                if (snapshot != null && !snapshot.isEmpty()) {
                                    Log.d("LoginFragment", "Device token already exists in Firestore");
                                } else {
                                    Map<String, Object> tokenData = new HashMap<>();
                                    tokenData.put("token", token);

                                    db.collection("DeviceToken").document(userId)
                                            .set(tokenData)
                                            .addOnSuccessListener(aVoid -> Log.d("LoginFragment", "Device token saved to Firestore"))
                                            .addOnFailureListener(e -> Log.e("LoginFragment", "Failed to save device token: " + e.getMessage()));
                                }
                            } else {
                                Log.e("LoginFragment", "Error checking device token existence: ", queryTask.getException());
                            }
                        });
                    } else {
                        Log.e("LoginFragment", "FCM token is null");
                    }
                });
    }

    // Navigate to main activity
    private void navigateToMainActivity() {
        Log.d("LoginFragment", "Navigating to ProductsActivity");
        Intent intent = new Intent(getActivity(), ProductsActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    // Save user details to SharedPreferences
    private void saveUserDetails(String id, String username, String email) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("id", id);
        editor.putString("username", username);
        editor.putString("email", email);
        editor.apply();
    }
}