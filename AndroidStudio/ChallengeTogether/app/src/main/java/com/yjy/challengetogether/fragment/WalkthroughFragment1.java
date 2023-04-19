package com.yjy.challengetogether.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.yjy.challengetogether.R;

public class WalkthroughFragment1 extends Fragment {
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle saveInstanceState) {
        view = inflater.inflate(R.layout.fragment_walkthrough1, container, false);
        return view;
    }
}