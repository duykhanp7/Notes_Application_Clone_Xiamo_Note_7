package com.example.notesapplication;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class ShowSheetNotifyWorker extends Worker {

    private DatabaseSaveNoteItems databaseSaveNoteItems;

    public ShowSheetNotifyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        databaseSaveNoteItems = new DatabaseSaveNoteItems(context);
    }

    @NonNull
    @Override
    public Result doWork() {
        int id;String textNotify;
        id = getInputData().getInt("id",0);
        textNotify = getInputData().getString("text_notify");

        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel(getApplicationContext());
        Notification notification = createNotification(getApplicationContext(),id,textNotify);
        manager.notify(id,notification);

        databaseSaveNoteItems.updateStateOverTimeNoteItem(id,true);

        return Result.success();
    }


    public void createNotificationChannel(Context context){
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel("1111","ABCD",NotificationManager.IMPORTANCE_HIGH);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        manager.createNotificationChannel(channel);
    }

    public Notification createNotification(Context context,int id,String txt){

        Bundle bundle = new Bundle();
        bundle.putInt("id",id);
        bundle.putString("txt",txt);

        Intent intent = new Intent(context,ShowSheetBottomDialogNotifyNotes.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("bundle",bundle);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getActivity(context,1000,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        //PENDING INTENT FOR BUTTON SKIP NOTIFICATION
        Intent intentSkipNotification = new Intent(getApplicationContext(),ShowSheetBottomDialogNotifyNotes.class);
        Bundle bundleSkipNotification = new Bundle();
        bundleSkipNotification.putBoolean("fromSkipNotification",true);
        bundleSkipNotification.putInt("id",id);
        bundleSkipNotification.putString("txt",txt);
        intentSkipNotification.putExtra("bundle",bundleSkipNotification);
        PendingIntent pendingIntentSkipNotification = PendingIntent.getActivity(getApplicationContext(),9997,intentSkipNotification,PendingIntent.FLAG_CANCEL_CURRENT);


        @SuppressLint("RemoteViewLayout") RemoteViews notificationLayoutCustom = new RemoteViews(context.getPackageName(), R.layout.custom_notification_layout);
        notificationLayoutCustom.setOnClickPendingIntent(R.id.buttonSkipNotification,pendingIntentSkipNotification);

        Intent intentAction = new Intent(context,ShowSheetBottomDialogNotifyNotes.class);
        intentAction.putExtra("bundle",bundle);
        intentAction.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntentAction = PendingIntent.getActivity(context, Integer.parseInt("100"),intentAction,PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder notification = new NotificationCompat.Builder(context,"1111")
                .setSmallIcon(R.mipmap.ic_notes_mimap_foreground)
                .setCustomContentView(notificationLayoutCustom) // CUSTOM LAYOUT CHO THÔNG BÁO
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setPriority(NotificationCompat.PRIORITY_HIGH)// thử dùng NotificationCompatManager và NotificationManager????
                .setCategory(NotificationCompat.CATEGORY_ALARM) // thử dùng CATEGORY_CALL và các các khác thử xem thế nào???
                .setFullScreenIntent(pendingIntent,true) //SET FULLSCREEN
                .setAutoCancel(true)
                .setContentIntent(pendingIntentAction)
                .setOngoing(true);

        return notification.build();
    }

}
