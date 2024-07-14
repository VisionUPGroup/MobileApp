package com.example.glass_project.product.ui.shoppingCart;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glass_project.DTO.CartDTO.CartSummaryResponse;
import com.example.glass_project.R;
import com.example.glass_project.config.repositories.CartRepositories;
import com.example.glass_project.config.services.CartServices;
import com.example.glass_project.data.adapter.OrderDetailAdapter;
import com.example.glass_project.databinding.FragmentShoppingCartBinding;
import com.example.glass_project.product.ui.order.OrderSummary;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShoppingCartFragment extends Fragment {

    private RecyclerView recyclerView;
    private FragmentShoppingCartBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentShoppingCartBinding.inflate(inflater, container, false);

        View rootView = binding.getRoot();

        recyclerView = binding.recyclerViewOrderList;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        CartServices cartServices = CartRepositories.cartServices();

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserSession", 0);
        String accountId = sharedPreferences.getString("id", null);
        Log.d("AccountID", "onCreateView: " + accountId);

        if (accountId != null) {
            Call<CartSummaryResponse> call = cartServices.getbyaccountid(Integer.parseInt(accountId));

            call.enqueue(new Callback<CartSummaryResponse>() {
                @SuppressLint({"SetTextI18n", "DefaultLocale", "UseCompatLoadingForColorStateLists"})
                @Override
                public void onResponse(@NonNull Call<CartSummaryResponse> call, @NonNull Response<CartSummaryResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        CartSummaryResponse cartSummaryResponse = response.body();

                        if (cartSummaryResponse.getCartDetails().isEmpty()) {
                            binding.textNoShoppingCart.setVisibility(View.VISIBLE);
                            binding.paymentDetailsBox.buttonProceedToCheckout.setEnabled(false);
                            binding.paymentDetailsBox.buttonProceedToCheckout.setBackgroundTintList(getResources().getColorStateList(R.color.gray));
                            binding.paymentDetailsBox.textSubtotalValue.setText("0 VND"); // Reset subtotal
                            binding.paymentDetailsBox.textShipmentCostValue.setText("0 VND"); // Reset shipment cost
                            binding.paymentDetailsBox.textGrandTotalValue.setText("0 VND"); // Reset grand total
                            return;
                        }

                        OrderDetailAdapter orderDetailAdapter = new OrderDetailAdapter(getContext(), cartSummaryResponse.getCartDetails());
                        recyclerView.setVisibility(View.VISIBLE);
                        recyclerView.setAdapter(orderDetailAdapter);

                        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
                        String formattedEyeGlassPrice = formatter.format(cartSummaryResponse.getTotalPrice());

                        binding.paymentDetailsBox.textSubtotalValue.setText(formattedEyeGlassPrice + " VND");
                        binding.paymentDetailsBox.textShipmentCostValue.setText("30.000 VND");

                        BigDecimal grandTotal = cartSummaryResponse.getTotalPrice().add(new BigDecimal(30000));
                        String formattedGrandTotal = formatter.format(grandTotal);
                        binding.paymentDetailsBox.textGrandTotalValue.setText(formattedGrandTotal + " VND");

                        binding.paymentDetailsBox.buttonProceedToCheckout.setOnClickListener(v -> {
                            // Proceed to checkout
                            Toast.makeText(getContext(), "Proceed to checkout", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getContext(), OrderSummary.class);
//                            intent.putExtra("totalPrice", grandTotal);
                            startActivity(intent);
                        });

                        binding.textNoShoppingCart.setVisibility(View.GONE);



                        // Update payment details
                    } else {
                        Log.e("API Error", "Response unsuccessful or empty");
                        recyclerView.setVisibility(View.GONE);
                        binding.textNoShoppingCart.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<CartSummaryResponse> call, @NonNull Throwable t) {
                    Log.e("API Error", t.getMessage(), t);
                    recyclerView.setVisibility(View.GONE);
                    binding.textNoShoppingCart.setVisibility(View.VISIBLE);
                }


            });
        }

        return rootView;
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
