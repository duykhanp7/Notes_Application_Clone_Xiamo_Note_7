package com.example.notesapplication.adapter;

import static com.example.notesapplication.main.NotesActivityMain.fragmentManager;
import static com.example.notesapplication.adapter.NotesItemAdapter.isShowed;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notesapplication.main.NotesActivityMain;
import com.example.notesapplication.R;
import com.example.notesapplication.databinding.CustomChildrenNoteItemBinding;
import com.example.notesapplication.databinding.CustomLayoutBottomAddNotesBinding;
import com.example.notesapplication.resources.ValueResources;
import com.example.notesapplication.bottomSheet.BottomSheetDialogFragmentFixNotes;
import com.example.notesapplication.model.ChildrenNoteItem;
import com.example.notesapplication.model.NoteItem;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.util.List;
import java.util.Objects;

public class ChildrenNotesItemAdapter extends RecyclerView.Adapter<ChildrenNotesItemAdapter.ViewHolder> {

    NoteItem noteItem;
    public static ValueResources valueResources;
    public ChildrenNotesItemAdapter(NoteItem noteItem){
        valueResources = new ValueResources();
        this.noteItem = noteItem;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        CustomChildrenNoteItemBinding customChildrenNoteItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext())
                , R.layout.custom_children_note_item
                ,parent
                ,false
                );
        return new ViewHolder(customChildrenNoteItemBinding,noteItem,this);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.customChildrenNoteItemBinding.setChildrenNoteItem(noteItem.getListNotes().get(position));
        holder.customChildrenNoteItemBinding.setNoteItem(noteItem);
        holder.customChildrenNoteItemBinding.childrenCheckBox.setChecked(noteItem.getListNotes().get(position).isChecked());
        if(noteItem.getListNotes().size()==1 && noteItem.title.trim().isEmpty()){
            noteItem.customItemNotesBinding.recyclerChildrenNotes.setPadding(0,32,0,-30);
        }
    }


    @Override
    public int getItemCount() {
        return noteItem.getListNotes().size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        CustomChildrenNoteItemBinding customChildrenNoteItemBinding;
        NoteItem noteItem;
        ChildrenNotesItemAdapter adapter;
        boolean updated = false;

        public ViewHolder(@NonNull CustomChildrenNoteItemBinding itemView,NoteItem noteItem,ChildrenNotesItemAdapter adapter) {
            super(itemView.getRoot());
            this.customChildrenNoteItemBinding = itemView;
            this.noteItem = noteItem;
            this.adapter = adapter;
            itemView.getRoot().setOnClickListener(this);
            itemView.getRoot().setOnLongClickListener(this);
            OnItemClickInView();
        }

        @Override
        public void onClick(View view) {
            updated = false;
            Toast.makeText(itemView.getContext(),"CHILDREN CLICKED",Toast.LENGTH_LONG).show();
            OpenBottomDialog(view);
        }

        @Override
        public boolean onLongClick(View view) {
            Log.i("AAA","IS LONG CLICKED ITEM CHILDREN");
            noteItem.setHoveredToDelete(true);
            NotesActivityMain.updateTextCountNumberItemsChecked();
            return true;
        }

        public void OnItemClickInView(){
            customChildrenNoteItemBinding.childrenTextInput.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!isShowed.get()){
                        updated = false;
                        Toast.makeText(itemView.getContext(),"CHILDREN CLICKED",Toast.LENGTH_LONG).show();
                        OpenBottomDialog(view);
                    }
                    else{
                        noteItem.setHoveredToDelete(!noteItem.isHoveredToDelete());
                        NotesActivityMain.updateTextCountNumberItemsChecked();
                    }
                }
            });

            customChildrenNoteItemBinding.childrenTextInput.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Log.i("AAA","IS LONG CLICKED ITEM CHILDREN");
                    if(!isShowed.get()){
                        NotesActivityMain.showTabLayout(View.GONE);
                        NotesActivityMain.showTopLayoutDelete(View.VISIBLE);
                        NotesActivityMain.showBottomLayoutDelete(View.VISIBLE);
                        NotesActivityMain.showButtonAddNotes(View.GONE);
                        isShowed.set(true);
                        NotesActivityMain.resetStateExpandableAllItems();
                    }
                    noteItem.setHoveredToDelete(true);
                    NotesActivityMain.updateTextCountNumberItemsChecked();
                    return true;
                }
            });

            customChildrenNoteItemBinding.childrenCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(getAdapterPosition() != -1){
                        if(noteItem.listNotes.get(getAdapterPosition()).isChecked() != b){
                            noteItem.getListNotes().get(getAdapterPosition()).setChecked(b);
                            customChildrenNoteItemBinding.childrenCheckBox.setChecked(b);
                            noteItem.setNumberItemCheck(
                                    String.valueOf(
                                            Integer.parseInt(
                                                    noteItem.getNumberItemCheck()) + ( b ? 1 : -1)));

                            if(Integer.parseInt(noteItem.getNumberItemCheck())==noteItem.getListNotes().size()){
                                noteItem.setChecked(true);
                                noteItem.setExpandable(false);
                            }
                            else{
                                if(noteItem.getChecked()){
                                    noteItem.setChecked(false);
                                    SharedPreferences sharedPreferences = compoundButton.getContext().getSharedPreferences("data_state",Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean("fromChildrenCheckBox",true);
                                    editor.commit();
                                }
                            }
                        }
                    }
                }
            });
        }



        public void OpenBottomDialog(View view){
            BottomSheetDialogFragmentFixNotes dialog = new BottomSheetDialogFragmentFixNotes(view.getContext(), noteItem);
            dialog.show(fragmentManager, dialog.getTag());
        }


        public int countItemCheck(List<ChildrenNoteItem> list){
            int a = 0;
            for (ChildrenNoteItem item : list){
                if(item.isChecked()){
                    a++;
                }
            }
            return a;
        }



        @SuppressLint("NotifyDataSetChanged")
        public void CompletedEdit(BottomSheetDialog bottomSheetDialog, CustomLayoutBottomAddNotesBinding customLayoutBottomAddNotesBinding){

            bottomSheetDialog.dismiss();
            noteItem.setTitle(Objects.requireNonNull(customLayoutBottomAddNotesBinding.textViewNoteTitle.getText()).toString());
            if(noteItem.getTitle().isEmpty()){
                if(noteItem.getListNotes().size()==1){
                    noteItem.setTitle("");
                    if(!noteItem.getExpandable()){
                        noteItem.setExpandable(true);
                    }
                }
            }


            Objects.requireNonNull(noteItem.notesItemBottomSheetAdapterObservableField.get()).returnTempToMainData();
            if(noteItem.getListNotes().size() == 1 && noteItem.getListNotes().get(0).getText().isEmpty()){
                noteItem.setExpandable(false);
            }
            noteItem.setNumberItemCheck(String.valueOf(countItemCheck(noteItem.getListNotes())));
            adapter.notifyDataSetChanged();



            if(noteItem.getTitle().isEmpty() && noteItem.getListNotes().size() == 1 && !noteItem.getTimeNotify().isEmpty()){
                noteItem.customItemNotesBinding.recyclerChildrenNotes.setPadding(0,0,0,-20);
            }
            else if(noteItem.getTitle().isEmpty() && noteItem.getListNotes().size() == 1 && noteItem.getTimeNotify().isEmpty()){
                noteItem.customItemNotesBinding.recyclerChildrenNotes.setPadding(0,32,0,-30);
            }
            else{
                noteItem.customItemNotesBinding.recyclerChildrenNotes.setPadding(80,0,0,0);
            }


            if(!noteItem.getTimeNotify().isEmpty()){
                noteItem.setTimeNotifyNote(customLayoutBottomAddNotesBinding.chipAlarmText.getText().toString());
            }
            else{
                noteItem.setTimeNotifyNote("");
            }
            updated = true;
        }
    }
}
