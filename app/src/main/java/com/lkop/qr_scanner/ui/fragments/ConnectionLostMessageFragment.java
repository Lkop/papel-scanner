package com.lkop.qr_scanner.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.lkop.qr_scanner.R;

public class ConnectionLostMessageFragment extends Fragment {

    private TextView update_message_textview, update_url;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_connection_lost_message, container, false);

        update_message_textview = (TextView) view.findViewById(R.id.update_message);
        update_url = (TextView) view.findViewById(R.id.update_url);



        return view;
    }


}
