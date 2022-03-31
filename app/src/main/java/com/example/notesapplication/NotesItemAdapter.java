package com.example.notesapplication;

import static com.example.notesapplication.NotesActivityMain.fragmentManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notesapplication.databinding.CustomItemNotesBinding;
import com.example.notesapplication.databinding.CustomLayoutBottomAddNotesBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;
import java.util.Objects;


public class NotesItemAdapter extends RecyclerView.Adapter<NotesItemAdapter.ViewHolder>{

    public List<NoteItem> noteItemList;
    Context context;
    public static ValueResources valueResources;

    public NotesItemAdapter(List<NoteItem> lists,Context context){
        valueResources = new ValueResources();
        noteItemList = lists;
        this.context = context;
        SharedPreferences data = context.getSharedPreferences("data_state",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = data.edit();
        editor.putBoolean("fromChildrenCheckBox",false);
        editor.commit();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CustomItemNotesBinding customItemNotesBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext())
                ,R.layout.custom_item_notes,parent,false
        );
        return new ViewHolder(customItemNotesBinding,this,noteItemList);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NoteItem noteItem = noteItemList.get(position);
        noteItem.setCustomItemNotesBinding(holder.customItemNotesBinding);
        holder.customItemNotesBinding.setNoteItem(noteItem);
        if(noteItem.getTitle().isEmpty() && noteItem.listNotes.size()==1){
            noteItem.setExpandable(true);
            noteItem.setTitle("");
            holder.customItemNotesBinding.recyclerChildrenNotes.setVisibility(View.VISIBLE);
            holder.customItemNotesBinding.textViewOnTimeNotes.setVisibility(View.GONE);
            holder.customItemNotesBinding.textViewOnTimeNotesBelow.setVisibility(View.VISIBLE);
            holder.customItemNotesBinding.recyclerChildrenNotes.setPadding(80,0,0,0);
        }
    }



    @Override
    public int getItemCount() {
        return noteItemList.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{

        CustomItemNotesBinding customItemNotesBinding;
        boolean textChanged = false;
        NotesItemAdapter adapter;
        List<NoteItem> noteItemList;
        boolean updated = false;

        public ViewHolder(@NonNull CustomItemNotesBinding itemView,NotesItemAdapter adapter,List<NoteItem> noteItemList) {
            super(itemView.getRoot());
            this.customItemNotesBinding = itemView;
            this.adapter = adapter;
            this.noteItemList = noteItemList;
            itemView.getRoot().setOnClickListener(this);
            onClickCallBack(itemView.getRoot());
        }

        @Override
        public void onClick(View view) {
            textChanged = true;
            updated = false;
            Toast.makeText(itemView.getContext(),"PARENT CLICKED +",Toast.LENGTH_LONG).show();
            OpenBottomDialog(view);
        }

        @Override
        public boolean onLongClick(View view) {
            return true;
        }



        //CALLBACK ON CLICK
        @SuppressLint("ClickableViewAccessibility")
        public void onClickCallBack(View view){
            customItemNotesBinding.circleButtonShowRecyclerChildren.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(customItemNotesBinding.getNoteItem().getExpandable()){
                        customItemNotesBinding.getNoteItem().setExpandable(false);
                        customItemNotesBinding.circleButtonShowRecyclerChildren.setImageResource(R.drawable.custom_background_expandable_less);
                    }
                    else{
                        customItemNotesBinding.getNoteItem().setExpandable(true);
                        customItemNotesBinding.circleButtonShowRecyclerChildren.setImageResource(R.drawable.custom_background_expandable_more);
                    }

                }
            });

            customItemNotesBinding.textViewNotes.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if(charSequence.toString().isEmpty()){
                        customItemNotesBinding.textViewNotes.setText("Danh sách việc cần làm");
                        customItemNotesBinding.getNoteItem().setTitle("");
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            customItemNotesBinding.checkBoxNotes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    SharedPreferences sharedPreferences = compoundButton.getContext().getSharedPreferences("data_state",Context.MODE_PRIVATE);
                    boolean state = sharedPreferences.getBoolean("fromChildrenCheckBox",false);

                    customItemNotesBinding.getNoteItem().setChecked(b);

                    if(!state){
                        for (ChildrenNoteItem item : customItemNotesBinding.getNoteItem().getListNotes()){
                            if(item.isChecked() != b){
                                item.setChecked(b);
                            }
                        }
                    }

                    if(b){
                        customItemNotesBinding.textViewNotes.setTextColor(view.getContext().getResources().getColor(R.color.brown,null));
                        customItemNotesBinding.getNoteItem().setExpandable(false);
                    }
                    else{
                        customItemNotesBinding.textViewNotes.setTextColor(view.getContext().getResources().getColor(R.color.black,null));
                    }
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("fromChildrenCheckBox",false);
                    editor.commit();
                    //CHANGE NUMBER ITEM CHECKED
                    customItemNotesBinding.getNoteItem().setNumberItemCheck
                            (String.valueOf(countItemCheck(customItemNotesBinding.getNoteItem().getListNotes())));
                    for (ChildrenNoteItem item : noteItemList.get(getAdapterPosition()).getListNotes()){
                    }
                }
            });
        }


        public void OpenBottomDialog(View view){

            NoteItem noteItem = noteItemList.get(getAdapterPosition());
            BottomSheetDialogFragmentFixNotes dialog = new BottomSheetDialogFragmentFixNotes(view.getContext(), noteItem);
            dialog.show(fragmentManager, dialog.getTag());
        }


        public  int countItemCheck(List<ChildrenNoteItem> list){
            int a = 0;
            for (ChildrenNoteItem item : list){
                if(item.isChecked()){
                    a++;
                }
            }
            return a;
        }

        @SuppressLint("NotifyDataSetChanged")
        public void CompletedEdit(BottomSheetDialog bottomSheetDialog, CustomLayoutBottomAddNotesBinding customLayoutBottomAddNotesBinding, NoteItem noteItem){

            bottomSheetDialog.dismiss();
            noteItem.setTitle(Objects.requireNonNull(customLayoutBottomAddNotesBinding.textViewNoteTitle.getText()).toString());
            if(noteItem.getTitle().isEmpty()){
                if(noteItem.getListNotes().size()==1){
                    noteItem.setTitle("");
                    if(!noteItem.getExpandable()){
                        noteItem.setExpandable(true);
                    }
                }
                if(noteItem.getListNotes().size()!=1){
                    customItemNotesBinding.textViewNotes.setText("Danh sách việc cần làm");
                }
            }

            Objects.requireNonNull(noteItem.notesItemBottomSheetAdapterObservableField.get()).returnTempToMainData();
            if(noteItem.getListNotes().size() == 1 && noteItem.getListNotes().get(0).getText().isEmpty()){
                noteItem.setExpandable(false);
            }
            noteItem.setNumberItemCheck(String.valueOf(countItemCheck(noteItem.getListNotes())));
            Objects.requireNonNull(noteItem.notesItemBottomSheetAdapterObservableField.get()).notifyDataSetChanged();
            Objects.requireNonNull(noteItem.noteItemChildrenAdapter.get()).notifyDataSetChanged();

            if(noteItem.getTitle().isEmpty() && noteItem.getListNotes().size() == 1 && !noteItem.getTimeNotify().isEmpty()){
                customItemNotesBinding.recyclerChildrenNotes.setPadding(0,0,0,-20);
            }
            else if(noteItem.getTitle().isEmpty() && noteItem.getListNotes().size() == 1 && noteItem.getTimeNotify().isEmpty()){
                customItemNotesBinding.recyclerChildrenNotes.setPadding(0,32,0,-30);
            }
            else{
                customItemNotesBinding.recyclerChildrenNotes.setPadding(80,0,0,0);
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
