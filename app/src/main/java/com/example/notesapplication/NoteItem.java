package com.example.notesapplication;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notesapplication.databinding.CustomItemNotesBinding;
import com.example.notesapplication.databinding.CustomLayoutBottomAddNotesBinding;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class NoteItem extends BaseObservable implements Parcelable {

    public ObservableField<ChildrenNotesItemBottomSheetAdapter> notesItemBottomSheetAdapterObservableField = new ObservableField<>();
    public ObservableField<ChildrenNotesItemAdapter> noteItemChildrenAdapter = new ObservableField<>();
    public CustomItemNotesBinding customItemNotesBinding;

    int id = 0;
    String title="";
    List<ChildrenNoteItem> listNotes;
    String timeNotifyNote="";
    String textTimeNotifyNull="Đặt báo thức";
    boolean isExpandable;
    boolean isChecked;
    String textIfTitleIsNull = "Danh sách việc cần làm";
    String numberItemCheck = "0";
    Date dateNotify = null;
    String timeNotify = "";
    boolean isOverTime;
    boolean isHoveredToDelete = false;
    boolean tempExpandable = false;




    public NoteItem(int id, List<ChildrenNoteItem> list, String title, String timeNotifyNote, boolean isExpandable) {
        this.id = id;
        this.title = title;
        this.listNotes = list;
        this.timeNotifyNote = timeNotifyNote;
        this.isExpandable = isExpandable;
        this.isChecked = false;
        this.numberItemCheck = countNumberChildrenItemChecked(listNotes);
        this.isOverTime = false;
        isHoveredToDelete = false;
        notesItemBottomSheetAdapterObservableField.set(new ChildrenNotesItemBottomSheetAdapter(this.listNotes,this));
    }

    protected NoteItem(Parcel in) {
        id = in.readInt();
        title = in.readString();
        timeNotifyNote = in.readString();
        textTimeNotifyNull = in.readString();
        isExpandable = in.readByte() != 0;
        isChecked = in.readByte() != 0;
        textIfTitleIsNull = in.readString();
        numberItemCheck = in.readString();
        timeNotify = in.readString();
    }

    public static final Creator<NoteItem> CREATOR = new Creator<NoteItem>() {
        @Override
        public NoteItem createFromParcel(Parcel in) {
            return new NoteItem(in);
        }

        @Override
        public NoteItem[] newArray(int size) {
            return new NoteItem[size];
        }
    };

    @Bindable
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        this.notifyPropertyChanged(BR.id);
    }

    @Bindable
    public List<ChildrenNoteItem> getListNotes() {
        return listNotes;
    }


    public void setListNotes(List<ChildrenNoteItem> lists) {
        this.listNotes = lists;
        this.notifyPropertyChanged(BR.listNotes);
    }


    @Bindable
    public String getTimeNotifyNote() {
        return timeNotifyNote;
    }


    public void setTimeNotifyNote(String timeNotifyNote) {
        this.timeNotifyNote = timeNotifyNote;
        notifyPropertyChanged(BR.timeNotifyNote);
    }

    @Bindable
    public String getTextTimeNotifyNull() {
        return textTimeNotifyNull;
    }

    public void setTextTimeNotifyNull(String textTimeNotifyNull) {
        this.textTimeNotifyNull = textTimeNotifyNull;
        notifyPropertyChanged(BR.textTimeNotifyNull);
    }

    @Bindable
    public boolean getExpandable() {
        return isExpandable;
    }

    public void setExpandable(boolean expandable) {
        isExpandable = expandable;
        notifyPropertyChanged(BR.expandable);
    }

    @Bindable
    public String getTitle() {
        return title;
    }

    public void setTitle(String textNote) {
        this.title = textNote;
        this.notifyPropertyChanged(BR.title);
    }

    @Bindable
    public boolean getChecked() {
        return isChecked;
    }


    public void setChecked(boolean checked) {
        isChecked = checked;
        this.notifyPropertyChanged(BR.checked);
    }

    public Date getDateNotify() {
        return dateNotify;
    }

    public void setDateNotify(Date dateNotify) {
        this.dateNotify = dateNotify;
    }

    @Bindable
    public String getTimeNotify() {
        return timeNotify;
    }

    public void setTimeNotify(String timeNotify) {
        this.timeNotify = timeNotify;
        this.notifyPropertyChanged(BR.timeNotify);
    }

    @Bindable
    public String getNumberItemCheck() {
        return numberItemCheck;
    }

    public void setNumberItemCheck(String numberItemCheck) {
        this.numberItemCheck = numberItemCheck;
        this.notifyPropertyChanged(BR.numberItemCheck);
    }

    @Bindable
    public String getTextIfTitleIsNull() {
        return textIfTitleIsNull;
    }

    public void setTextIfTitleIsNull(String textIfTitleIsNull) {
        this.textIfTitleIsNull = textIfTitleIsNull;
        this.notifyPropertyChanged(BR.textIfTitleIsNull);
    }

    @Bindable
    public boolean isOverTime() {
        return isOverTime;
    }

    public void setOverTime(boolean overTime) {
        isOverTime = overTime;
        this.notifyPropertyChanged(BR.overTime);
    }

    @Bindable
    public boolean isHoveredToDelete() {
        return isHoveredToDelete;
    }

    public void setHoveredToDelete(boolean hoveredToDelete) {
        isHoveredToDelete = hoveredToDelete;
        this.notifyPropertyChanged(BR.hoveredToDelete);
    }


    public boolean isTempExpandable() {
        return tempExpandable;
    }

    public void setTempExpandable(boolean tempExpandable) {
        this.tempExpandable = tempExpandable;
    }

    public String countNumberChildrenItemChecked(List<ChildrenNoteItem> listNotes){
        int a = 0;
        for (ChildrenNoteItem item : listNotes){
            if(item.isChecked()){
                a++;
            }
        }
        return String.valueOf(a);
    }
    public ObservableField<ChildrenNotesItemBottomSheetAdapter> getNotesItemBottomSheetAdapterObservableField() {
        return notesItemBottomSheetAdapterObservableField;
    }

    public void setNotesItemBottomSheetAdapterObservableField(ObservableField<ChildrenNotesItemBottomSheetAdapter> notesItemBottomSheetAdapterObservableField) {
        this.notesItemBottomSheetAdapterObservableField = notesItemBottomSheetAdapterObservableField;
    }

    public ObservableField<ChildrenNotesItemAdapter> getNoteItemChildrenAdapter() {
        return noteItemChildrenAdapter;
    }

    public void setNoteItemChildrenAdapter(ObservableField<ChildrenNotesItemAdapter> noteItemChildrenAdapter) {
        this.noteItemChildrenAdapter = noteItemChildrenAdapter;
    }


    public CustomItemNotesBinding getCustomItemNotesBinding() {
        return customItemNotesBinding;
    }

    public void setCustomItemNotesBinding(CustomItemNotesBinding customItemNotesBinding) {
        this.customItemNotesBinding = customItemNotesBinding;
    }


    @Override
    public boolean equals(Object o) {
        NoteItem noteItem = (NoteItem) o;
        return isExpandable == noteItem.isExpandable && isChecked == noteItem.isChecked && Objects.equals(title, noteItem.title) && Objects.equals(listNotes, noteItem.listNotes) && Objects.equals(timeNotifyNote, noteItem.timeNotifyNote) && Objects.equals(numberItemCheck, noteItem.numberItemCheck);
    }

    @Override
    public int hashCode() {
        return Objects.hash(notesItemBottomSheetAdapterObservableField, id, title, listNotes, timeNotifyNote, textTimeNotifyNull, isExpandable, isChecked, textIfTitleIsNull, numberItemCheck);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(timeNotifyNote);
        parcel.writeString(textTimeNotifyNull);
        parcel.writeByte((byte) (isExpandable ? 1 : 0));
        parcel.writeByte((byte) (isChecked ? 1 : 0));
        parcel.writeString(textIfTitleIsNull);
        parcel.writeString(numberItemCheck);
        parcel.writeString(timeNotify);
    }
}
