package com.example.notesapplication.main;

import static com.example.notesapplication.adapter.NotesItemAdapter.isShowed;
import static com.example.notesapplication.fragment.AddNotesFragment.isHoveredDelete;
import static com.example.notesapplication.fragment.AddQuoteFragment.modeDelete;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.notesapplication.R;
import com.example.notesapplication.adapter.FragmentNotesAdapter;
import com.example.notesapplication.database.DatabaseSaveNoteItems;
import com.example.notesapplication.fragment.AddNotesFragment;
import com.example.notesapplication.fragment.AddQuoteFragment;
import com.example.notesapplication.model.NoteItem;
import com.example.notesapplication.model.QuoteItem;
import com.example.notesapplication.roomdatabase.DatabaseObject;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class NotesActivityMain extends AppCompatActivity {

    public static TabLayout tabLayoutNote;
    public static FloatingActionButton floatingButtonAddNotes;
    public static ViewPager2 viewPager2;
    private List<Fragment> fragmentList;
    public static List<NoteItem> listNotes;
    public static List<QuoteItem> listQuotes;
    private FragmentNotesAdapter fragmentNotesAdapter;
    DatabaseSaveNoteItems databaseSaveNoteItems;

    @SuppressLint("StaticFieldLeak")
    public static RelativeLayout layoutTopDeleteNote, layoutBottomButtonDelete;
    @SuppressLint("StaticFieldLeak")
    public static ImageView buttonCloseSheetDelete, buttonSelectAllItems;
    @SuppressLint("StaticFieldLeak")
    public static TextView textViewNumberItemsChecked,buttonDeleteNotes;

    AddNotesFragment addNotesFragment;
    AddQuoteFragment addQuoteFragment;

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

            InitListNoteItemsAndQuotesItems();
            InitListsFragment();

            fragmentNotesAdapter = new FragmentNotesAdapter(this, fragmentList);
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
                    if(viewPager2.getCurrentItem() == 0){
                        addQuoteFragment.OpenBottomSheetAddQuotes();
                    }
                    else{
                        addNotesFragment.OpenBottomSheetDialogAddNotes();
                    }
                }
            });

            buttonCloseSheetDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewPager2.setUserInputEnabled(true);
                    int pos = viewPager2.getCurrentItem();
                    if(pos == 0){
                        modeDelete.set(false);
                        addQuoteFragment.resetStateAllQuoteItems(false);
                    }
                    else{
                        isHoveredDelete.set(false);
                        resetStateDeleteAllItems(false,pos);
                        isShowed.set(false);
                        resetStateExpandableOriginal();
                    }
                    showTabLayout(View.VISIBLE);
                    showTopLayoutDelete(View.GONE);
                    showBottomLayoutDelete(View.GONE);
                    showButtonAddNotes(View.VISIBLE);
                }
            });

            buttonSelectAllItems.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int posViewPager = viewPager2.getCurrentItem();
