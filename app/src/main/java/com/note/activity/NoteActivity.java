package com.note.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.note.MainActivity;
import com.note.R;
import com.note.note.ListNoteAdapter;
import com.note.note.Nota;
import com.note.notification.NotificationWorker;
import com.note.utils.FileUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * This class represents NoteActivity.
 *
 * Instanced when "Add Note" button is clicked
 */
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
     * To affords the ordering by name, it will add '0' if day/month is less than 0:
     * 01 != 0, it will check if 0 < 0...
     * @see Nota#Nota(DatePicker, TimePicker, String)
     *
     * @throws IOException
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void saveNote() throws IOException {

        Nota nota = new Nota(this.calendarView, this.timePicker, note.getText().toString());

        String fileName = nota.getDate() + "@" + nota.getTime() + ".txt";

        File file = new File(getApplicationContext().getFilesDir(), fileName);

        if(!file.exists()){
            file.createNewFile();
        }else{
            ListNoteAdapter.removeNotificationScheduled(fileName, getApplicationContext());
            Toast.makeText(getApplicationContext(), "Attività già istanziata. La sto cancellando...", Toast.LENGTH_LONG).show();
        }

        BufferedWriter bufferedWriter = FileUtils.getBufferedWriter(getApplicationContext(), fileName);
        bufferedWriter.write(nota.getDate() + "\n" + nota.getTime() + "\n");
        bufferedWriter.write(this.note.getText().toString());
        bufferedWriter.flush();
        bufferedWriter.close();

        Toast.makeText(getApplicationContext(), "Attività schedulata correttamente", Toast.LENGTH_SHORT).show();

        NotificationWorker.setNewNotification(nota, getApplicationContext());
    }
}