package com.example.glass_project.config;

import android.util.Base64;

public class Encryptor {
    // Mã hóa chuỗi
    public static String encrypt(String plainText) {
        return Base64.encodeToString(plainText.getBytes(), Base64.DEFAULT);
    }

    // Giải mã chuỗi
    public static String decrypt(String encryptedText) {
        return new String(Base64.decode(encryptedText, Base64.DEFAULT));
    }
}
