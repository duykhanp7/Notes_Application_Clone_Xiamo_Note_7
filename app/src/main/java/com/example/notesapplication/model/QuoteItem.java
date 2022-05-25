package com.example.notesapplication.model;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.example.notesapplication.BR;
import com.example.notesapplication.utils.Utils;


@Entity(tableName = Utils.TABLE_NAME)
public class QuoteItem extends BaseObservable implements Parcelable {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "QUOTES")
    String quotes;
    @ColumnInfo(name = "TIME")
    String timeSetQuotes;
    @ColumnInfo(name = "CHECK")
    boolean stateChecked;
    @ColumnInfo(name = "DELETE")
    boolean stateCheckedToDelete;

    public QuoteItem(){}

    public QuoteItem(@NonNull String quotes, String timeSetQuotes, boolean stateChecked, boolean stateCheckedToDelete) {
        this.quotes = quotes;
        this.timeSetQuotes = timeSetQuotes;
        this.stateChecked = stateChecked;
        this.stateCheckedToDelete = stateCheckedToDelete;
    }

    @NonNull
    @Bindable
    public String getQuotes() {
        return quotes;
    }

    public void setQuotes(String quotes) {
        this.quotes = quotes;
        this.notifyPropertyChanged(BR.quotes);
    }

    @Bindable
    public String getTimeSetQuotes() {
        return timeSetQuotes;
    }

    public void setTimeSetQuotes(String timeSetQuotes) {
        this.timeSetQuotes = timeSetQuotes;
        this.notifyPropertyChanged(BR.timeSetQuotes);
    }

    @Bindable
    public boolean isStateChecked() {
        return stateChecked;
    }

    public void setStateChecked(boolean stateChecked) {
        this.stateChecked = stateChecked;
        this.notifyPropertyChanged(BR.stateChecked);
    }

    @Bindable
    public boolean isStateCheckedToDelete() {
        return stateCheckedToDelete;
    }

    public void setStateCheckedToDelete(boolean stateCheckedToDelete) {
        this.stateCheckedToDelete = stateCheckedToDelete;
        this.notifyPropertyChanged(BR.stateCheckedToDelete);
    }

    public static Creator<QuoteItem> getCREATOR() {
        return CREATOR;
    }

    protected QuoteItem(Parcel in) {
        quotes = in.readString();
        timeSetQuotes = in.readString();
        stateChecked = in.readByte() != 0;
        stateCheckedToDelete = in.readByte() != 0;
    }

    public static final Creator<QuoteItem> CREATOR = new Creator<QuoteItem>() {
        @Override
        public QuoteItem createFromParcel(Parcel in) {
            return new QuoteItem(in);
        }

        @Override
        public QuoteItem[] newArray(int size) {
            return new QuoteItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(quotes);
        parcel.writeString(timeSetQuotes);
        parcel.writeByte((byte) (stateChecked ? 1 : 0));
        parcel.writeByte((byte) (stateCheckedToDelete ? 1 : 0));
    }
}
