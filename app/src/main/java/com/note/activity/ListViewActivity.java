package com.note.activity;

import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.note.MainActivity;
import com.note.R;

public class ListViewActivity extends AppCompatActivity {

    private static ListView listNote;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        listNote = findViewById(R.id.listNote);
        listNote.setAdapter(MainActivity.listNoteAdapter);
    }
}
