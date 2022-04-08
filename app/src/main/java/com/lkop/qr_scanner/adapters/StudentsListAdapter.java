package com.lkop.qr_scanner.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.lkop.qr_scanner.R;
import com.lkop.qr_scanner.models.Student;
import java.util.ArrayList;

public class StudentsListAdapter extends ArrayAdapter<Student> {

    public StudentsListAdapter(ArrayList<Student> students, Context context) {
        super(context, R.layout.student_list_item, students);
    }

    @Override
    public View getView(int position, View convert_view, ViewGroup parent) {
        Student student = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convert_view == null) {
            convert_view = LayoutInflater.from(getContext()).inflate(R.layout.student_list_item, parent, false);
        }
        TextView name_textview = (TextView) convert_view.findViewById(R.id.student_name_textview);
        TextView lastname_textview = (TextView) convert_view.findViewById(R.id.student_lastname_textview);
        TextView am_textview = (TextView) convert_view.findViewById(R.id.student_am_textview);

        name_textview.setText(student.getName());
        lastname_textview.setText(student.getLastname());
        am_textview.setText(student.getAM() + "");

        return convert_view;
    }

}
