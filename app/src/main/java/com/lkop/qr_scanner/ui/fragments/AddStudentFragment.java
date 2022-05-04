package com.lkop.qr_scanner.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.lkop.qr_scanner.R;
import com.google.gson.Gson;
import com.lkop.qr_scanner.models.Student;

public class AddStudentFragment extends Fragment {

    private View view;
    private TextView name_textview, lastname_textview, am_textview;
    private Student student;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_student, container, false);

        Gson gson = new Gson();
        student = gson.fromJson(getArguments().getString("StudentClass", ""), Student.class);

        name_textview = (TextView)view.findViewById(R.id.name_textview_search_fragment);
        lastname_textview = (TextView)view.findViewById(R.id.lastname_textview_search_fragment);
        am_textview = (TextView)view.findViewById(R.id.am_textview_search_fragment);

        name_textview.setText(student.getName());
        lastname_textview.setText(student.getLastname());
        am_textview.setText(student.getAM() + "");

        Button add_in_classroom_button = (Button) view.findViewById(R.id.add_button_add_student_fragment);
        add_in_classroom_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_in_classroom_button.setEnabled(false);
                Bundle bundle = new Bundle();
                //bundle.putBoolean("what", true);
                getParentFragmentManager().setFragmentResult("add_student_to_classroom_response", bundle);
            }
        });
        return view;
    }
}
