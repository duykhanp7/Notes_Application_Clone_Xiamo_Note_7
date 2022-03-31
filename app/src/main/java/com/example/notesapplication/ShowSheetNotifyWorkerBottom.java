package com.example.notesapplication;

import static com.example.notesapplication.NotesActivityMain.fragmentManager;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.concurrent.TimeUnit;

public class ShowSheetNotifyWorkerBottom extends Worker {


    public ShowSheetNotifyWorkerBottom(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }



    @NonNull
    @Override
    public Result doWork() {

        int id = getInputData().getInt("id",0);
        String txt = getInputData().getString("text_notify");

        if(fragmentManager != null){
            BottomSheetDialogShowNotify showNotify = new BottomSheetDialogShowNotify(id,txt);
            showNotify.show(fragmentManager,showNotify.getTag());
        }
        else{
            Data data = new Data.Builder()
                    .putInt("id",id)
                    .putString("text_notify",txt)
                    .build();
            WorkRequest workRequest = new OneTimeWorkRequest.Builder(ShowSheetNotifyWorker.class)
                    .addTag("work_request")
                    .setInputData(data)
                    .build();
            WorkManager.getInstance(getApplicationContext()).enqueue(workRequest);
        }

        return Result.success();
    }
}
