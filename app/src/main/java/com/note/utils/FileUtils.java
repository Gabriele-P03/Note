package com.note.utils;


import android.content.Context;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.Buffer;

public class FileUtils {

    public static File getDir(Context context, String dirName){
        File file = new File(context.getFilesDir(), dirName);
        if(!file.exists())
            file.mkdir();

        return file;
    }

    public static File getFile(Context context, String fileName){
        File file = new File(context.getFilesDir(), fileName);
        if(!file.exists()){
            try {
                if (file.createNewFile()) {
                    Toast.makeText(context.getApplicationContext(), "File has been created inside " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
                }
            } catch (IOException e) {
                Toast.makeText(context.getApplicationContext(), "File hasn't been created", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
        return file;
    }

    public static BufferedReader getBufferedReader(Context context, String fileName) throws FileNotFoundException {
        return new BufferedReader(new FileReader(getFile(context, fileName)));
    }

    public static BufferedWriter getBufferedWriter(Context context, String fileName) throws IOException {
        return new BufferedWriter(new FileWriter(getFile(context, fileName)));
    }
}
