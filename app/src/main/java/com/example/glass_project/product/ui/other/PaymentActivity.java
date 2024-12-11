package com.example.glass_project.product.ui.other;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.example.glass_project.R;
import com.example.glass_project.product.ui.order.history.OrderDetailActivity;


public class PaymentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.glass_project.R.layout.activity_payment);  // Layout chứa WebView

        WebView webView = findViewById(R.id.webViewPayment);
        webView.getSettings().setJavaScriptEnabled(true); // Bật JavaScript

        // Nhận URL thanh toán từ Intent
        String paymentUrl = getIntent().getStringExtra("paymentUrl");

        // Đảm bảo URL hợp lệ trước khi load
        if (paymentUrl != null && !paymentUrl.isEmpty()) {
            webView.loadUrl(paymentUrl);
        }

        // Cấu hình WebView để xử lý các liên kết trong WebView
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();

                // Kiểm tra URL trả về có chứa "order-success"
                if (url.contains("order-success")) {
                    // Nếu URL chứa "order-success", chuyển đến OrderSuccessActivity
                    Intent intent = new Intent(PaymentActivity.this, OrderDetailActivity.class);

                    // Truyền orderId hoặc các thông tin khác vào Intent
                    int orderId = getIntent().getIntExtra("orderId", -1);  // Nhận orderId từ Intent trước đó
                    if (orderId != -1) {
                        intent.putExtra("orderId", orderId);  // Truyền orderId vào Intent
                    }
                    startActivity(intent);
                    finish();  // Kết thúc Activity hiện tại
                    return true;  // Đã xử lý URL này, không cần load nữa
                }

                // Tiếp tục load URL trong WebView nếu không phải URL "order-success"
                return false;
            }
        });

        // Cấu hình WebView để xử lý sự kiện JavaScript, chẳng hạn như mở các cửa sổ pop-up
        webView.setWebChromeClient(new WebChromeClient());
    }
}
