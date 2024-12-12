package com.example.glass_project.config;

public class Config {
    private static final String ENCRYPTED_BASE_URL = "aHR0cHM6Ly92aXNpb251cC5henVyZXdlYnNpdGVzLm5ldA==";

    // Phương thức giải mã và truy cập giá trị BASE_URL
    public static String getBaseUrl() {
        return Encryptor.decrypt(ENCRYPTED_BASE_URL);
    }
}
