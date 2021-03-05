package com.note.note;

import android.content.ClipData;
import android.content.ClipboardManager;
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
import android.widget.Toast;

import androidx.work.WorkManager;

import com.note.R;
import java.io.File;
import java.util.ArrayList;

/**
 * Custom List Adapter
 *
 * This one contains the information of any nota, showing them onto the main layout.
 *
 * R.layout#list_view_with_button is inflated, with its delete button, user can delete
 * the relative note . Notice that notification cannot be eliminated
 *
 * @see com.note.MainActivity or README on gitHub
 *
 * @see R.layout#list_view_with_button
 */
public class ListNoteAdapter extends BaseAdapter implements ListAdapter {

    private ArrayList<Nota> arrayList;
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

        //When user click on a text view, text will be saved into clipboard
        textView.setOnClickListener( v -> {
            ClipboardManager clipboardManager = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("Note clipboard", textView.getText().toString());
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(context, "Testo copiato negli appunti", Toast.LENGTH_SHORT).show();
        });

        ImageButton deleteButton = view.findViewById(R.id.deleteNote);

        deleteButton.setOnClickListener( v -> {

            LayoutInflater layoutInflater = (LayoutInflater)parent.getContext().getSystemService(parent.getContext().LAYOUT_INFLATER_SERVICE);
            View popupView = layoutInflater.inflate(R.layout.delete_popup_window, parent, false);

            Button decline = popupView.findViewById(R.id.deleteDeclineButton);

            /**
             * In order to allow the user to  delete any note voluntarily,
             * a confirming popup window will appear
             */
            PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

            Button agreed = popupView.findViewById(R.id.deleteAgreeButton);

            agreed.setOnClickListener(v1 ->{

                File file = new File(context.getFilesDir(),
                        arrayList.get(position).date + "@" + arrayList.get(position).time + ".txt");



                arrayList.remove(position);

                //Removing scheduled notification
                removeNotificationScheduled(file.getName(), this.context);

                //Removing file
                if(file.exists())
                    file.delete();

                popupWindow.dismiss();

                //Update the list view to show the new note without needing to restart the application
                this.notifyDataSetChanged();
            });

            decline.setOnClickListener(vDecline -> {
                popupWindow.dismiss();
            });

            popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
        });

        return view;
    }

    public static void removeNotificationScheduled(String fileName, Context context) {

        WorkManager workManager = WorkManager.getInstance(context);
        workManager.cancelAllWorkByTag(fileName);
    }

}
