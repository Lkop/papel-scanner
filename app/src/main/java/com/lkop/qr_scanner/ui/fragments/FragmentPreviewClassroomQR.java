package com.lkop.qr_scanner.ui.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lkop.qr_scanner.constants.DefineURLS;
import com.example.lkop.qr_scanner.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class FragmentPreviewClassroomQR extends Fragment {

    String classroom_token;

    public FragmentPreviewClassroomQR() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        classroom_token = args.getString("classroom_token");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_preview_classroom_qr, container, false);

        ImageView img_view = (ImageView)view.findViewById(R.id.image_view_preview_qr);


        Picasso.get().load(DefineURLS.EXTERNAL_GET_QR+"?size=900x900&data="+classroom_token).into(img_view, new Callback() {
            @Override
            public void onSuccess() {

                TextView loading_msg = (TextView)view.findViewById(R.id.preview_loading_message);
                loading_msg.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {

            }
        });

        return view;
    }

}
