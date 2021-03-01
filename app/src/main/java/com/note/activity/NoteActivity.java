package com.note.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import com.note.R;
import com.note.note.Nota;
import com.note.notification.NotificationWorker;
import com.note.utils.FileUtils;
import java.io.BufferedWriter;
import java.io.IOException;


public class NoteActivity extends AppCompatActivity {

    private EditText note;
    private DatePicker calendarView;
    private TimePicker timePicker;
    private Button saveButton;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        loadComponents();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void loadComponents() {
        
        this.note = findViewById(R.id.note);
        this.calendarView = findViewById(R.id.calendarNote);
        this.timePicker = findViewById(R.id.timeNote);
        this.saveButton = findViewById(R.id.saveNoteButton);
        
        this.saveButton.setOnClickListener( v -> {
            try {
                saveNote();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Called when save note button is clicked
     *
     * To affors the ordering by name, it will add '0' if day/month is less than 0:
     * 01 != 0, it will check if 0 < 0...
     *
     * @throws IOException
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void saveNote() throws IOException {

        Nota nota = new Nota(this.calendarView, this.timePicker);

        String fileName = nota.getDate() + "@" + nota.getTime() + ".txt";

        BufferedWriter bufferedWriter = FileUtils.getBufferedWriter(getApplicationContext(), fileName);
        bufferedWriter.write(nota.getDate() + "\n" + nota.getTime() + "\n");
        bufferedWriter.write(this.note.getText().toString());
        bufferedWriter.flush();
        bufferedWriter.close();

        setNewNotification(nota);
    }

    private void setNewNotification(Nota nota){
        Intent intent = new Intent(this, NotificationWorker.class);
        intent.putExtra("date", nota.getDate());
        intent.putExtra("time", nota.getTime());
        intent.putExtra("title", nota.getTitle());
        intent.putExtra("note", nota.getNote());


        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);


        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, nota.getDelay(), pendingIntent);
    }
}