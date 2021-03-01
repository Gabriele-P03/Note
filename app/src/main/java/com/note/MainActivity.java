package com.note;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
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
import com.note.notification.NotificationWorker;
import com.note.utils.FileUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Main Activity class
 *
 * This app let you take note of everything you have to do.
 *
 * These ones are grouped into a list view organized by time:
 * The first one is the next one you have to do
 *
 *
 *
 * @gitHub
 * @author GABRIELE-P03
 */
public class MainActivity extends AppCompatActivity {

    private LinearLayout thisLayout;
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

        //loadAutoStart();
        loadScreenSize();
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
        Y = thisLayout.getMeasuredHeight();

        final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, Y-offsetY, true);
        popupWindow.showAtLocation(popupView, Gravity.NO_GRAVITY, 0, offsetY);
    }

    /**
     * Get the screen real size and subtracts the title's bar's size
     * Then the params' layout are set to the right width and height
     */
    public static int newX;
    public static int newY;
    @RequiresApi(api = Build.VERSION_CODES.R)
    private void loadScreenSize() {
        Point point = new Point();
        thisLayout = findViewById(R.id.main_layout);
        getWindowManager().getDefaultDisplay().getRealSize(point);

        ViewGroup.LayoutParams tmp = thisLayout.getLayoutParams();

        newX  = point.x - (point.x - tmp.width);
        newY = point.y - (point.y - tmp.height);

        thisLayout.getLayoutParams().height = newY;
        thisLayout.getLayoutParams().width = newX;
    }

    /**
     * Method called to read all notes and set them into list view
     */
    private void loadNotes() throws IOException {

        ArrayList<Nota> arrayList = new ArrayList<>();

        File dir = FileUtils.getDir(getApplicationContext(), "");
        File files[] = dir.listFiles();
        File orderFiles[] = new File[files.length];
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

    private void createNotificationChannel(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "NoteChannel";
            String description = "Channel for Note notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("note", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}