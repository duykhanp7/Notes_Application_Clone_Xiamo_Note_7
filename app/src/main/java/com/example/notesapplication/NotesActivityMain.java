package com.example.notesapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.notesapplication.databinding.CustomLayoutBottomAddNotesBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;


public class NotesActivityMain extends AppCompatActivity {

    private TabLayout tabLayoutNote;
    private FloatingActionButton floatingButtonAddNotes;
    private ViewPager2 viewPager2;
    private List<Fragment> lists;
    List<NoteItem> listNotes;
    private FragmentNotesAdapter fragmentNotesAdapter;

    AddNotesFragment addNotesFragment;

    public static FragmentManager fragmentManager;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.notes_activity_main);
            fragmentManager = getSupportFragmentManager();
            tabLayoutNote = findViewById(R.id.tabLayoutNotes);
            floatingButtonAddNotes = findViewById(R.id.floatingButtonAddNote);
            viewPager2 = findViewById(R.id.viewPagerNotes);
            tabLayoutNote.setTabRippleColor(null);
            tabLayoutNote.setTabGravity(TabLayout.GRAVITY_CENTER);
            tabLayoutNote.setTabMode(TabLayout.MODE_FIXED);
            tabLayoutNote.setSelectedTabIndicatorColor(android.R.color.transparent);

            InitListNoteItems();
            InitListsFragment();

            fragmentNotesAdapter = new FragmentNotesAdapter(this,lists);
            viewPager2.setAdapter(fragmentNotesAdapter);

            new TabLayoutMediator(tabLayoutNote, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
                @Override
                public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                    switch (position){
                        case 0:
                            tab.setIcon(R.drawable.ic_quote);
                            break;
                        case 1:
                            tab.setIcon(R.drawable.ic_note_add);
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + position);
                    }
                }
            }).attach();

            tabLayoutNote.getTabAt(0).getIcon().setTint(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_orange_dark));

            tabLayoutNote.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    Objects.requireNonNull(tab.getIcon()).setTint(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_orange_dark));
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    Objects.requireNonNull(tab.getIcon()).setTint(ContextCompat.getColor(getApplicationContext(),R.color.brown));
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

            floatingButtonAddNotes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addNotesFragment.OpenBottomSheetDialogAddNotes();
                }
            });


    }

    public void InitListsFragment(){
        lists = new ArrayList<>();
        lists.add(new AddQuoteFragment());
        addNotesFragment = new AddNotesFragment(listNotes,getApplicationContext());
        lists.add(addNotesFragment);

    }

    public void InitListNoteItems(){
        listNotes = new ArrayList<>();
    }

}