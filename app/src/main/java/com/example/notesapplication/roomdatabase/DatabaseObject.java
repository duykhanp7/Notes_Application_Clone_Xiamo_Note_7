package com.example.notesapplication.roomdatabase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.notesapplication.model.QuoteItem;
import com.example.notesapplication.utils.Utils;


@Database(entities = {QuoteItem.class},version = 1)
public abstract class DatabaseObject extends RoomDatabase {

    private static DatabaseObject databaseObject = null;

    public static synchronized DatabaseObject getInstance(Context context){
        if(databaseObject == null){
            databaseObject = Room.databaseBuilder(context.getApplicationContext(),DatabaseObject.class, Utils.database_name)
                    .allowMainThreadQueries()
                    .build();
        }
        return databaseObject;
    }

    public abstract RoomDatabaseDao roomDatabaseDao();
}
