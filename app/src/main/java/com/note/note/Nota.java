package com.note.note;

import android.os.Build;
import android.widget.DatePicker;
import android.widget.TimePicker;
import androidx.annotation.RequiresApi;
import java.util.Calendar;

public class Nota {

    String date, time, title, note;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public Nota(DatePicker datePicker, TimePicker timePicker, String note){

        String tmp = "";

        //Adding '0' if day is less than 10
        if(datePicker.getDayOfMonth() < 10)
            tmp = "0";
        tmp += String.valueOf(datePicker.getDayOfMonth());
        this.date = tmp + "-";

        //Reset
        tmp = "";

        //Adding '0' if month is less than 10
        if(datePicker.getMonth() < 10)
            tmp = "0";
        tmp += String.valueOf(datePicker.getMonth()+1);
        this.date += tmp + "-";


        this.date += datePicker.getYear();

        //Adding '0' if hour is less than 10
        int hour = timePicker.getHour();
        tmp = (hour < 10 ? "0"+ hour : String.valueOf(hour));

        this.time = tmp + ":" + timePicker.getMinute();

        int divider = note.indexOf("\n");
        this.title = note.substring(0, divider);
        this.note = note.substring(divider+1);
    }

    public Nota(String date, String time, String title, String note){
        this.date = date;
        this.time = time;
        this.title = title;
        this.note = note;
    }

    @Override
    public String toString() {
        return getDate() + "    " + getTime() + "\n" + getTitle() + "\n" + getNote();
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }

    public String getNote() {
        return note;
    }

    /**
     * As you can read in the WorkManager documentation (or AlarmManager one),
     * the triggerDelay which must be passed is not the to-do date in millis,
     * but the delay since the moment you click on the Save Note button 'till
     * the note one. So app needs to calculate the delay (difference) between them
     *
     * @see androidx.work.WorkManager
     *
     *
     * @return
     */
    public long getDelay(){

        int year = Integer.parseInt(date.substring(6)),
                month = Integer.parseInt(date.substring(3, 5)),
                day = Integer.parseInt(date.substring(0, 2)),
                hour = Integer.parseInt(time.substring(0, 2)),
                minutes = Integer.parseInt(time.substring(3));

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month-1, day, hour, minutes);


        long delay = calendar.getTimeInMillis();
        long current = Calendar.getInstance().getTimeInMillis();
        long trigger = delay-current;
        return trigger;
    }
}
