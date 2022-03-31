package com.example.notesapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseSaveNoteItems extends SQLiteOpenHelper {

    //DATABASE NAME AND VERSION
    public static String DATABASE_NAME_NOTE_ITEM = "NOTE_ITEM_DATABASE";
    public static int VERSION = 2;

    //TABLE NOTE ITEM
    public static String TABLE_NAME_NOTE_ITEM = "NOTE_ITEM_TABLE";
    public static String id = "id";
    public static String title = "title";
    public static String timeNotifyNote = "timeNotifyNote";
    public static String isExpandable = "isExpandable"; // 0 or 1 : 1 = true, 0 = false
    public static String isChecked = "isChecked";       // 0 or 1 : 1 = true, 0 = false
    public static String numberItemCheck = "numberItemCheck";
    public static String dateNotify = "dateNotify";
    public static String timeNotify = "timeNotify";
    public static String isOverTime = "isOverTime";     // 0 or 1 : 1 = true, 0 = false

    //TABLE CHILDREN ITEM
    public static String TABLE_NAME_CHILDREN_ITEM = "TABLE_NAME_CHILDREN_ITEM";
    public static String idChildren = "idChildren";
    public static String idParent = "idParent";
    public static String textChildren = "text";
    public static String isCheckedChildren = "isChecked";// 0 or 1 : 1 = true, 0 = false

    //CONSTRUCTOR
    public DatabaseSaveNoteItems(@Nullable Context context) {
        super(context, DATABASE_NAME_NOTE_ITEM, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //CREATE TABLE TO SAVE NOTE ITEM
        String sqlCreateTableParent =
                                String.format("CREATE TABLE IF NOT EXISTS %s (" +
                                        " %s INTEGER NOT NULL PRIMARY KEY, " +      //ID
                                        " %s TEXT, " +                              //TITLE
                                        " %s TEXT, " +                             //TIME NOTIFY
                                        " %s TEXT, " +                             //EXPANDABLE
                                        " %s TEXT, " +                             //CHECKED
                                        " %s TEXT, " +                             //NUMBER OF CHECKED
                                        " %s TEXT, " +                             //DATE NOTIFY
                                        " %s TEXT, " +                             //TIME NOTIFY
                                        " %s TEXT )"                               //OVER TIME
                                        ,TABLE_NAME_NOTE_ITEM,id,title,timeNotifyNote,isExpandable,
                                                    isChecked,numberItemCheck,dateNotify,timeNotify,isOverTime);

        //CREATE TABLE TO SAVE CHILDREN ITEM
        String sqlCreateTableChildren =
                                String.format("CREATE TABLE IF NOT EXISTS %s(" +
                                        " %s TEXT, " +
                                        " %s TEXT, " +
                                        " %s TEXT, " +
                                        " %s TEXT )",TABLE_NAME_CHILDREN_ITEM,idParent,idChildren,
                                                    textChildren,isCheckedChildren);
        sqLiteDatabase.execSQL(sqlCreateTableParent);
        sqLiteDatabase.execSQL(sqlCreateTableChildren);
    }

    //INSERT NEW NOTE ITEM AND CHILDREN ITEM TO DATABASE
    public void insertItemToDatabase(NoteItem noteItem){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        //SAVE NOTE ITEM TO DATABASE FIRST
        new Thread(new Runnable() {
            @Override
            public void run() {
                ContentValues contentValuesNoteItem = new ContentValues();
                contentValuesNoteItem.put(id,String.valueOf(noteItem.getId()));
                contentValuesNoteItem.put(title,String.valueOf(noteItem.getTitle()));
                contentValuesNoteItem.put(timeNotifyNote,String.valueOf(noteItem.getTimeNotifyNote()));
                contentValuesNoteItem.put(isExpandable,String.valueOf(noteItem.getExpandable() ? 1 : 0));
                contentValuesNoteItem.put(isChecked,String.valueOf(noteItem.getChecked() ? 1 : 0));
                contentValuesNoteItem.put(numberItemCheck,String.valueOf(noteItem.getNumberItemCheck()));
                contentValuesNoteItem.put(dateNotify, String.valueOf(noteItem.getDateNotify()));
                contentValuesNoteItem.put(timeNotify,String.valueOf(noteItem.getTimeNotify()));
                contentValuesNoteItem.put(isOverTime,String.valueOf(noteItem.isOverTime() ? 1 : 0));
                sqLiteDatabase.insert(TABLE_NAME_NOTE_ITEM,null,contentValuesNoteItem);
            }
        }).start();

        //SAVE CHILDREN ITEM TO DATABASE SECONDS
        new Thread(new Runnable() {
            @Override
            public void run() {
                ContentValues contentValuesChildrenItem = new ContentValues();
                for (ChildrenNoteItem item : noteItem.getListNotes()){
                    contentValuesChildrenItem.put(idParent,noteItem.getId());
                    contentValuesChildrenItem.put(idChildren,item.getIdChildren());
                    contentValuesChildrenItem.put(textChildren,item.getText());
                    contentValuesChildrenItem.put(isCheckedChildren,item.isChecked());
                    sqLiteDatabase.insert(TABLE_NAME_CHILDREN_ITEM,null,contentValuesChildrenItem);
                }
            }
        }).start();
    }


    //DELETE NOTE ITEM WHICH SWIPED BY USERS
    public void deleteNoteItem(int idItem){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        new Thread(new Runnable() {
            @Override
            public void run() {
                sqLiteDatabase.delete(TABLE_NAME_NOTE_ITEM,id+"=?",new String[]{String.valueOf(idItem)});
                sqLiteDatabase.delete(TABLE_NAME_CHILDREN_ITEM,idParent+"=?",new String[]{String.valueOf(idItem)});
            }
        }).start();
    }


    //UPDATE NOTE ITEM
    public void updateNoteItem(NoteItem noteItem){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        //UPDATE NOTE ITEM
        new Thread(new Runnable() {
            @Override
            public void run() {
                ContentValues contentValuesUpdateParent = new ContentValues();
                contentValuesUpdateParent.put(title,String.valueOf(noteItem.getTitle()));
                contentValuesUpdateParent.put(timeNotifyNote,String.valueOf(noteItem.getTimeNotifyNote()));
                contentValuesUpdateParent.put(isExpandable,String.valueOf(noteItem.getExpandable() ? 1 : 0));
                contentValuesUpdateParent.put(isChecked,String.valueOf(noteItem.getChecked() ? 1 : 0));
                contentValuesUpdateParent.put(numberItemCheck,String.valueOf(noteItem.getNumberItemCheck()));
                contentValuesUpdateParent.put(dateNotify, String.valueOf(noteItem.getDateNotify()));
                contentValuesUpdateParent.put(timeNotify,String.valueOf(noteItem.getTimeNotify()));
                contentValuesUpdateParent.put(isOverTime,String.valueOf(noteItem.isOverTime() ? 1 : 0));
                sqLiteDatabase.update(TABLE_NAME_NOTE_ITEM,contentValuesUpdateParent,id+"=?",new String[]{String.valueOf(noteItem.getId())});
            }
        }).start();

        //UPDATE CHILDREN ITEM
        new Thread(new Runnable() {
            @Override
            public void run() {
                ContentValues contentValuesUpdateChildren = new ContentValues();
                sqLiteDatabase.delete(TABLE_NAME_CHILDREN_ITEM,idParent+"=?",new String[]{String.valueOf(noteItem.getId())});
                for (ChildrenNoteItem item : noteItem.getListNotes()){
                    contentValuesUpdateChildren.put(idParent,noteItem.getId());
                    contentValuesUpdateChildren.put(idChildren,item.getIdChildren());
                    contentValuesUpdateChildren.put(textChildren,item.getText());
                    contentValuesUpdateChildren.put(isCheckedChildren,item.isChecked());
                    sqLiteDatabase.insert(TABLE_NAME_CHILDREN_ITEM,null,contentValuesUpdateChildren);
                }
            }
        }).start();
    }


    //UPDATE STATE OVERTIME OF NOTE ITEM (1 is true, 0 is false)
    public void updateStateOverTimeNoteItem(int idItem,boolean state){
        Log.i("AAA","UPDATE STATE ID : "+idItem);
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        new Thread(new Runnable() {
            @Override
            public void run() {
                ContentValues contentValues = new ContentValues();
                contentValues.put(isOverTime,String.valueOf(state ? 1 : 0));
                sqLiteDatabase.update(TABLE_NAME_NOTE_ITEM,contentValues,id+"=?",new String[]{String.valueOf(idItem)});
            }
        }).start();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //DO NOTHING
    }
}
