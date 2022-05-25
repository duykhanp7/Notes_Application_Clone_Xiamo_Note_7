package com.example.notesapplication.adapter;

import static com.example.notesapplication.fragment.AddNotesFragment.isHoveredDelete;
import static com.example.notesapplication.fragment.AddQuoteFragment.modeDelete;
import static com.example.notesapplication.main.NotesActivityMain.listQuotes;
import static com.example.notesapplication.main.NotesActivityMain.viewPager2;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notesapplication.R;
import com.example.notesapplication.databinding.BottomSheetAddQuotesLayoutBinding;
import com.example.notesapplication.databinding.QuotesItemLayoutBinding;
import com.example.notesapplication.fragment.AddQuoteFragment;
import com.example.notesapplication.main.NotesActivityMain;
import com.example.notesapplication.model.QuoteItem;
import com.example.notesapplication.roomdatabase.DatabaseObject;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class QuotesItemAdapter extends RecyclerView.Adapter<QuotesItemAdapter.ViewHolder> implements Filterable {

    public  List<QuoteItem> quoteItemList;
    public  List<QuoteItem> quoteItemListOld;

    public QuotesItemAdapter(){
        this.quoteItemList = new ArrayList<>();
        this.quoteItemListOld = new ArrayList<>();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setQuoteItemList(List<QuoteItem> quoteItemList){
        this.quoteItemList = new ArrayList<>(quoteItemList);
        this.quoteItemListOld = new ArrayList<>(quoteItemList);
        notifyDataSetChanged();
    }

    public void addNewQuotesItem(QuoteItem item){
        int pos = quoteItemList.size();
        quoteItemList.add(pos,item);
        quoteItemListOld.add(pos,item);
        listQuotes.add(pos,item);
        notifyItemInserted(pos);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clearQuoteItems(){
        quoteItemList.clear();
        quoteItemListOld.clear();
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void removeQuoteItems(List<QuoteItem> items){
        quoteItemList.removeAll(items);
        quoteItemListOld.removeAll(items);
        notifyDataSetChanged();
        for (QuoteItem item : quoteItemList){
            item.setStateCheckedToDelete(false);
            item.setStateChecked(false);
        }
        for (QuoteItem item : quoteItemListOld){
            item.setStateCheckedToDelete(false);
            item.setStateChecked(false);
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    public void returnToCurrentQuoteItems(){
        quoteItemList = new ArrayList<>(quoteItemListOld);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        QuotesItemLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.quotes_item_layout,parent,false);
        return new ViewHolder(binding, quoteItemList);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuoteItem item = quoteItemList.get(position);
        holder.binding.setQuotes(item);
        holder.setQuoteItem(item);
    }

    @Override
    public int getItemCount() {
        return quoteItemList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String key = charSequence.toString().trim().toLowerCase();
                List<QuoteItem> filterItemsQuote = new ArrayList<>();
                if(!key.trim().isEmpty()){
                    for (QuoteItem item : quoteItemListOld){
                        if(item.getQuotes().toLowerCase().contains(key)){
                            filterItemsQuote.add(item);
                        }
                    }
                    quoteItemList = filterItemsQuote;
                }
                else{
                    quoteItemList = quoteItemListOld;
                }

                FilterResults results = new FilterResults();
                results.values = filterItemsQuote;

                return results;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                quoteItemList = (List<QuoteItem>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    //SET STATE CHECKED OR NOT FOR ALL ITEMS QUOTE
    public void resetStateTo(boolean bool){
        for (QuoteItem item : quoteItemList){
            item.setStateCheckedToDelete(bool);
            item.setStateChecked(bool);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{
        QuoteItem quoteItem;
        QuotesItemLayoutBinding binding;
        List<QuoteItem> quoteItemList;
        public ViewHolder(@NonNull QuotesItemLayoutBinding binding, List<QuoteItem> quoteItemList) {
            super(binding.getRoot());
            this.binding = binding;
            this.quoteItemList = quoteItemList;
            binding.getRoot().setOnClickListener(this);
            binding.getRoot().setOnLongClickListener(this);

            binding.radioButtonCheckDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    quoteItem.setStateChecked(!quoteItem.isStateChecked());
                }
            });
        }

        public void setQuoteItem(QuoteItem item){
            this.quoteItem = item;
        }

        //ON CLICK
        @Override
        public void onClick(View view) {
            if(modeDelete.get()){
                quoteItem.setStateChecked(!quoteItem.isStateChecked());
            }
            else{
                BottomSheetDialog bottomSheetDialogUpdateQuote = new BottomSheetDialog(view.getContext(),R.style.CustomBottomSheetDialog);
                BottomSheetAddQuotesLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(view.getContext()),R.layout.bottom_sheet_add_quotes_layout,null,false);
                bottomSheetDialogUpdateQuote.setContentView(binding.getRoot());
                binding.textInputQuotes.requestFocus();
                binding.textInputQuotes.setText(quoteItem.getQuotes());

                binding.buttonCompleteEditNote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        String oldText = quoteItem.getQuotes();
//                        quoteItem.setQuotes(Objects.requireNonNull(binding.textInputQuotes.getText()).toString().trim());
//                        quoteItem.setTimeSetQuotes(AddQuoteFragment.getTimeCurrentInString());
//
//                        DatabaseObject.getInstance(view.getContext()).roomDatabaseDao().updateQuoteItemByField(oldText, quoteItem.getQuotes(),quoteItem.getTimeSetQuotes());
                        QuoteItem item = listQuotes.get(getAdapterPosition());
                        item.setQuotes(Objects.requireNonNull(binding.textInputQuotes.getText()).toString().trim());
                        item.setTimeSetQuotes(AddQuoteFragment.getTimeCurrentInString());
                        DatabaseObject.getInstance(view.getContext()).roomDatabaseDao().updateQuoteItem(item);
                        bottomSheetDialogUpdateQuote.dismiss();
                    }
                });

                bottomSheetDialogUpdateQuote.show();
            }
        }

        //ON LONG CLICK
        @Override
        public boolean onLongClick(View view) {
            viewPager2.setUserInputEnabled(false);
            modeDelete.set(true);
            resetStateTo(true);
            quoteItem.setStateCheckedToDelete(true);
            quoteItem.setStateChecked(true);

            NotesActivityMain.showTabLayout(View.GONE);
            NotesActivityMain.showTopLayoutDelete(View.VISIBLE);
            NotesActivityMain.showBottomLayoutDelete(View.VISIBLE);
            NotesActivityMain.showButtonAddNotes(View.GONE);
            NotesActivityMain.resetStateExpandableAllItems();

            return true;
        }

        public void resetStateTo(boolean bool){
            for (QuoteItem item : quoteItemList){
                item.setStateCheckedToDelete(bool);
            }
        }

    }
}
