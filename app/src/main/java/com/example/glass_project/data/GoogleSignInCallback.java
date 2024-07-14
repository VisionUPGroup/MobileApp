package com.example.glass_project.data;

public interface GoogleSignInCallback {
    void onGoogleSignInSuccess(String username, String email);
    void onGoogleSignInFailure(String errorMessage);
}
