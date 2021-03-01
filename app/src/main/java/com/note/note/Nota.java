package com.note.note;

import android.os.Build;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.Reader;
import java.nio.Buffer;
import java.util.Calendar;
import java.util.Date;

public class Nota {

    String date, time, title, note;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public Nota(DatePicker datePicker, TimePicker timePicker){

        String tmp = "";

        //Adding '0' if the day is less than 10
        if(datePicker.getDayOfMonth() < 10)
            tmp = "0";
        tmp += String.valueOf(datePicker.getDayOfMonth());
        this.date = tmp + "-";

        //Reset
        tmp = "";

        //Adding '0' if the month is less than 10
        if(datePicker.getMonth() < 10)
            tmp = "0";
        tmp += String.valueOf(datePicker.getMonth()+1);
        this.date += tmp + "-";


        this.date += datePicker.getYear();
        this.time = timePicker.getHour() + ":" + timePicker.getMinute();
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

    public long getDelay(){

        int year = Integer.valueOf(date.substring(6)), month = Integer.valueOf(date.substring(3, 5)), day = Integer.valueOf(date.substring(0, 2)),
                hour = Integer.valueOf(time.substring(0, 2)), minutes = Integer.valueOf(time.substring(3));

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month-1, day, hour, minutes);


        long delay = calendar.getTimeInMillis();
        long current = Calendar.getInstance().getTimeInMillis();

        return delay-current;
    }
}
