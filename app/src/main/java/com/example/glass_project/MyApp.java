package com.example.glass_project;

import android.app.Application;
import android.content.Context;

import com.zeugmasolutions.localehelper.LocaleHelper;

import java.util.Locale;

public class MyApp extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.INSTANCE.setLocale(base, getSavedLocale(base)));
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private Locale getSavedLocale(Context context) {
        String savedLanguage = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
                .getString("Language", "en"); // Mặc định là tiếng Anh
        return new Locale(savedLanguage);
    }
}
