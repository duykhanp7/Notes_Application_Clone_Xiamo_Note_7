package com.example.notesapplication.adapter;

import static com.example.notesapplication.main.NotesActivityMain.fragmentManager;

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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notesapplication.main.NotesActivityMain;
import com.example.notesapplication.R;
import com.example.notesapplication.resources.ValueResources;
import com.example.notesapplication.bottomSheet.BottomSheetDialogFragmentFixNotes;
import com.example.notesapplication.model.ChildrenNoteItem;
import com.example.notesapplication.model.NoteItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class NotesItemAdapter extends RecyclerView.Adapter<NotesItemAdapter.ViewHolder> implements Filterable {

    public List<NoteItem> noteItemList;
    public List<NoteItem> noteItemListReserve = new ArrayList<>();
    Context context;
    public static ValueResources valueResources;
    public static ObservableField<Boolean> isShowed = new ObservableField<>();

    public NotesItemAdapter(List<NoteItem> lists,Context context){
        valueResources = new ValueResources();
        noteItemList = lists;
        noteItemListReserve = lists;
        this.context = context;
        SharedPreferences data = context.getSharedPreferences("data_state",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = data.edit();
        editor.putBoolean("fromChildrenCheckBox",false);
        editor.commit();
        isShowed.set(false);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CustomItemNotesBinding customItemNotesBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext())
                , R.layout.custom_item_notes,parent,false
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
        Log.i("AAA","ON BIND ITEM AGAIN");
    }



    @Override
    public int getItemCount() {
        return noteItemList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String usersInput = charSequence.toString();
                if(usersInput.isEmpty()){
                    noteItemList = noteItemListReserve;
                }
                else{
                    Log.i("AAA","USER INPUT : "+usersInput);
                    List<NoteItem> noteItemsPos = new ArrayList<>();
                    for (int i = 0 ; i < noteItemListReserve.size() ; i++){
                        NoteItem item = noteItemListReserve.get(i);
                        Log.i("AAA","TITLE EQUALS : "+item.getTitle() +" -- "+usersInput);
                        if(item.getTitle().toLowerCase(Locale.ROOT).contains(usersInput.toLowerCase(Locale.ROOT))){
                            noteItemsPos.add(item);
                        }
                        else{
                            for (ChildrenNoteItem a : item.getListNotes()){
                                Log.i("AAA","TITLE EQUALS CHILDREN : "+a.getText() +" -- "+usersInput);
                                if(a.getText().toLowerCase(Locale.ROOT).contains(usersInput.toLowerCase(Locale.ROOT))){
                                    noteItemsPos.add(item);
                                }
                            }
                        }
                    }
                    noteItemList = noteItemsPos;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = noteItemList;

                return filterResults;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                noteItemList = (List<NoteItem>) filterResults.values;
                notifyDataSetChanged();
            }
        };
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
            itemView.getRoot().setOnLongClickListener(this);
            onClickCallBack(itemView.getRoot());
        }

        @Override
        public void onClick(View view) {
            if(!isShowed.get()){
                textChanged = true;
                updated = false;
                Toast.makeText(itemView.getContext(),"PARENT CLICKED +",Toast.LENGTH_LONG).show();
                OpenBottomDialog(view);
            }
            else{
                noteItemList.get(getAdapterPosition()).setHoveredToDelete(!noteItemList.get(getAdapterPosition()).isHoveredToDelete());
                NotesActivityMain.updateTextCountNumberItemsChecked();
            }
        }

        @Override
        public boolean onLongClick(View view) {
            Log.i("AAA","IS LONG CLICKED ITEM PARENT");
            if(!isShowed.get()){
                NotesActivityMain.showTabLayout(View.GONE);
                NotesActivityMain.showTopLayoutDelete(View.VISIBLE);
                NotesActivityMain.showBottomLayoutDelete(View.VISIBLE);
                NotesActivityMain.showButtonAddNotes(View.GONE);
                NotesActivityMain.resetStateExpandableAllItems();
                isShowed.set(true);
            }
            noteItemList.get(getAdapterPosition()).setHoveredToDelete(true);
            NotesActivityMain.updateTextCountNumberItemsChecked();
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
                }
            });

            customItemNotesBinding.radioButtonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    noteItemList.get(getAdapterPosition()).setHoveredToDelete(!noteItemList.get(getAdapterPosition()).isHoveredToDelete());
                    NotesActivityMain.updateTextCountNumberItemsChecked();
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
    }
}
