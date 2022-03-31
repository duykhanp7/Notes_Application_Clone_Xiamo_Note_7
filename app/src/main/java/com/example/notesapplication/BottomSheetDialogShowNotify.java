package com.example.notesapplication;

import static com.example.notesapplication.AddNotesFragment.databaseSaveNoteItems;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetDialogShowNotify extends BottomSheetDialogFragment {

    String txtNotify;
    int id;

    public BottomSheetDialogShowNotify(int id,String text){
        this.id = id;
        this.txtNotify = text;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        NotificationManagerCompat.from(requireContext()).cancel(id);
        TextView textViewTextNotify,buttonOkeCloseSheet;

        View view = LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_notify_notes_on_time,null,false);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext(),R.style.CustomBottomSheetDialog);

        textViewTextNotify = view.findViewById(R.id.textViewNotesShow);
        textViewTextNotify.setText(txtNotify);

        buttonOkeCloseSheet = view.findViewById(R.id.buttonOkeCloseSheet);
        buttonOkeCloseSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /////
                NoteItem item = AddNotesFragment.findItemWithId(id);
                if(item != null){
                    item.setOverTime(true);
                }
                if(databaseSaveNoteItems != null){
                    databaseSaveNoteItems.updateStateOverTimeNoteItem(id,true);
                }
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.getBehavior().setDraggable(false);
        bottomSheetDialog.setCanceledOnTouchOutside(false);

        return bottomSheetDialog;
    }
}