//                    if(posViewPager == 0){
//                        addQuoteFragment.resetStateAllQuoteItems(true);
//                    }
//                    else{
//
//                    }
                    int posCurrentPager = viewPager2.getCurrentItem();
                    if(allItemsIsChecked(posCurrentPager)){
                        Log.i("AAA","ALLL FALSEEEEEEEEEEEE");
                        resetStateDeleteAllItems(false,posCurrentPager);
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
                        resetStateDeleteAllItems(true,posCurrentPager);
                        updateTextCountNumberItemsChecked(posCurrentPager);
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
                    isHoveredDelete.set(false);
                    viewPager2.setUserInputEnabled(true);
                }
            });

    }

    public void InitListsFragment(){
        fragmentList = new ArrayList<>();
        addNotesFragment = new AddNotesFragment(listNotes,getApplicationContext());
        addQuoteFragment = new AddQuoteFragment(listQuotes);
        fragmentList.add(addQuoteFragment);
        fragmentList.add(addNotesFragment);
    }

    public void InitListNoteItemsFromDatabase(){
        listNotes = databaseSaveNoteItems.getListNoteItemsReturn();
    }

    public void InitListQuoteItemsFromDatabase(){
        listQuotes = DatabaseObject.getInstance(this).roomDatabaseDao().getQuoteItems();
    }

    //RETRIEVE DATA FROM DATABASE
    public void InitListNoteItemsAndQuotesItems(){
        listNotes = new ArrayList<>();
        listQuotes = new ArrayList<>();
        databaseSaveNoteItems = new DatabaseSaveNoteItems(getApplicationContext());
        InitListNoteItemsFromDatabase();
        InitListQuoteItemsFromDatabase();
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
        viewPager2.setUserInputEnabled(true);
        int posViewPager = viewPager2.getCurrentItem();
        if(posViewPager == 0){
            modeDelete.set(false);
            addQuoteFragment.returnToCurrentQuoteItems();
            addQuoteFragment.resetStateAllQuoteItems(false);
            if(checkLayoutDeleteIsShow()){
                showTabLayout(View.VISIBLE);
                showTopLayoutDelete(View.GONE);
                showBottomLayoutDelete(View.GONE);
                showButtonAddNotes(View.VISIBLE);
            }
        }
        else{
            if(checkLayoutDeleteIsShow()){
                isHoveredDelete.set(false);
                resetStateDeleteAllItems(false,posViewPager);
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
    }

    public void resetStateDeleteAllItems(boolean state, int pos){

        if(pos == 0){
            for (QuoteItem item : listQuotes){
                //item.setStateCheckedToDelete(state);
                item.setStateChecked(state);
            }
        }
        else{
            for (int i = 0; i < listNotes.size(); i++) {
                listNotes.get(i).setHoveredToDelete(state);
            }
        }
    }

    //CHANGE ALL STATE EXPANDABLE OF ITEMS TO FALSE
    public static void resetStateExpandableAllItems(){
        for (NoteItem item : listNotes){
            item.setTempExpandable(item.getExpandable());
            item.setExpandable(false);
        }
    }

    public void resetStateExpandableOriginal(){
        for (NoteItem item : listNotes){
            item.setExpandable(item.isTempExpandable());
        }
    }

    public boolean allItemsIsChecked(int pos){

        if(pos == 0){
            for (QuoteItem item : listQuotes){
                if(!item.isStateChecked()){
                    return false;
                }
            }
            return true;
        }

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
        List<QuoteItem> listQuoteItemsDelete = new ArrayList<>();
        int posCurrentPager = viewPager2.getCurrentItem();
        if(allItemsIsChecked(posCurrentPager)){
            if(posCurrentPager == 0){
                modeDelete.set(false);
                listQuotes.clear();
                Objects.requireNonNull(AddQuoteFragment.quotesItemAdapterObservableField.get()).clearQuoteItems();
                DatabaseObject.getInstance(this).roomDatabaseDao().deleteAllQuoteItems();
                AddQuoteFragment.binding.icEmptyNotes.post(new Runnable() {
                    @Override
                    public void run() {
                        AddQuoteFragment.binding.icEmptyNotes.setVisibility(View.VISIBLE);
                    }
                });
                AddQuoteFragment.binding.textViewNull.post(new Runnable() {
                    @Override
                    public void run() {
                        AddQuoteFragment.binding.textViewNull.setVisibility(View.VISIBLE);
                    }
                });
            }
            else{
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
        }
        else{
            if(posCurrentPager == 0){
                int size = listQuotes.size();
                for (int i = 0; i < listQuotes.size(); ++i){
                    QuoteItem item = listQuotes.get(i);
                    if(item.isStateChecked()){
                        listQuoteItemsDelete.add(item);
                        DatabaseObject.getInstance(this).roomDatabaseDao().deleteQuoteItem(item);
                    }
                }
                listQuotes.removeAll(listQuoteItemsDelete);
                Objects.requireNonNull(AddQuoteFragment.quotesItemAdapterObservableField.get()).removeQuoteItems(listQuoteItemsDelete);
                modeDelete.set(false);
                for (QuoteItem item : listQuotes){
                    item.setStateCheckedToDelete(false);
                    item.setStateChecked(false);
                }
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
    }

    public static int countItemsChecked(int pos){
        int i = 0;
        if(pos == 0){
            for (QuoteItem item : listQuotes){
                if(item.isStateChecked()){
                    i++;
                }
            }
        }
        else{
            for (NoteItem item : listNotes){
                if(item.isHoveredToDelete()){
                    i++;
                }
            }
        }
        return  i;
    }

    public static void updateTextCountNumberItemsChecked(int pos){
        textViewNumberItemsChecked.post(new Runnable() {
            @Override
            public void run() {
                int count = countItemsChecked(pos);
                if(count != 0){
                    disableClickedForButtonDelete(false);
                }
                else {
                    disableClickedForButtonDelete(true);
                }
                textViewNumberItemsChecked.setText(count > 0 ? "Đã chọn "+String.valueOf(countItemsChecked(pos))+" mục" : "Chọn mục");
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
        if(fragmentList.size() != 0){
            for (NoteItem item : listNotes){
                if(isShowed.get()){
                    item.setExpandable(item.isTempExpandable());
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