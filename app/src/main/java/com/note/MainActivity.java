package com.note;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.system.Os;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.note.activity.ListViewActivity;
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
import java.util.Calendar;
import java.util.List;

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

    private ImageButton showMenuButton;
    public static ListNoteAdapter listNoteAdapter;

    private TextView titleNote, descriptionNote, scheduledTime;


    //The first one contains the offset between start of display and the one of the layout
    //The second one the height of this activity's layout
    private int offsetY, Y;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);

        loadComponents();
        try {
            loadNotes();
        } catch (IOException e) {
            e.printStackTrace();
        }

        loadNextNote();

        NotificationWorker.createNotificationChannel(getApplicationContext());
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void loadComponents() {

        showMenuButton = findViewById(R.id.showMenuButton);
        showMenuButton.setOnClickListener( v -> inflateMenu());

        titleNote = findViewById(R.id.titleMain);
        descriptionNote = findViewById(R.id.descriptionMain);
        scheduledTime = findViewById(R.id.timeScheduled);
    }

    /**
     * Menu Popup Window, inflated when the menu button is clicked
     * It is inflated bottom of the divider
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void inflateMenu() {
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.menu_layout, null);

        Button settingButton = popupView.findViewById(R.id.settingsButton);
        settingButton.setOnClickListener( v -> startActivity(new Intent(this, SettingsActivity.class)));

        Button listButton = popupView.findViewById(R.id.listButton);
        listButton.setOnClickListener( v -> startActivity(new Intent(this, ListViewActivity.class)));

        Button noteButton = popupView.findViewById(R.id.addNoteButton);
        noteButton.setOnClickListener( v -> startActivity(new Intent(this, NoteActivity.class)));

        /*
         *   @offsetY is the offset height between the top side of the display
         *   and the divider
         *
         * @see activity_main.xml - divider
         */
        View viewDivider = findViewById(R.id.dividerMain);
        offsetY = showMenuButton.getTop() + showMenuButton.getHeight() + viewDivider.getTop() + viewDivider.getHeight();
        Y = findViewById(R.id.main_layout).getMeasuredHeight();

        final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, Y, true);
        popupWindow.showAsDropDown(popupView, 0, offsetY, Gravity.CENTER);
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
    }

    /**
     * Method called to set the information, about the next note,
     * to the main view
     */
    private void loadNextNote(){
        if(listNoteAdapter.getCount() > 0){
            Nota nota = (Nota)listNoteAdapter.getItem(0);

            titleNote.setText(nota.getTitle());
            descriptionNote.setText(nota.getNote());
            scheduledTime.setText(nota.getDate() + " alle " + nota.getTime());

        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        try {
            loadNotes();
        } catch (IOException e) {
            e.printStackTrace();
        }

        loadNextNote();
    }


    /**
     * Called when user clicks on Permission button.
     * Fire the permission manager to check if the
     * app can push notification even if it isn't running
     * @param v
     */
    public void checkPermissions(View v){

        Intent intent = new Intent();
        String manufacturer = Build.MANUFACTURER;

        if ("xiaomi".equalsIgnoreCase(manufacturer)) {
            intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
        } else if ("oppo".equalsIgnoreCase(manufacturer)) {
            intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
        } else if ("vivo".equalsIgnoreCase(manufacturer)) {
            intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
        } else if ("Letv".equalsIgnoreCase(manufacturer)) {
            intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
        } else if ("Honor".equalsIgnoreCase(manufacturer) || "Huawei".equalsIgnoreCase(manufacturer)) {
            intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
        }else if("Samsung".equalsIgnoreCase(manufacturer)){
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.N)
                intent.setComponent(new ComponentName("com.samsung.android.lool", "com.samsung.android.sm.ui.battery.BatteryActivity"));
            else
                intent.setComponent(new ComponentName("com.samsung.android.sm", "com.samsung.android.sm.ui.battery.BatteryActivity"));
        }

        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if  (list.size() > 0) {
            startActivity(intent);
        }
    }

    /**
     * Called on Information button click.
     * Shows information about the author and the application
     * @param v
     */
    public void showInfo(View v){
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.info_layout, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0 ,0);
    }
}