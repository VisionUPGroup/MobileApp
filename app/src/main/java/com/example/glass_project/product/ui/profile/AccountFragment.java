package com.example.glass_project.product.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.glass_project.MainActivity;
import com.example.glass_project.R;
import com.example.glass_project.product.ui.account.ChangePasswordActivity;
import com.example.glass_project.product.ui.account.EditProfileActivity;
import com.example.glass_project.product.ui.order.history.ListOrderHistoryActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.zeugmasolutions.localehelper.LocaleHelper;

import java.util.Locale;

public class AccountFragment extends Fragment {

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private Switch languageSwitch;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        TextView usernameView = view.findViewById(R.id.username);
        TextView emailView = view.findViewById(R.id.email);
        TextView userOrdersButton = view.findViewById(R.id.user_order);
        TextView editProfileButton = view.findViewById(R.id.edit_profile);
        TextView changePasswordTextView = view.findViewById(R.id.changepassword);
        Button logoutButton = view.findViewById(R.id.btn_sign_out);
        languageSwitch = view.findViewById(R.id.language_switch);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        String email = sharedPreferences.getString("email", "");

        usernameView.setText(username);
        emailView.setText(email);

        // Set language switch state based on current language
        String currentLanguage = sharedPreferences.getString("Language", "en");
        languageSwitch.setChecked(currentLanguage.equals("vi"));

        languageSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                changeLanguage(requireContext(), "vi");
            } else {
                changeLanguage(requireContext(), "en");
            }
        });

        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });

        changePasswordTextView.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
            startActivity(intent);
        });

        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        logoutButton.setOnClickListener(v -> signOutAndStartSignInActivity());

        userOrdersButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ListOrderHistoryActivity.class);
            startActivity(intent);
        });

        return view;
    }

    public void changeLanguage(Context context, String languageCode) {
        // Đặt locale mới
        Locale newLocale = new Locale(languageCode);
        LocaleHelper.INSTANCE.setLocale(context, newLocale);

        // Lưu ngôn ngữ vào SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Language", languageCode);
        editor.apply();

        // Làm mới giao diện bằng cách khởi động lại Activity
        Intent intent = requireActivity().getIntent();
        requireActivity().finish(); // Đảm bảo đóng Activity hiện tại
        requireActivity().overridePendingTransition(0, 0); // Xóa hiệu ứng chuyển Activity
        startActivity(intent);
    }
    @Override
    public void onResume() {
        super.onResume();
        // Làm mới nội dung giao diện tại đây
        TextView usernameView = requireView().findViewById(R.id.username);
    }


    private void signOutAndStartSignInActivity() {
        mAuth.signOut();

        // Clear user session
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        mGoogleSignInClient.signOut().addOnCompleteListener(requireActivity(), task -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });
    }
}
