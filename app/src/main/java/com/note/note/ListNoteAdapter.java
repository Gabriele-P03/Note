package com.note.note;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.note.R;
import com.note.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;

public class ListNoteAdapter extends BaseAdapter implements ListAdapter {

    private ArrayList<Nota> arrayList = new ArrayList<>();
    private Context context;

    public ListNoteAdapter(ArrayList<Nota> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if(view == null){
           view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_with_button, parent, false);
        }

        TextView textView = view.findViewById(R.id.list_item);
        textView.setText(arrayList.get(position).toString());
        ImageButton deleteButton = view.findViewById(R.id.deleteNote);

        deleteButton.setOnClickListener( v -> {

            LayoutInflater layoutInflater = (LayoutInflater)parent.getContext().getSystemService(parent.getContext().LAYOUT_INFLATER_SERVICE);
            View popupView = layoutInflater.inflate(R.layout.delete_popup_window, parent, false);

            Button decline = popupView.findViewById(R.id.deleteDeclineButton);
            PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

            Button agreed = popupView.findViewById(R.id.deleteAgreeButton);

            agreed.setOnClickListener(v1 ->{

                File file = new File(context.getFilesDir(),
                        arrayList.get(position).date + "@" + arrayList.get(position).time + ".txt");

                if(file.exists())
                    file.delete();

                arrayList.remove(position);
                popupWindow.dismiss();
                this.notifyDataSetChanged();
            });

            decline.setOnClickListener(vDecline -> {
                popupWindow.dismiss();
            });

            popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
        });



        return view;
    }

}
