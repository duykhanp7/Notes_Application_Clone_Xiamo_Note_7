package com.example.notesapplication.bottomSheet;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.notesapplication.R;


public class ShowSheetBottomDialogNotifyNotes extends AppCompatActivity {

    TextView buttonOke;
    TextView textViewTextNotify;
    Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), android.R.color.transparent));
        this.getWindow().setFlags(2622464,
                2622464);
        setContentView(R.layout.activity_show_sheet_bottom_dialog_notify_notes);

        bundle = getIntent().getBundleExtra("bundle");

        buttonOke = findViewById(R.id.buttonOkeCloseSheetActivity);
        textViewTextNotify = findViewById(R.id.textViewNotesShowActivity);

        int id = bundle.getInt("id",0);
        String txt = bundle.getString("txt","");

        textViewTextNotify.setText(txt);
        buttonOke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAndRemoveTask();
                NotificationManagerCompat.from(getApplicationContext()).cancel(id);
            }
        });

        boolean fromButtonSKipNotification= bundle.getBoolean("fromSkipNotification",false);

        if(fromButtonSKipNotification){
            finishAndRemoveTask();
            NotificationManagerCompat.from(getApplicationContext()).cancel(id);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        finishAndRemoveTask();
        NotificationManagerCompat.from(getApplicationContext()).cancel(bundle.getInt("id"));
    }
}