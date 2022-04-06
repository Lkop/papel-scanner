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

    private TextView textview_subject_name;
    private TextView textview_date;
    private TextView textview_type;

    public ClassroomsListAdapter(ArrayList<Classroom> data, Context context) {
        super(context, R.layout.custom_list_item, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        Classroom classroom_info = getItem(position);

        View view = convertView;

        if (view == null) {

            //viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            //LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.custom_list_item, parent, false);
        }


        //Initialize TextViews
        textview_subject_name = (TextView)view.findViewById(R.id.custom_list_item_subject_name);
        textview_date = (TextView)view.findViewById(R.id.custom_list_item_date);
        textview_type = (TextView)view.findViewById(R.id.custom_list_item_type);


            //convertView.setTag(viewHolder);

        //} else {
            //viewHolder = (ViewHolder) convertView.getTag();
        //}

        textview_subject_name.setText(classroom_info.getSubjectName());
        textview_date.setText(classroom_info.getDate());
        textview_type.setText(classroom_info.getType());

        // Return the completed view to render on screen
        return view;
    }

}
