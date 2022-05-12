package com.example.notesapplication;

import static com.example.notesapplication.NotesItemAdapter.isShowed;

import androidx.annotation.ColorInt;
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
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
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

    public static TabLayout tabLayoutNote;
    public static FloatingActionButton floatingButtonAddNotes;
    private ViewPager2 viewPager2;
    private List<Fragment> lists;
    public static List<NoteItem> listNotes;
    private FragmentNotesAdapter fragmentNotesAdapter;
    DatabaseSaveNoteItems databaseSaveNoteItems;

    @SuppressLint("StaticFieldLeak")
    public static RelativeLayout layoutTopDeleteNote, layoutBottomButtonDelete;
    @SuppressLint("StaticFieldLeak")
    public static ImageView buttonCloseSheetDelete, buttonSelectAllItems;
    @SuppressLint("StaticFieldLeak")
    public static TextView textViewNumberItemsChecked,buttonDeleteNotes;

    AddNotesFragment addNotesFragment;

    public static FragmentManager fragmentManager;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.notes_activity_main);
            //HOOK VIEWS
            fragmentManager = getSupportFragmentManager();
            tabLayoutNote = findViewById(R.id.tabLayoutNotes);
            floatingButtonAddNotes = findViewById(R.id.floatingButtonAddNote);
            viewPager2 = findViewById(R.id.viewPagerNotes);

            layoutTopDeleteNote = findViewById(R.id.layoutTopDeleteNote);
            layoutBottomButtonDelete = findViewById(R.id.layoutBottomButtonDelete);
            buttonCloseSheetDelete = findViewById(R.id.buttonCloseSheetDelete);
            buttonSelectAllItems = findViewById(R.id.buttonSelectAllItems);
            textViewNumberItemsChecked = findViewById(R.id.textViewNumberItemsChecked);
            buttonDeleteNotes = findViewById(R.id.buttonDeleteNotes);

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

            buttonCloseSheetDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    resetStateDeleteAllItems(false);
                    isShowed.set(false);
                    showTabLayout(View.VISIBLE);
                    showTopLayoutDelete(View.GONE);
                    showBottomLayoutDelete(View.GONE);
                    showButtonAddNotes(View.VISIBLE);
                    resetStateExpandableOriginal();
                }
            });

            buttonSelectAllItems.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(allItemsIsChecked()){
                        Log.i("AAA","ALLL FALSEEEEEEEEEEEE");
                        resetStateDeleteAllItems(false);
                        textViewNumberItemsChecked.post(new Runnable() {
                            @Override
                            public void run() {
                                textViewNumberItemsChecked.setText("Chọn mục");
                            }
                        });
                        disableClickedForButtonDelete(true);
                    }
                    else{
                        Log.i("AAA","TRUEEEEEEEEEEEEEEEEE");
                        resetStateDeleteAllItems(true);
                        updateTextCountNumberItemsChecked();
                    }
                }
            });

            buttonDeleteNotes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteItems();
                    resetStateExpandableOriginal();
                    isShowed.set(false);
                    showTopLayoutDelete(View.GONE);
                    showBottomLayoutDelete(View.GONE);
                    showTabLayout(View.VISIBLE);
                    showButtonAddNotes(View.VISIBLE);
                }
            });

    }

    public void InitListsFragment(){
        lists = new ArrayList<>();
        lists.add(new AddQuoteFragment());
        addNotesFragment = new AddNotesFragment(listNotes,getApplicationContext());
        lists.add(addNotesFragment);
    }

    public void InitListNoteItemsFromDatabase(){
        listNotes = databaseSaveNoteItems.getListNoteItemsReturn();
        for (NoteItem item :listNotes){
            Log.i("AAA","NOTE ITEM : "+item.getId()+"-"+item.getTitle()+"-"
                    +item.getDateNotify()+"-"+item.getTimeNotifyNote()+"-"+
                    item.getTimeNotify());
        }
    }

    public void InitListNoteItems(){
        listNotes = new ArrayList<>();
        databaseSaveNoteItems = new DatabaseSaveNoteItems(getApplicationContext());
        InitListNoteItemsFromDatabase();
    }

    public static void showTopLayoutDelete(int visibility){
        layoutTopDeleteNote.post(new Runnable() {
            @Override
            public void run() {
                layoutTopDeleteNote.setVisibility(visibility);
            }
        });
    }

    public static void showBottomLayoutDelete(int visibility){
        layoutBottomButtonDelete.post(new Runnable() {
            @Override
            public void run() {
                layoutBottomButtonDelete.setVisibility(visibility);
            }
        });
    }

    public static void showTabLayout(int visibility){
        tabLayoutNote.post(new Runnable() {
            @Override
            public void run() {
                tabLayoutNote.setVisibility(visibility);
            }
        });
    }

    public static void showButtonAddNotes(int visibility){
        floatingButtonAddNotes.post(new Runnable() {
            @Override
            public void run() {
                floatingButtonAddNotes.setVisibility(visibility);
            }
        });
    }

    public static void disableClickedForButtonDelete(boolean disableState){
        buttonDeleteNotes.post(new Runnable() {
            @SuppressLint("UseCompatTextViewDrawableApis")
            @Override
            public void run() {
                buttonDeleteNotes.setClickable(!disableState);
                buttonDeleteNotes.setTextColor(disableState ? Color.argb(100,152,152,152) : Color.BLACK);
                buttonDeleteNotes.setCompoundDrawableTintList(ColorStateList.valueOf(disableState ? Color.argb(100,152,152,152) : Color.BLACK));
            }
        });
    }

    public boolean checkLayoutDeleteIsShow(){
        return layoutTopDeleteNote.getVisibility() == View.VISIBLE;
    }

    @Override
    public void onBackPressed() {
        if(checkLayoutDeleteIsShow()){
            resetStateDeleteAllItems(false);
            showTabLayout(View.VISIBLE);
            showTopLayoutDelete(View.GONE);
            showBottomLayoutDelete(View.GONE);
            showButtonAddNotes(View.VISIBLE);
            isShowed.set(false);

            //////////RESET STATE OF NOTE ITEMS BACK TO ORIGINALS
            resetStateExpandableOriginal();
        }
        else{
            super.onBackPressed();
        }
    }

    public void resetStateDeleteAllItems(boolean state){
        for (int i = 0; i < listNotes.size(); i++) {
            listNotes.get(i).setHoveredToDelete(state);
        }
    }

    //CHANGE ALL STATE EXPANDABLE OF ITEMS TO FALSE
    public static void resetStateExpandableAllItems(){
        for (NoteItem item : listNotes){
            item.setTempExpandable(item.getExpandable() ? true : false);
            item.setExpandable(false);
        }
    }

    public void resetStateExpandableOriginal(){
        for (NoteItem item : listNotes){
            item.setExpandable(item.isTempExpandable() ? true : false);
        }
    }

    public boolean allItemsIsChecked(){
        for (NoteItem item :listNotes){
            if(!item.isHoveredToDelete()){
                return false;
            }
        }
        return true;
    }


    @SuppressLint("NotifyDataSetChanged")
    public void deleteItems(){
        List<NoteItem> listItemsDelete = new ArrayList<>();

        if(allItemsIsChecked()){
            listNotes.clear();
            databaseSaveNoteItems.deleteAllItems();
            Objects.requireNonNull(AddNotesFragment.listsObservable.get()).notifyDataSetChanged();
            AddNotesFragment.fragmentAddNotesBinding.icEmptyNotes.post(new Runnable() {
                @Override
                public void run() {
                    AddNotesFragment.fragmentAddNotesBinding.icEmptyNotes.setVisibility(View.VISIBLE);
                }
            });
            AddNotesFragment.fragmentAddNotesBinding.textViewNull.post(new Runnable() {
                @Override
                public void run() {
                    AddNotesFragment.fragmentAddNotesBinding.textViewNull.setVisibility(View.VISIBLE);
                }
            });
        }
        else{
            int size = listNotes.size();
            for (int i = 0; i < listNotes.size(); ++i){
                NoteItem item = listNotes.get(i);
                if(item.isHoveredToDelete()){
                    listItemsDelete.add(item);
                    databaseSaveNoteItems.deleteNoteItem(item.getId());
                }
            }
            listNotes.removeAll(listItemsDelete);
            Objects.requireNonNull(AddNotesFragment.listsObservable.get()).notifyItemRangeRemoved(0,size);
        }
    }

    public static int countItemsChecked(){
        int i = 0;
        for (NoteItem item : listNotes){
            if(item.isHoveredToDelete()){
                i++;
            }
        }
        return  i;
    }

    public static void updateTextCountNumberItemsChecked(){
        textViewNumberItemsChecked.post(new Runnable() {
            @Override
            public void run() {
                int count = countItemsChecked();
                if(count != 0){
                    disableClickedForButtonDelete(false);
                }
                else {
                    disableClickedForButtonDelete(true);
                }
                textViewNumberItemsChecked.setText(count > 0 ? "Đã chọn "+String.valueOf(countItemsChecked())+" mục" : "Chọn mục");
            }
        });
    }


    //ERROR WITH NOTE ITEM WITH ONE CHILDREN ITEM WHEN ON LONG CLICK
    //AND EXPANDABLE FALSE LOST VIEW//ERROR WITH NOTE ITEM WITH ONE CHILDREN ITEM WHEN ON LONG CLICK
    //AND EXPANDABLE FALSE LOST VIEW//ERROR WITH NOTE ITEM WITH ONE CHILDREN ITEM WHEN ON LONG CLICK
    //AND EXPANDABLE FALSE LOST VIEW//ERROR WITH NOTE ITEM WITH ONE CHILDREN ITEM WHEN ON LONG CLICK
    //AND EXPANDABLE FALSE LOST VIEW//ERROR WITH NOTE ITEM WITH ONE CHILDREN ITEM WHEN ON LONG CLICK
    //AND EXPANDABLE FALSE LOST VIEW

    public void saveTheLastStateOfListNoteItemsToDatabase(){
        if(lists.size() != 0){
            for (NoteItem item : listNotes){
                if(isShowed.get()){
                    item.setExpandable(item.isTempExpandable() ? true : false);
                }
                databaseSaveNoteItems.updateNoteItemEnd(item);
            }
        }
    }

    //SAVE THE LAST STATE OF NOTE ITEMS TO DATABASE WHEN FRAGMENT PAUSED
    @Override
    protected void onPause() {
        super.onPause();
        Log.i("AAA","ON PAUSEEEEEEEEEEEEEEEEEEE");
        saveTheLastStateOfListNoteItemsToDatabase();
    }
}