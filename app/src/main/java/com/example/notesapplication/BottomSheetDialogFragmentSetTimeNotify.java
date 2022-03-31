package com.example.notesapplication;

import static com.example.notesapplication.NotesItemAdapter.valueResources;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import com.example.notesapplication.databinding.CustomBottomSheetDateTimePickerBinding;
import com.example.notesapplication.databinding.CustomLayoutBottomAddNotesBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class BottomSheetDialogFragmentSetTimeNotify extends BottomSheetDialogFragment {

    ValueResources valueResources;
    Context context;
    NoteItem noteItem;
    CustomBottomSheetDateTimePickerBinding customBottomSheetDateTimePickerBinding;
    CustomLayoutBottomAddNotesBinding customLayoutBottomAddNotesBinding;
    String textNotifyReturn = "";
    public BottomSheetDialogFragmentSetTimeNotify(ValueResources valueResources,Context context,NoteItem noteItem,CustomLayoutBottomAddNotesBinding customLayoutBottomAddNotesBinding){
        this.valueResources = valueResources;
        this.context = context;
        this.noteItem = noteItem;
        this.customLayoutBottomAddNotesBinding = customLayoutBottomAddNotesBinding;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialogChip = new BottomSheetDialog(context,R.style.CustomBottomSheetDialog);
        customBottomSheetDateTimePickerBinding
                = DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.custom_bottom_sheet_date_time_picker,null,false);

        customBottomSheetDateTimePickerBinding.setValuesResources(valueResources);
        customBottomSheetDateTimePickerBinding.setNoteItem(noteItem);

        if(noteItem.getTimeNotify().isEmpty()){
            Calendar calendar = Calendar.getInstance();
            customBottomSheetDateTimePickerBinding.textViewDate.setText(
                    valueResources.convertDateToDateString(calendar.getTime())+" "
            );
            customBottomSheetDateTimePickerBinding.textViewTime.setText(" "+
                    calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE));
        }
        else{

            String currentStringDate = valueResources.convertDateToStringFormatStandard(noteItem.getDateNotify());
            int index = valueResources.dateFormatStandardList.indexOf(currentStringDate);

            int year = Integer.parseInt(valueResources.dateFormatStandardList.get(index).split("/")[2]);

            if(year == Calendar.getInstance().get(Calendar.YEAR)){
                customBottomSheetDateTimePickerBinding.textViewDate.setText(valueResources.getItemListDateString(index)+" ");
            }
            else{
                customBottomSheetDateTimePickerBinding.textViewDate.setText(
                        valueResources.getItemListDateString(index) +", "+year+" ");
            }

            customBottomSheetDateTimePickerBinding.textViewTime.setText(" "+noteItem.getTimeNotify());
        }

        bottomSheetDialogChip.setContentView(customBottomSheetDateTimePickerBinding.getRoot());
        bottomSheetDialogChip.getBehavior().setDraggable(false);

        bottomSheetDialogChip.show();

        customBottomSheetDateTimePickerBinding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialogChip.dismiss();
            }
        });

        customBottomSheetDateTimePickerBinding.buttonOk.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {

                bottomSheetDialogChip.dismiss();

                int datePos = customBottomSheetDateTimePickerBinding.numberPicker.getValue();
                int hoursValuePos = customBottomSheetDateTimePickerBinding.numberPickerHours.getValue();
                int minutesValuePos = customBottomSheetDateTimePickerBinding.numberPickerMinutes.getValue();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM");

                String dateTxt = valueResources.getItemDateFormatStandard(datePos);

                if(Integer.parseInt(dateTxt.split("/")[2]) == Calendar.getInstance().get(Calendar.YEAR)){
                    customLayoutBottomAddNotesBinding.chipAlarmText.setText(simpleDateFormat.format(valueResources.getItemListDateDate(datePos))+" "+(hoursValuePos < 10 ? "0"+hoursValuePos : hoursValuePos)+":"+(minutesValuePos < 10 ? "0"+minutesValuePos : minutesValuePos));
                }
                else{
                    customLayoutBottomAddNotesBinding.chipAlarmText.setText(simpleDateFormat.format(valueResources.getItemListDateDate(datePos))+"/"+dateTxt.split("/")[2]+" "+(hoursValuePos < 10 ? "0"+hoursValuePos : hoursValuePos)+":"+(minutesValuePos < 10 ? "0"+minutesValuePos : minutesValuePos));
                }


                noteItem.setDateNotify(valueResources.getItemListDateDate(datePos));

                noteItem.setTimeNotify(String.valueOf((hoursValuePos<10?"0"+hoursValuePos:hoursValuePos)+":"+(minutesValuePos<10?"0"+minutesValuePos:minutesValuePos)));

            }
        });


        customBottomSheetDateTimePickerBinding.numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldPos, int newPos) {

                Date date = valueResources.getItemListDateDate(newPos);
                Calendar calendar = Calendar.getInstance();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
                customBottomSheetDateTimePickerBinding.textViewDate.post(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        customBottomSheetDateTimePickerBinding.textViewDate.setText(
                                valueResources.getItemListDateString(numberPicker.getValue())
                                        +
                                        ((simpleDateFormat.format(date).equals(String.valueOf(calendar.get(Calendar.YEAR)))) ? "" : ", "+simpleDateFormat.format(date))+" ");
                    }
                });
            }
        });

        customBottomSheetDateTimePickerBinding.numberPickerHours.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldPos, int newPos) {
                String text = customBottomSheetDateTimePickerBinding.textViewTime.getText().toString();
                customBottomSheetDateTimePickerBinding.textViewTime.post(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        customBottomSheetDateTimePickerBinding.textViewTime
                                .setText(newPos+":"+text.split(":")[1]);
                    }
                });
            }
        });

        customBottomSheetDateTimePickerBinding.numberPickerMinutes.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldPos, int newPos) {
                String text = customBottomSheetDateTimePickerBinding.textViewTime.getText().toString();
                customBottomSheetDateTimePickerBinding.textViewTime.post(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        customBottomSheetDateTimePickerBinding.textViewTime
                                .setText(text.split(":")[0]+":"+(newPos < 10 ? "0"+newPos : newPos));
                    }
                });
            }
        });
        return  bottomSheetDialogChip;
    }
}
