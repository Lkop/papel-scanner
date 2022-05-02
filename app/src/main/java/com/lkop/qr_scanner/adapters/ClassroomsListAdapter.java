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
        super(context, R.layout.classroom_list_item, classrooms);
    }

    @Override
    public View getView(int position, View convert_view, ViewGroup parent) {
        Classroom classroom = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convert_view == null) {
            convert_view = LayoutInflater.from(getContext()).inflate(R.layout.classroom_list_item, parent, false);
        }
        TextView subject_name_textview = (TextView) convert_view.findViewById(R.id.subject_name_textview_classroom_list_item);
        TextView description_name_textview = (TextView) convert_view.findViewById(R.id.description_textview_classroom_list_item);
        TextView datetime_textview = (TextView) convert_view.findViewById(R.id.datetime_textview_classroom_list_item);
        TextView type_textview = (TextView) convert_view.findViewById(R.id.type_textview_classroom_list_item);

        subject_name_textview.setText(classroom.getSubjectName());
        description_name_textview.setText(classroom.getDescription());
        datetime_textview.setText(classroom.getDate());
        type_textview.setText(classroom.getType());

        return convert_view;
    }

}
