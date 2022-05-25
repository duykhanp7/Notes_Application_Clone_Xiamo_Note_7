package com.example.notesapplication.bindingUtils;

import static com.example.notesapplication.adapter.NotesItemAdapter.valueResources;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.databinding.Bindable;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notesapplication.adapter.ChildrenNotesItemAdapter;
import com.example.notesapplication.adapter.QuotesItemAdapter;
import com.example.notesapplication.bottomSheet.ChildrenNotesItemBottomSheetAdapter;
import com.example.notesapplication.model.NoteItem;
import com.example.notesapplication.adapter.NotesItemAdapter;
import com.example.notesapplication.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.List;


public class BindingUtils {



    @BindingAdapter({"setAdapter"})
    public static void setAdapter(RecyclerView view, NotesItemAdapter adapter) {
        view.setLayoutManager(new LinearLayoutManager(view.getContext()));
        view.setHasFixedSize(true);
        view.setAdapter(adapter);
    }

    @BindingAdapter({"setChildrenAdapter"})
    public static void setChildrenAdapter(RecyclerView view, NoteItem noteItem){
        ChildrenNotesItemAdapter adapter = new ChildrenNotesItemAdapter(noteItem);
        noteItem.noteItemChildrenAdapter.set(adapter);
        view.setLayoutManager(new LinearLayoutManager(view.getContext()));
        view.setHasFixedSize(true);
        view.setAdapter(adapter);
    }

    @BindingAdapter({"changePaddingWhenStateDeleteChanged"})
    public static void paddingTopCustom(RecyclerView recyclerView,boolean bool){
        if(bool){
            recyclerView.setPadding(0,100,0,0);
        }
        else{
            recyclerView.setPadding(0,0,0,0);
        }
    }

    @BindingAdapter({"setChildrenBottomSheetAdapter"})
    public static void setChildrenAdapter(RecyclerView view, ChildrenNotesItemBottomSheetAdapter adapter){
        LinearLayoutManager manager = new LinearLayoutManager(view.getContext());
        manager.setStackFromEnd(false);
        view.setLayoutManager(manager);
        view.setHasFixedSize(true);
        view.setAdapter(adapter);
    }


    @BindingAdapter({"changePadding"})
    public static void changePadding(RecyclerView view,NoteItem noteItem){
        if(noteItem.getTitle().isEmpty() && noteItem.getListNotes().size() == 1){
            view.setPadding(0,0,0,0);
        }
        else{
            view.setPadding(80,0,0,0);
        }
    }


    @BindingAdapter({"changeFontStyle","titleItem"})
    public static void changeFontStyle(TextInputEditText editText,int size,String title){
        if(size == 1 && title==""){
            editText.setTypeface(Typeface.MONOSPACE,Typeface.BOLD);
        }
        else{
            editText.setTypeface(Typeface.MONOSPACE,Typeface.NORMAL);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @BindingAdapter({"strikeThrough"})
    public static void setStrikeThrough(TextView textView,boolean bool){
        if(bool){
            textView.setForeground(textView.getContext().getDrawable(R.drawable.custom_strike_through));
        }
        else{
            textView.setForeground(null);
        }
    }


    @BindingAdapter({"textColorChangeByChecked"})
    public static void setTextColorChanged(TextView editText,boolean check){
        if(check){
            editText.setTextColor(ContextCompat.getColor(editText.getContext(),R.color.brown));
        }
        else{
            editText.setTextColor(ContextCompat.getColor(editText.getContext(),R.color.black));
        }
    }


    @BindingAdapter({"editTextStrikeThrough"})
    public static void setTextStrikeThroughForEditText(TextInputEditText editText,boolean bool){
        if(bool){
            editText.setPaintFlags(editText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else{
            editText.setPaintFlags(editText.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    @BindingAdapter({"displayValues","initValues"})
    public static void setDisplayValuesForNumberPicker(NumberPicker numberPicker, List<String> list,NoteItem noteItem){
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(list.size()-1);
        numberPicker.setDisplayedValues(list.toArray(new String[]{}));
        int index = 0;
        if(noteItem.timeNotify.isEmpty()){
            Calendar calendar = Calendar.getInstance();
            String currentStringDate = valueResources.convertDateToStringFormatStandard(calendar.getTime());
            index = valueResources.dateFormatStandardList.indexOf(currentStringDate);
        }
        else{
            String currentStringDate = valueResources.convertDateToStringFormatStandard(noteItem.getDateNotify());
            index = valueResources.dateFormatStandardList.indexOf(currentStringDate);
        }
        numberPicker.setValue(index);
    }


    @BindingAdapter({"displayValuesHour","initValues"})
    public static void setDisplayValuesForNumberPickerHour(NumberPicker numberPicker,String[] strHours,NoteItem noteItem){
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(strHours.length-1);
        numberPicker.setDisplayedValues(strHours);
        if(noteItem.timeNotify.isEmpty()){
            Calendar calendar = Calendar.getInstance();
            numberPicker.setValue(calendar.get(Calendar.HOUR_OF_DAY));
        }
        else{
            numberPicker.setValue(Integer.parseInt(noteItem.getTimeNotify().split(":")[0]));
        }
    }

    @BindingAdapter({"displayValuesMinutes","initValues"})
    public static void setDisplayValuesForNumberPickerMinutes(NumberPicker numberPicker,String[] strMinutes,NoteItem noteItem){
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(strMinutes.length-1);
        numberPicker.setDisplayedValues(strMinutes);
        if(noteItem.timeNotify.isEmpty()){
            Calendar calendar = Calendar.getInstance();
            numberPicker.setValue(calendar.get(Calendar.MINUTE));
        }
        else{
            numberPicker.setValue(Integer.parseInt(noteItem.getTimeNotify().split(":")[1]));
        }
    }

    @BindingAdapter("quotesAdapter")
    public static void setQuotesAdapter(RecyclerView recyclerView, QuotesItemAdapter adapter){
        LinearLayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

}
