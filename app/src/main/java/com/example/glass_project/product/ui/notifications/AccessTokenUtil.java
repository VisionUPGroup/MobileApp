package com.example.glass_project.product.ui.notifications;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.ServiceAccountCredentials;

import java.io.FileInputStream;
import java.io.IOException;

public class AccessTokenUtil {
    public static String getAccessToken() throws IOException {
        // Path to the Service Account JSON key file
        String jsonKeyPath = "D:\\GitHub\\Glass-Android-Java\\app\\daring-keep-410204-firebase-adminsdk-c8jgn-0ea74372ef.json";

        GoogleCredentials credentials = ServiceAccountCredentials.fromStream(new FileInputStream(jsonKeyPath))
                .createScoped("https://www.googleapis.com/auth/cloud-platform");

        credentials.refreshIfExpired();
        AccessToken token = credentials.getAccessToken();

        return token.getTokenValue();
    }
}
