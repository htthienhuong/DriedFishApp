package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class HistoryFragment extends Fragment {
    View view;
    RecyclerView recyclerViewViewedFish;
    FishAdapter fishAdapter;
    FragmentActivity activity;
    Context context;
    Button clearHistoryButton;

    public HistoryFragment(){
        super(R.layout.fragment_history);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this.getActivity();
        if (activity == null)
            Log.d("HistoryFragment", "activity is null");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);

        if (view != null) {
            context = view.getContext();
            recyclerViewViewedFish = view.findViewById(R.id.rcv_viewed_fish);
            clearHistoryButton = view.findViewById(R.id.clear_history_button);
        } else
            Log.d("HistoryFragment", "onCreateView: view is null");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerViewViewedFish.setLayoutManager(linearLayoutManager);

        fishAdapter = new FishAdapter(this.activity, LoginActivity.currentUser.getViewedFishItems());
        recyclerViewViewedFish.setAdapter(fishAdapter);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(
                view.getContext(), DividerItemDecoration.VERTICAL
        );
        recyclerViewViewedFish.addItemDecoration(itemDecoration);

        clearHistoryButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View view) {
                // Clear in local User's data
                LoginActivity.currentUser.clearAllViewedFishes();

                // Update changes to realtime database
                LoginActivity.currentUserReference.setValue(LoginActivity.currentUser);

                // Update Adapter and RecyclerView
                fishAdapter = new FishAdapter(activity, LoginActivity.currentUser.getViewedFishItems());
                recyclerViewViewedFish.setAdapter(fishAdapter);

                // Show toast notification
                Toast.makeText(
                        context,
                        getResources().getString(R.string.cleared_history_notification),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        return view;
    }
}