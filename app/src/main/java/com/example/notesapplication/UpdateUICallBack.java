package com.example.notesapplication;

import androidx.recyclerview.widget.DiffUtil;

import java.util.ArrayList;
import java.util.List;

public class UpdateUICallBack extends DiffUtil.Callback {

    List<NoteItem> oldList = new ArrayList<>();
    List<NoteItem> newList = new ArrayList<>();


    public UpdateUICallBack(List<NoteItem> oldList,List<NoteItem> newList){
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        NoteItem oldItem = oldList.get(oldItemPosition);
        NoteItem newItem = newList.get(newItemPosition);
        return oldItem.getId() == newItem.getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        NoteItem oldItem = oldList.get(oldItemPosition);
        NoteItem newItem = newList.get(newItemPosition);
        return oldItem.equals(newItem);
    }
}
