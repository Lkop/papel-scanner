package com.lkop.qr_scanner.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import com.lkop.qr_scanner.models.CustomSpinnerItem;
import java.util.ArrayList;

public class CustomSpinnerAdapter extends ArrayAdapter<CustomSpinnerItem> {

    public CustomSpinnerAdapter(ArrayList<CustomSpinnerItem> spinner_item, Context context) {
        super(context, android.R.layout.simple_spinner_dropdown_item, spinner_item);
    }

    @Override
    public View getView(int position, View convert_view, ViewGroup parent) {
        CustomSpinnerItem spinner_item = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convert_view == null) {
            convert_view = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }

        CheckedTextView name_textview = (CheckedTextView) convert_view.findViewById(android.R.id.text1);
        name_textview.setText(spinner_item.getText());

        if (spinner_item.getId() == -1) {
            name_textview.setTextColor(Color.GRAY);
        }else{
            name_textview.setTextColor(Color.BLACK);
        }

        return convert_view;
    }

    @Override
    public View getDropDownView(int position, View convert_view, ViewGroup parent) {
        CustomSpinnerItem spinner_item = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convert_view == null) {
            convert_view = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }

        CheckedTextView name_textview = (CheckedTextView) convert_view.findViewById(android.R.id.text1);
        name_textview.setText(spinner_item.getText());

        return convert_view;
    }


}
