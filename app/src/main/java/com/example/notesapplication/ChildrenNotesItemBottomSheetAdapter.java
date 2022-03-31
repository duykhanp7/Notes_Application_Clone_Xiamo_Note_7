package com.example.notesapplication;

import static com.example.notesapplication.AddNotesFragment.fromAddNotes;
import static com.example.notesapplication.BottomSheetDialogFragmentFixNotes.fromFixNotes;

import android.annotation.SuppressLint;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notesapplication.databinding.CustomChildrenNoteItemBottomDialogBinding;
import com.example.notesapplication.databinding.CustomLayoutBottomAddNotesBinding;

import java.util.ArrayList;
import java.util.List;

public class ChildrenNotesItemBottomSheetAdapter extends RecyclerView.Adapter<ChildrenNotesItemBottomSheetAdapter.ViewHolder> {

    List<ChildrenNoteItem> lists;
    List<ChildrenNoteItem> temp;
    public NoteItem noteItem;
    @SuppressLint("StaticFieldLeak")
    public static CustomLayoutBottomAddNotesBinding customLayoutBottomAddNotesBinding;

    public ChildrenNotesItemBottomSheetAdapter(List<ChildrenNoteItem> listAdd,NoteItem noteItem) {
        this.noteItem = noteItem;
        this.lists = listAdd;
        temp = new ArrayList<>();
        for (int i = 0; i < listAdd.size(); i++) {
            temp.add(new ChildrenNoteItem(listAdd.get(i).getIdChildren(), listAdd.get(i).getText(), listAdd.get(i).isChecked()));
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CustomChildrenNoteItemBottomDialogBinding customChildrenNoteItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext())
                , R.layout.custom_children_note_item_bottom_dialog
                , parent
                , false
        );
        return new ViewHolder(customChildrenNoteItemBinding, this, temp, lists,noteItem);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChildrenNoteItem item = temp.get(position);
        holder.customChildrenNoteItemBinding.setChildrenNoteItem(item);
        if (position == temp.size() - 1) {
            holder.customChildrenNoteItemBinding.childrenTextInput.requestFocus();
        }
    }


    public void UpdateData(){
        for (int i = 0; i < lists.size(); i++) {
            temp.get(i).setChecked(lists.get(i).isChecked());
        }
    }

    @Override
    public int getItemCount() {
        return temp.size();
    }

    public void setObject(CustomLayoutBottomAddNotesBinding a){
        customLayoutBottomAddNotesBinding = a;
    }

    public void returnTempToMainData() {
        for (int i = 0; i < temp.size(); i++) {
            lists.get(i).setText(temp.get(i).getText());
            lists.get(i).setChecked(temp.get(i).isChecked());
        }
    }

 public List<ChildrenNoteItem> returnTempToMainDataToList(){
     for (int i = 0; i < temp.size(); i++) {
         temp.get(i).setIdChildren(i+1);
     }
     return temp;
 }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        CustomChildrenNoteItemBottomDialogBinding customChildrenNoteItemBinding;
        ChildrenNotesItemBottomSheetAdapter adapter;
        List<ChildrenNoteItem> subLists;
        List<ChildrenNoteItem> subTemp;
        NoteItem noteItem;
        boolean hasUpdatedColor = false;

        public ViewHolder(@NonNull CustomChildrenNoteItemBottomDialogBinding itemView, ChildrenNotesItemBottomSheetAdapter adapter, List<ChildrenNoteItem> subTemp, List<ChildrenNoteItem> subLists,NoteItem noteItem) {
            super(itemView.getRoot());
            this.customChildrenNoteItemBinding = itemView;
            this.adapter = adapter;
            this.subTemp = subTemp;
            this.subLists = subLists;
            this.noteItem = noteItem;
            OnClickCallBack();
        }


        public void OnClickCallBack() {

            customChildrenNoteItemBinding.childrenTextInput.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                    if (keyEvent.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_DEL) {
                        if(subTemp.size()!=1){
                            if(!subTemp.get(getAdapterPosition()).getText().isEmpty()){
                                subTemp.get(getAdapterPosition()-1).appendText(subTemp.get(getAdapterPosition()).getText());
                                adapter.notifyItemChanged(getAdapterPosition()-1);
                            }
                            subTemp.remove(getAdapterPosition());
                            subLists.remove(getAdapterPosition());
                            if(subTemp.size() != 1){
                                customLayoutBottomAddNotesBinding.textInputLayout.setVisibility(View.VISIBLE);
                                customLayoutBottomAddNotesBinding.textViewNoteTitle.setVisibility(View.VISIBLE);
                            }
                            else{
                                if(customLayoutBottomAddNotesBinding.textViewNoteTitle.getText().toString().isEmpty()){
                                    customLayoutBottomAddNotesBinding.textInputLayout.setVisibility(View.GONE);
                                    customLayoutBottomAddNotesBinding.textViewNoteTitle.setVisibility(View.GONE);
                                }
                            }
                            adapter.notifyItemRemoved(getAdapterPosition());
                        }
                        else{
                            customChildrenNoteItemBinding.childrenTextInput.setHint("Chạm \"Enter\" để tạo nhiệm vụ");
                            customChildrenNoteItemBinding.childrenTextInput.setHintTextColor(view.getContext().getResources().getColor(R.color.brown,null));
                            //noteItem.getListNotes().get(getAdapterPosition()).setText("");
                        }
                        if(customLayoutBottomAddNotesBinding.textViewNoteTitle.getText().toString().isEmpty()){
                            if(subTemp.size()==1){
                                customLayoutBottomAddNotesBinding.recyclerViewEditNote.setPadding(0,0,0,0);
                            }
                        }
                    }
                    return false;
                }
            });

            customChildrenNoteItemBinding.childrenTextInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

                    subTemp.get(getAdapterPosition()).setText(charSequence.toString().replaceAll("\n", ""));
                    subTemp.get(getAdapterPosition()).setChecked(customChildrenNoteItemBinding.childrenCheckBox.isChecked());

                    if (charSequence.toString().contains("\n") && !subTemp.get(getAdapterPosition()).isChecked()) {
                        int pos = customChildrenNoteItemBinding.childrenTextInput.getSelectionStart();
                        boolean b = charSequence.toString().trim().isEmpty();
                        if(pos == charSequence.length() && getAdapterPosition() == subTemp.size()-1 && !b){
                            customChildrenNoteItemBinding.childrenTextInput.setText(charSequence.toString().replace("\n", ""));
                            subTemp.add(new ChildrenNoteItem(subTemp.size() + 1, "", false));
                            subLists.add(new ChildrenNoteItem(subLists.size() + 1, "", false));
                            if(subTemp.size() != 1){
                                customLayoutBottomAddNotesBinding.textInputLayout.setVisibility(View.VISIBLE);
                                customLayoutBottomAddNotesBinding.textViewNoteTitle.setVisibility(View.VISIBLE);
                            }
                            else{
                                customLayoutBottomAddNotesBinding.textInputLayout.setVisibility(View.GONE);
                                customLayoutBottomAddNotesBinding.textViewNoteTitle.setVisibility(View.GONE);
                            }
                            adapter.notifyItemInserted(getAdapterPosition() + 1);
                            customLayoutBottomAddNotesBinding.recyclerViewEditNote.setPadding(70,0,0,0);
                        }
                        else{
                            if(!b){
                                String text = charSequence.toString();
                                String textStart,textEnd;
                                textStart = text.substring(0,pos).replace("\n","");
                                textEnd = text.substring(pos).replace("\n","");
                                subTemp.add(getAdapterPosition(),new ChildrenNoteItem(subLists.size()+1,"",false));
                                subLists.add(getAdapterPosition(),new ChildrenNoteItem(subLists.size()+1,"",false));
                                adapter.notifyItemInserted(getAdapterPosition());
                                subTemp.get(getAdapterPosition()).setText(textEnd);
                                adapter.notifyItemChanged(getAdapterPosition());
                                subTemp.get(getAdapterPosition()-1).setText(textStart);
                                adapter.notifyItemChanged(getAdapterPosition()-1);
                                customLayoutBottomAddNotesBinding.textInputLayout.setVisibility(View.VISIBLE);
                                customLayoutBottomAddNotesBinding.textViewNoteTitle.setVisibility(View.VISIBLE);
                                customLayoutBottomAddNotesBinding.recyclerViewEditNote.setPadding(70,0,0,0);
                            }
                        }
                    }
                    if(charSequence.toString().trim().isEmpty() && charSequence.toString().contains("\n")){
                        customChildrenNoteItemBinding.childrenTextInput.setText("");
                    }

                    if(charSequence.toString().trim().isEmpty() && subTemp.size()==1){
                        customLayoutBottomAddNotesBinding.buttonCompleteEditNote.setTextColor(ContextCompat.getColor(customLayoutBottomAddNotesBinding.getRoot().getContext(),R.color.brown));
                        customLayoutBottomAddNotesBinding.buttonCompleteEditNote.setClickable(false);
                        customLayoutBottomAddNotesBinding.buttonCompleteEditNote.setFocusable(false);
                        customLayoutBottomAddNotesBinding.buttonCompleteEditNote.setEnabled(false);
                    }
                    else {
                        customLayoutBottomAddNotesBinding.buttonCompleteEditNote.setTextColor(ContextCompat.getColor(customLayoutBottomAddNotesBinding.getRoot().getContext(), android.R.color.holo_orange_dark));
                        customLayoutBottomAddNotesBinding.buttonCompleteEditNote.setClickable(true);
                        customLayoutBottomAddNotesBinding.buttonCompleteEditNote.setFocusable(true);
                        customLayoutBottomAddNotesBinding.buttonCompleteEditNote.setEnabled(true);
                    }

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            customChildrenNoteItemBinding.childrenCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    customChildrenNoteItemBinding.getChildrenNoteItem().setChecked(b);
                }
            });

        }

    }

}
