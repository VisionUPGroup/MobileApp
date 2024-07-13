package com.example.glass_project.product.ui.shoppingCart;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glass_project.DTO.CartDTO.CartSummaryResponse;
import com.example.glass_project.config.repositories.CartRepositories;
import com.example.glass_project.config.services.CartServices;
import com.example.glass_project.data.adapter.OrderDetailAdapter;
import com.example.glass_project.databinding.FragmentShoppingCartBinding;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShoppingCartFragment extends Fragment {

    private FragmentShoppingCartBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentShoppingCartBinding.inflate(inflater, container, false);

        RecyclerView recyclerView = binding.recyclerViewOrderList;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        CartServices cartServices = CartRepositories.cartServices();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserSession", 0);
        String accountid = sharedPreferences.getString("id", null);
        Log.d("AccountID", "onCreateView: " + accountid);

        Call<CartSummaryResponse> call = cartServices.getbyaccountid(Integer.parseInt(accountid));

        call.enqueue(new retrofit2.Callback<CartSummaryResponse>() {
            @Override
            public void onResponse(@NonNull Call<CartSummaryResponse> call, @NonNull Response<CartSummaryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CartSummaryResponse cartSummaryResponse = response.body();
                    OrderDetailAdapter orderDetailAdapter = new OrderDetailAdapter(getContext(), cartSummaryResponse.getCartDetails());
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.setAdapter(orderDetailAdapter);

                    binding.textNoShoppingCart.setVisibility(View.GONE);
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




        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
