package com.lkop.qr_scanner.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.lkop.qr_scanner.R;
import com.lkop.qr_scanner.constants.URLs;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class PreviewClassroomQRFragment extends Fragment {

    private View view;
    private String classroom_token;
    private ImageView qr_preview_imageview;

    public PreviewClassroomQRFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        classroom_token = args.getString("classroom_token");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_preview_classroom_qr, container, false);

        qr_preview_imageview = (ImageView)view.findViewById(R.id.qr_preview_imageview_preview_fragment);
        qr_preview_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        Picasso.get().load(URLs.EXTERNAL_GET_QR+"?size=900x900&data="+classroom_token).into(qr_preview_imageview, new Callback() {
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
