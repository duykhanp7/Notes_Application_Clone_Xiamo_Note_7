package com.example.notesapplication.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.notesapplication.R;
import com.example.notesapplication.adapter.QuotesItemAdapter;
import com.example.notesapplication.databinding.BottomSheetAddQuotesLayoutBinding;
import com.example.notesapplication.databinding.FragmentAddQuoteBinding;
import com.example.notesapplication.model.QuoteItem;
import com.example.notesapplication.roomdatabase.DatabaseObject;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class AddQuoteFragment extends Fragment {

    public static FragmentAddQuoteBinding binding;
    QuotesItemAdapter quotesItemAdapter;
    public static ObservableField<QuotesItemAdapter> quotesItemAdapterObservableField;
    public static ObservableField<Integer> sizeItemsQuote;
    public static ObservableField<Boolean> modeDelete;
    List<QuoteItem> listQuotes;

    public AddQuoteFragment(List<QuoteItem> listQuotes) {
        // Required empty public constructor
        this.listQuotes = listQuotes;
        quotesItemAdapter = new QuotesItemAdapter();
        quotesItemAdapterObservableField = new ObservableField<>(quotesItemAdapter);
        modeDelete = new ObservableField<>(false);
        sizeItemsQuote = new ObservableField<>(0);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),R.layout.fragment_add_quote,container,false);

        binding.setAddQuotesFragmentItem(this);
        //INIT ADAPTER
        initItemsInRecyclerView();


        //INIT CLICK FOR SEARCH VIEW
        binding.searchViewFilterAdapter.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                quotesItemAdapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.trim().isEmpty() ){
                    quotesItemAdapter.returnToCurrentQuoteItems();
                }
                else{
                    quotesItemAdapter.getFilter().filter(newText);
                }
                return true;
            }
        });





        return binding.getRoot();
    }

    public void initItemsInRecyclerView(){
        quotesItemAdapter = new QuotesItemAdapter();
        quotesItemAdapter.setQuoteItemList(listQuotes);
        quotesItemAdapterObservableField.set(quotesItemAdapter);
        sizeItemsQuote.set(listQuotes.size());
    }


    public void OpenBottomSheetAddQuotes(){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext(),R.style.CustomBottomSheetDialog);
        BottomSheetAddQuotesLayoutBinding bottomSheetAddQuotesLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(requireContext()), R.layout.bottom_sheet_add_quotes_layout, null, false);
        bottomSheetDialog.setContentView(bottomSheetAddQuotesLayoutBinding.getRoot());
        bottomSheetAddQuotesLayoutBinding.buttonCompleteEditNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textQuotes = Objects.requireNonNull(bottomSheetAddQuotesLayoutBinding.textInputQuotes.getText()).toString();
                if(!textQuotes.trim().isEmpty()){
                    addNewQuotesItem(textQuotes);
                    sizeItemsQuote.set(1);
                    bottomSheetDialog.dismiss();
                }
            }
        });
        bottomSheetDialog.show();
        bottomSheetAddQuotesLayoutBinding.textInputQuotes.requestFocus();
    }


    public void addNewQuotesItem(String textQuotes){
        QuoteItem item = new QuoteItem(textQuotes,getTimeCurrentInString(),false,false);
        quotesItemAdapter.addNewQuotesItem(item);
        DatabaseObject.getInstance(requireContext().getApplicationContext()).roomDatabaseDao().insertQuotesItem(item);
    }

    public void returnToCurrentQuoteItems(){
        quotesItemAdapter.returnToCurrentQuoteItems();
    }

    public void resetStateAllQuoteItems(boolean bool){
        quotesItemAdapter.resetStateTo(bool);
    }

    public static String getTimeCurrentInString(){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("hh:mm");
        return format.format(Calendar.getInstance().getTime());
    }


}