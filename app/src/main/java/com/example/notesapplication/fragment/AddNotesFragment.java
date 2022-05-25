package com.example.notesapplication.fragment;

import static com.example.notesapplication.main.NotesActivityMain.fragmentManager;
import static com.example.notesapplication.adapter.NotesItemAdapter.valueResources;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.example.notesapplication.bottomSheet.BottomSheetDialogFragmentSetTimeNotify;
import com.example.notesapplication.databinding.BottomSheetAddQuotesLayoutBinding;
import com.example.notesapplication.databinding.CustomLayoutBottomAddNotesBinding;
import com.example.notesapplication.databinding.FragmentAddNotesBinding;
import com.example.notesapplication.model.ChildrenNoteItem;
import com.example.notesapplication.database.DatabaseSaveNoteItems;
import com.example.notesapplication.model.IDItem;
import com.example.notesapplication.model.NoteItem;
import com.example.notesapplication.adapter.NotesItemAdapter;
import com.example.notesapplication.R;
import com.example.notesapplication.bottomSheet.ShowSheetNotifyWorker;
import com.example.notesapplication.bottomSheet.ShowSheetNotifyWorkerBottom;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class AddNotesFragment extends Fragment{

    public static ObservableField<NotesItemAdapter> listsObservable = new ObservableField<>();
    public static List<NoteItem> lists = null;
    boolean updated = false;
    public static FragmentAddNotesBinding fragmentAddNotesBinding;
    public static DatabaseSaveNoteItems databaseSaveNoteItems;
    public static ObservableField<Boolean> isHoveredDelete;

    Context context;

//    public static ObservableField<Boolean> stateDeleteAllItems = new ObservableField<>();

    public static boolean fromAddNotes = false; //variables to check bottom sheet add notes open or sheet fix open


    public AddNotesFragment(List<NoteItem> lists, Context context){
        AddNotesFragment.lists = lists;
        this.context = context;
        //stateDeleteAllItems.set(false);
        databaseSaveNoteItems = new DatabaseSaveNoteItems(context);
        isHoveredDelete = new ObservableField<>(false);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentAddNotesBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_notes,container,false);
        listsObservable.set(new NotesItemAdapter(lists, requireContext()));
        fragmentAddNotesBinding.setAddNotesFragmentItem(this);

        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int pos = viewHolder.getAdapterPosition();
                databaseSaveNoteItems.deleteNoteItem(lists.get(pos).getId());
                lists.remove(pos);
                Objects.requireNonNull(listsObservable.get()).notifyItemRemoved(pos);
                if (lists.size() != 0){
                    fragmentAddNotesBinding.textViewNull.setVisibility(View.INVISIBLE);
                    fragmentAddNotesBinding.icEmptyNotes.setVisibility(View.INVISIBLE);
                }
                else{
                    fragmentAddNotesBinding.textViewNull.setVisibility(View.VISIBLE);
                    fragmentAddNotesBinding.icEmptyNotes.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(fragmentAddNotesBinding.recyclerViewListNotes);

        fragmentAddNotesBinding.searchViewFilterAdapter.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                
                Objects.requireNonNull(listsObservable.get()).getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Objects.requireNonNull(listsObservable.get()).getFilter().filter(newText);
                return false;
            }
        });

        return fragmentAddNotesBinding.getRoot();
    }


    public void OpenBottomSheetDialogAddNotes(){
        updated = false;
        fromAddNotes = true;
        List<ChildrenNoteItem> listItemChildren = new ArrayList<>();
        listItemChildren.add(new ChildrenNoteItem(1,"",false));
        NoteItem noteItem = new NoteItem(IDItem.idNoteItem,listItemChildren,"","",true);
        IDItem.idNoteItem++;

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext(),R.style.CustomBottomSheetDialog);

        CustomLayoutBottomAddNotesBinding customLayoutBottomAddNotesBinding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext()),R.layout.custom_layout_bottom_add_notes,
                null,
                false);

        bottomSheetDialog.setContentView(customLayoutBottomAddNotesBinding.getRoot());
        customLayoutBottomAddNotesBinding.setNoteItem(noteItem);
        Objects.requireNonNull(noteItem.notesItemBottomSheetAdapterObservableField.get()).UpdateData();
        Objects.requireNonNull(noteItem.notesItemBottomSheetAdapterObservableField.get()).setObject(customLayoutBottomAddNotesBinding);

        customLayoutBottomAddNotesBinding.buttonCompleteEditNote.setTextColor(ContextCompat.getColor(requireContext(),R.color.brown));
        customLayoutBottomAddNotesBinding.buttonCompleteEditNote.setClickable(false);
        customLayoutBottomAddNotesBinding.buttonCompleteEditNote.setFocusable(false);
        customLayoutBottomAddNotesBinding.buttonCompleteEditNote.setEnabled(false);

        if(noteItem.listNotes.size() > 1){

            ViewGroup.LayoutParams params = customLayoutBottomAddNotesBinding.recyclerViewEditNote.getLayoutParams();
            params.height = noteItem.listNotes.size()*150;

            if(params.height >= 1560){
                params.height = 800;
            }

            customLayoutBottomAddNotesBinding.recyclerViewEditNote.setLayoutParams(params);
        }

        if(noteItem.listNotes.size() == 1 && noteItem.getTitle()==""){
            customLayoutBottomAddNotesBinding.recyclerViewEditNote.setPadding(0,0,0,0);
        }

        bottomSheetDialog.getBehavior().setDraggable(false);
        bottomSheetDialog.setCanceledOnTouchOutside(true);


        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                fromAddNotes = false;
                if(!updated){
                    Objects.requireNonNull(noteItem.notesItemBottomSheetAdapterObservableField.get()).returnTempToMainData();
                    if(noteItem.getListNotes().size() == 1 && noteItem.getListNotes().get(0).getText().trim().isEmpty()){
                        IDItem.idNoteItem--;
                    }
                    else {
                        noteItem.setTitle(customLayoutBottomAddNotesBinding.textViewNoteTitle.getText().toString());
                        if(!noteItem.getTimeNotify().isEmpty()){
                            noteItem.setTimeNotifyNote(customLayoutBottomAddNotesBinding.chipAlarmText.getText().toString());
                        }
                        else {
                            noteItem.setTimeNotifyNote("");
                        }

                        lists.add(noteItem);
                        editChildrenItemId(noteItem);

                        Objects.requireNonNull(listsObservable.get()).notifyItemInserted(lists.size()-1);
                        updated = true;
                        if(lists.size()!=0){
                            fragmentAddNotesBinding.textViewNull.setVisibility(View.INVISIBLE);
                            fragmentAddNotesBinding.icEmptyNotes.setVisibility(View.INVISIBLE);
                        }else {
                            fragmentAddNotesBinding.textViewNull.setVisibility(View.VISIBLE);
                            fragmentAddNotesBinding.icEmptyNotes.setVisibility(View.VISIBLE);
                        }
                        //START JOB SCHEDULER
                        startJobScheduler(noteItem);
                        //SAVE NEW NOTE ITEM AND CHILDREN ITEMS TO DATABASE
                        databaseSaveNoteItems.insertItemToDatabase(noteItem);
                    }
                }
            }
        });


        customLayoutBottomAddNotesBinding.chipAlarmText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialogFragmentSetTimeNotify dialog =
                        new BottomSheetDialogFragmentSetTimeNotify(valueResources, view.getContext(), noteItem,customLayoutBottomAddNotesBinding);
                dialog.show(fragmentManager, dialog.getTag());
            }
        });

        customLayoutBottomAddNotesBinding.chipAlarmText.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noteItem.setTimeNotify("");
                noteItem.setDateNotify(null);
                customLayoutBottomAddNotesBinding.chipAlarmText.setText("Đặt báo thức");
            }
        });

        //BUTTON ADD NOTE ITEM
        customLayoutBottomAddNotesBinding.buttonCompleteEditNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!updated){
                    fromAddNotes = false;
                    noteItem.setTitle(customLayoutBottomAddNotesBinding.textViewNoteTitle.getText().toString());
                    if(!noteItem.getTimeNotify().isEmpty()){
                        noteItem.setTimeNotifyNote(customLayoutBottomAddNotesBinding.chipAlarmText.getText().toString());
                    }
                    else {
                        noteItem.setTimeNotifyNote("");
                    }
                    Objects.requireNonNull(noteItem.notesItemBottomSheetAdapterObservableField.get()).returnTempToMainData();

                    bottomSheetDialog.dismiss();

                    lists.add(noteItem);
                    editChildrenItemId(noteItem);
                    Objects.requireNonNull(listsObservable.get()).notifyItemInserted(lists.size()-1);
                    updated = true;
                    if(lists.size()!=0){
                        fragmentAddNotesBinding.textViewNull.setVisibility(View.INVISIBLE);
                        fragmentAddNotesBinding.icEmptyNotes.setVisibility(View.INVISIBLE);
                    }else {
                        fragmentAddNotesBinding.textViewNull.setVisibility(View.VISIBLE);
                        fragmentAddNotesBinding.icEmptyNotes.setVisibility(View.VISIBLE);
                    }
                    //START JOB SCHEDULER
                    startJobScheduler(noteItem);
                    //SAVE NEW NOTE ITEM AND CHILDREN ITEMS TO DATABASE
                    databaseSaveNoteItems.insertItemToDatabase(noteItem);
                }
            }
        });
        updated = false;
        bottomSheetDialog.show();
    }

    private void startJobScheduler(NoteItem noteItem) {
        String txt = editTextNotifyOnTime(noteItem);
        Data data = new Data.Builder()
                .putInt("id",noteItem.getId())
                .putString("text_notify",txt)
                .build();

        if(noteItem.getDateNotify() != null){
            long timeCountDown = calculateTimeBetweenTwoDate(noteItem);
            if(timeCountDown > 0){
                WorkRequest workRequest;
                if(fragmentManager != null){
                    workRequest = new OneTimeWorkRequest.Builder(ShowSheetNotifyWorkerBottom.class)
                            .addTag("work_request")
                            .setInputData(data)
                            .setInitialDelay(timeCountDown, TimeUnit.MILLISECONDS)
                            .build();
                }
                else{
                    workRequest = new OneTimeWorkRequest.Builder(ShowSheetNotifyWorker.class)
                            .addTag("work_request")
                            .setInputData(data)
                            .setInitialDelay(timeCountDown, TimeUnit.MILLISECONDS)
                            .build();
                }
                WorkManager.getInstance(requireContext()).enqueue(workRequest);
            }
            else{
                noteItem.setOverTime(true);
            }
        }
    }


    public static String editTextNotifyOnTime(NoteItem item){
        List<String> stringList = new ArrayList<>();
        for (ChildrenNoteItem subItem : item.getListNotes()){
            stringList.add(subItem.getText());
        }

        return String.valueOf(item.getTitle()+"(") + String.join(", ", stringList)+")";
    }

    public static long calculateTimeBetweenTwoDate(NoteItem noteItem){
        Calendar calendar = Calendar.getInstance();
        Calendar calendarEnd = Calendar.getInstance();

        calendarEnd.setTime(noteItem.getDateNotify());
        calendarEnd.set(Calendar.HOUR_OF_DAY, Integer.parseInt(noteItem.getTimeNotify().split(":")[0]));
        calendarEnd.set(Calendar.MINUTE, Integer.parseInt(noteItem.getTimeNotify().split(":")[1]));

        long timeCountDown = calendarEnd.getTimeInMillis() - calendar.getTimeInMillis();

        return timeCountDown;
    }

    public static NoteItem findItemWithId(int id){
        for (NoteItem item : lists){
            if(id == item.getId()){
                return item;
            }
        }
        return null;
    }

    public static int findPositionItemWithId(int id){
        for (int i = 0; i < lists.size() ; i++){
            if(id == lists.get(i).getId()){
                return i;
            }
        }
        return -1;
    }

    public static void editChildrenItemId(NoteItem noteItem){
        int i = 0;
        for (ChildrenNoteItem item : noteItem.getListNotes()){
            item.setIdChildren(i++);
        }
    }

}