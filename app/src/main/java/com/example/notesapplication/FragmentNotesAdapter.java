package com.example.notesapplication;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class FragmentNotesAdapter extends FragmentStateAdapter {

    private List<Fragment> lists;

    public FragmentNotesAdapter(@NonNull FragmentActivity fragmentActivity,List<Fragment> lists) {
        super(fragmentActivity);
        this.lists = lists;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return lists.get(position);
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }
}
