package com.mobdeve.s11.g25.pidyon.controller;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.mobdeve.s11.g25.pidyon.controller.fragments.ChatsFragment;
import com.mobdeve.s11.g25.pidyon.controller.fragments.ContactsFragment;

public class MainFragmentAdapter extends FragmentStateAdapter {
    public MainFragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return (position == 0) ? new ChatsFragment() : new ContactsFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
