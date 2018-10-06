package com.hehe.cam;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;


/**
 * A simple {@link Fragment} subclass.
 */
public class ScheduleFragment extends Fragment {


    public ScheduleFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_schedule, container, false);

        Spinner s=v.findViewById(R.id.roleSpinner1);
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(getContext(),R.array.tt,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
        Spinner s1=v.findViewById(R.id.roleSpinner2);
        ArrayAdapter<CharSequence> a=ArrayAdapter.createFromResource(getContext(),R.array.tt1,android.R.layout.simple_spinner_item);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s1.setAdapter(a);
        Spinner s2=v.findViewById(R.id.roleSpinner3);
        ArrayAdapter<CharSequence> a2=ArrayAdapter.createFromResource(getContext(),R.array.tt,android.R.layout.simple_spinner_item);
        a2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s2.setAdapter(adapter);
        Spinner s3=v.findViewById(R.id.roleSpinner4);
        ArrayAdapter<CharSequence> a3=ArrayAdapter.createFromResource(getContext(),R.array.tt1,android.R.layout.simple_spinner_item);
        a3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s3.setAdapter(adapter);
        Spinner s4=v.findViewById(R.id.roleSpinner5);
        ArrayAdapter<CharSequence> a4=ArrayAdapter.createFromResource(getContext(),R.array.tt,android.R.layout.simple_spinner_item);
        a4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s4.setAdapter(a4);
        return v;
    }

}
