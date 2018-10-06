package com.hehe.cam;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class TeacherProfileFragment extends Fragment {


    private View v;
    private String id;

    public TeacherProfileFragment() {
        // Required empty public constructor
    }

    TextView name, name_value, pos, pos_value, branch, branch_value, birth, birth_value, mob, mob_value, mail, mail_value;
    String Branch, Year, Role;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_teacher_profile, container, false);
        intitializeViews();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        Role = preferences.getString("role", "teacher");
        Year = preferences.getString("year", "");
        id = preferences.getString("id", "");
        Branch = preferences.getString("branch", "cmpn");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Branch).child("teacher").child(id);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return v;
    }

    private void intitializeViews() {
        name = v.findViewById(R.id.name);
        name_value = v.findViewById(R.id.name2);
        pos = v.findViewById(R.id.designation);
        pos_value = v.findViewById(R.id.designation_value);
        branch = v.findViewById(R.id.branch);
        branch_value = v.findViewById(R.id.cmpn);
        birth = v.findViewById(R.id.dob);
        birth_value = v.findViewById(R.id.dobv);
        mob = v.findViewById(R.id.num);
        mob_value = v.findViewById(R.id.numr);
        mail = v.findViewById(R.id.mail);
        mail_value = v.findViewById(R.id.mailv);
    }

}
