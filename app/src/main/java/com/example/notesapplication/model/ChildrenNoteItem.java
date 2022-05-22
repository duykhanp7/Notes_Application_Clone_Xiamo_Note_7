package com.example.notesapplication.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.notesapplication.BR;

import java.util.Objects;

public class ChildrenNoteItem extends BaseObservable{


    int idParent;
    int idChildren = 0;
    String text;
    boolean isChecked = false;


    public ChildrenNoteItem(int idChildren,String text, boolean isChecked) {
        this.idChildren = idChildren;
        this.text = text;
        this.isChecked = isChecked;
    }


    @Bindable
    public int getIdChildren() {
        return idChildren;
    }

    public void setIdChildren(int idChildren) {
        this.idChildren = idChildren;
        this.notifyPropertyChanged(BR.idChildren);
    }

    @Bindable
    public int getIdParent() {
        return idParent;
    }

    public void setIdParent(int idParent) {
        this.idParent = idParent;
        this.notifyPropertyChanged(BR.idParent);
    }


    @Bindable
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        this.notifyPropertyChanged(BR.text);
    }


    @Bindable
    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
        this.notifyPropertyChanged(BR.checked);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChildrenNoteItem item = (ChildrenNoteItem) o;
        return idChildren == item.idChildren && isChecked == item.isChecked && Objects.equals(text, item.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getText(), isChecked());
    }

    public void appendText(String text){
        this.text+=text;
        this.notifyPropertyChanged(BR.text);
    }
}
