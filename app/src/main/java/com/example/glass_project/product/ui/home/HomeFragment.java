package com.example.glass_project.product.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glass_project.data.adapter.GlassAdapter;
import com.example.glass_project.data.model.Glass;
import com.example.glass_project.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private List<Glass> glassList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        glassList = getGlassList();
        GlassAdapter adapter = new GlassAdapter(getContext(), glassList);
        recyclerView.setAdapter(adapter);

        return root;
    }

    private List<Glass> getGlassList() {
        List<Glass> productList = new ArrayList<>();
        productList.add(new Glass(1, "Glass 1", "Glass 1", 20));
        productList.add(new Glass(2, "Glass 2", "Glass 2", 30));
        productList.add(new Glass(3, "Glass 3", "Glass 3", 30));
        productList.add(new Glass(4, "Glass 4", "Glass 4", 30));
        productList.add(new Glass(5, "Glass 5", "Glass 5", 30));
        productList.add(new Glass(6, "Glass 6", "Glass 6", 30));
        productList.add(new Glass(7, "Glass 7", "Glass 7", 30));
        // Add more products here
        return productList;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}