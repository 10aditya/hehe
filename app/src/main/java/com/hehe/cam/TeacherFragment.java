package com.hehe.cam;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class TeacherFragment extends Fragment implements View.OnClickListener{


    public TeacherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_teacher, container, false);
        BottomNavigationView navigation = v.findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.framelayout, new PostFragment());
        fragmentTransaction.commitAllowingStateLoss();
        FloatingActionButton fab = v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                View ADLayout = getLayoutInflater().inflate(R.layout.alert_layout, null);
                alertDialog.setView(ADLayout);
                ADLayout.findViewById(R.id.lecture).setOnClickListener(TeacherFragment.this);
                ADLayout.findViewById(R.id.newpost).setOnClickListener(TeacherFragment.this);
                alertDialog.show();
            }
        });
        return v;
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            switch (item.getItemId()) {
                case R.id.posts:
                    fragmentTransaction.replace(R.id.framelayout, new PostFragment());
                    return true;
                case R.id.schedule:
                    fragmentTransaction.replace(R.id.framelayout, new ScheduleFragment());
                    return true;
                case R.id.profile:

                    return true;
            }
            fragmentTransaction.commitAllowingStateLoss();
            return false;
        }
    };

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.newpost){
            startActivity(new Intent(getContext(), TeacherPostActivity.class));
        } else {
            startActivity(new Intent(getContext(), LectureActivity.class));
        }
    }
}
