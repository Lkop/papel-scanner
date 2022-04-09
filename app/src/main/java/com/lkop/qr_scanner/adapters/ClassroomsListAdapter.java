package com.lkop.qr_scanner.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.lkop.qr_scanner.R;
import com.lkop.qr_scanner.models.Classroom;
import java.util.ArrayList;

public class ClassroomsListAdapter extends ArrayAdapter<Classroom> {

    public ClassroomsListAdapter(ArrayList<Classroom> classrooms, Context context) {
        super(context, R.layout.custom_list_item, classrooms);
    }

    @Override
    public View getView(int position, View convert_view, ViewGroup parent) {
        Classroom classroom = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convert_view == null) {
            convert_view = LayoutInflater.from(getContext()).inflate(R.layout.custom_list_item, parent, false);
        }
        TextView subject_name_textview = (TextView) convert_view.findViewById(R.id.custom_list_item_subject_name);
        TextView date_textview = (TextView) convert_view.findViewById(R.id.custom_list_item_date);
        TextView type_textview = (TextView) convert_view.findViewById(R.id.custom_list_item_type);

        subject_name_textview.setText(classroom.getSubjectName());
        date_textview.setText(classroom.getDate());
        type_textview.setText(classroom.getType());

        return convert_view;
    }

}
