package com.note;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import com.note.activity.NoteActivity;
import com.note.activity.SettingsActivity;
import com.note.note.ListNoteAdapter;
import com.note.note.Nota;
import com.note.utils.FileUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Current Build 1.0.0
 *
 * Doc below is the same as Readme.md
 *
 * Here you are a To-Do application
 *
 * These ones are showed onto a list view organized by time:
 * The first one is the next you have to do
 *
 *
 * To add a new note, click on "Add Note" on the popup window.
 *
 * Notice that notification and note is created every time you
 * click on "Save Note", so even if you delete it from
 * the list view, the notification will be NOT eliminated and you
 * will received it the same!
 *
 *
 * Note are saved on a text file called as "DD-MM-YY@HH:MM",
 * so you can create only one note at a given date and time.
 *
 * If you create twice, the last one will only be considered.
 * Notice that, as said above, notification is set every time you
 * create a new note and cannot be deleted, so you will receive
 * notification about both
 *
 * Why I chose to not allow deleting notification via button click?
 * 'Cause when the notification is pushed, maybe you cannot look at
 * the phone, so you gonna do it later
 *
 * @gitHub https://github.com/Gabriele-P03/Note
 * @author GABRIELE-P03
 */
public class MainActivity extends AppCompatActivity {

    private static ImageButton showMenuButton;
    private static ListView listNote;

    public ListNoteAdapter listNoteAdapter;

    //The first one contains the offset between start of display and the one of the layout
    //The second one the height of this activity's layout
    private int offsetY, Y;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadComponents();
        try {
            loadNotes();
        } catch (IOException e) {
            e.printStackTrace();
        }

        createNotificationChannel();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void loadComponents() {

        showMenuButton = findViewById(R.id.showMenuButton);
        showMenuButton.setOnClickListener( v -> inflateMenu());
        listNote = findViewById(R.id.listNote);
    }

    /**
     * Menu Popup Window, inflated when the menu button is clicked
     * It is inflated bottom of the divider
     */
    private void inflateMenu() {
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.menu_layout, null);

        Button settingButton = popupView.findViewById(R.id.settingsButton);
        settingButton.setOnClickListener( v -> startActivity(new Intent(this, SettingsActivity.class)));

        Button noteButton = popupView.findViewById(R.id.addNoteButton);
        noteButton.setOnClickListener( v -> startActivity(new Intent(this, NoteActivity.class)));

        /*
         *   @offsetY is the offset height between the top side of the display
         *   and the divider
         *
         * @see activity_main.xml - divider
         */
        View viewDivider = findViewById(R.id.divider);
        offsetY = showMenuButton.getTop() + showMenuButton.getHeight() + viewDivider.getTop() + viewDivider.getHeight();
        Y = findViewById(R.id.main_layout).getMeasuredHeight();

        final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, Y-offsetY, true);
        popupWindow.showAtLocation(popupView, Gravity.NO_GRAVITY, 0, offsetY);
    }

    /**
     * Method called to read all notes from the directory of application
     * and set them onto list view
     *
     * They're sorted by name, notice that name represents date and time,
     * so the array is organized by time, as said below the class
     */
    private void loadNotes() throws IOException {

        ArrayList<Nota> arrayList = new ArrayList<>();

        File dir = FileUtils.getDir(getApplicationContext(), "");
        File files[] = dir.listFiles();
        Arrays.sort(files, 0 , files.length);

        for (File file : files){

            BufferedReader bufferedReader = FileUtils.getBufferedReader(getApplicationContext(), file.getName());
            String tmp;
            String date = bufferedReader.readLine(), time = bufferedReader.readLine(), title = bufferedReader.readLine(), note = "";

            while( (tmp = bufferedReader.readLine()) != null){
                note += tmp;;
            }

            Nota nota = new Nota(date, time, title, note);
            arrayList.add(nota);
        }

        listNoteAdapter = new ListNoteAdapter(arrayList, getApplicationContext());
        listNote.setAdapter(listNoteAdapter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        try {
            loadNotes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Since Android O (API 26), a channel for notifications
     * must be set to the Notification Manager
     *
     * For more details see its doc
     */
    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "NoteChannel";
            String description = "Channel for Note notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("note", name, importance);

            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{200, 100, 200, 100, 200, 100, 200});
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}