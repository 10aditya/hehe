package com.hehe.cam;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class StudentFeedFragment extends Fragment {


    private String id;

    public StudentFeedFragment() {
        // Required empty public constructor
    }

    String Branch, Year, Role;
    List<HashMap<String,String>> hashMaps = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_student_feed, container, false);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        Role = preferences.getString("role", "teacher");
        Year = preferences.getString("year", "");
        id = preferences.getString("id", "");
        Branch = preferences.getString("branch", "cmpn");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(Branch);
        if (Branch.equals("fe")) {
            databaseReference = databaseReference.child("posts");
        } else {
            databaseReference = databaseReference.child(Year).child("posts");
        }

        Query query = databaseReference.getRef().orderByChild("date");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("date", dataSnapshot1.child("date").getValue(String.class));
                    hashMap.put("imageurl", dataSnapshot1.child("imageurl").getValue(String.class));
                    hashMap.put("fileurl", dataSnapshot1.child("fileurl").getValue(String.class));
                    hashMap.put("dos", dataSnapshot1.child("dos").getValue(String.class));
                    hashMap.put("type", dataSnapshot1.child("type").getValue(String.class));
                    hashMap.put("desc", dataSnapshot1.child("desc").getValue(String.class));
                    hashMaps.add(hashMap);
                }
                RecyclerView recyclerView = v.findViewById(R.id.student_feed_rv);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                recyclerView.setAdapter(new TeacherPostRVAdapter(getContext(), hashMaps));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return v;
    }

}
