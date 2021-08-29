package com.mobdeve.s11.g25.pidyon.controller;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.mobdeve.s11.g25.pidyon.controller.fragments.AboutMeFragment;
import com.mobdeve.s11.g25.pidyon.controller.fragments.BlockedUsersFragment;
import com.mobdeve.s11.g25.pidyon.controller.fragments.FriendRequestsFragment;

public class ProfileFragmentAdapter extends FragmentStateAdapter {
    public ProfileFragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position) {
            case 1:
                return new FriendRequestsFragment();
            case 2:
                return new BlockedUsersFragment();
        }

        return new AboutMeFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
