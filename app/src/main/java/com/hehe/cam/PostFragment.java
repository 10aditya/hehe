package com.hehe.cam;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class PostFragment extends Fragment {


    private String id;
    private RecyclerView recyclerView;
    private boolean sedone = false, fedone = false, tedone = false, bedone = false;

    public PostFragment() {
        // Required empty public constructor
    }

    String Branch, Year, Role;
    List<HashMap<String, String>> hashMaps;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_post, container, false);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        Role = preferences.getString("role", "teacher");
        Year = preferences.getString("year", "");
        id = preferences.getString("id", "");
        Branch = preferences.getString("branch", "cmpn");
        recyclerView = v.findViewById(R.id.postrv);
        Log.d("ID: ", id);
        hashMaps = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Branch).child("se").child("posts");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            HashMap<String, String> hashMap;

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d("tid: ", snapshot.child("tid").getValue(String.class));
                    if (snapshot.child("tid").getValue(String.class).toString().equals(id)) {
                        hashMap = new HashMap<>();
                        hashMap.put("date", snapshot.child("date").getValue(String.class));
                        hashMap.put("imageurl", snapshot.child("imageurl").getValue(String.class));
                        hashMap.put("fileurl", snapshot.child("fileurl").getValue(String.class));
                        hashMap.put("dos", snapshot.child("dos").getValue(String.class));
                        hashMap.put("type", snapshot.child("type").getValue(String.class));
                        hashMap.put("desc", snapshot.child("desc").getValue(String.class));
                        hashMap.put("year", "se");
                        hashMap.put("branch", Branch);
                        hashMaps.add(hashMap);
                    }
                }
                sedone = true;
                hehe();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        reference = FirebaseDatabase.getInstance().getReference().child(Branch).child("te").child("posts");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            HashMap<String, String> hashMap;

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("tid").getValue(String.class).toString().equals(id)) {
                        hashMap = new HashMap<>();
                        hashMap.put("date", snapshot.child("date").getValue(String.class));
                        hashMap.put("imageurl", snapshot.child("imageurl").getValue(String.class));
                        hashMap.put("fileurl", snapshot.child("fileurl").getValue(String.class));
                        hashMap.put("dos", snapshot.child("dos").getValue(String.class));
                        hashMap.put("type", snapshot.child("type").getValue(String.class));
                        hashMap.put("desc", snapshot.child("desc").getValue(String.class));
                        hashMap.put("year", "te");
                        hashMap.put("branch", Branch);
                        hashMaps.add(hashMap);
                    }
                }
                tedone = true;
                hehe();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        reference = FirebaseDatabase.getInstance().getReference().child(Branch).child("be").child("posts");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            HashMap<String, String> hashMap;

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("tid").getValue(String.class).toString().equals(id)) {
                        hashMap = new HashMap<>();
                        hashMap.put("date", snapshot.child("date").getValue(String.class));
                        hashMap.put("imageurl", snapshot.child("imageurl").getValue(String.class));
                        hashMap.put("fileurl", snapshot.child("fileurl").getValue(String.class));
                        hashMap.put("dos", snapshot.child("dos").getValue(String.class));
                        hashMap.put("type", snapshot.child("type").getValue(String.class));
                        hashMap.put("desc", snapshot.child("desc").getValue(String.class));
                        hashMap.put("year", "be");
                        hashMap.put("branch", Branch);
                        hashMaps.add(hashMap);
                    }
                }
                bedone = true;
                hehe();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("fe").child("posts");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            HashMap<String, String> hashMap;

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("tid").getValue(String.class).toString().equals(id)) {
                        hashMap = new HashMap<>();
                        hashMap.put("date", snapshot.child("date").getValue(String.class));
                        hashMap.put("imageurl", snapshot.child("imageurl").getValue(String.class));
                        hashMap.put("fileurl", snapshot.child("fileurl").getValue(String.class));
                        hashMap.put("dos", snapshot.child("dos").getValue(String.class));
                        hashMap.put("type", snapshot.child("type").getValue(String.class));
                        hashMap.put("desc", snapshot.child("desc").getValue(String.class));
                        hashMap.put("year", "fe");
                        hashMap.put("branch", "null");
                        hashMaps.add(hashMap);
                    }
                }
                fedone = true;
                hehe();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        // Collections.sort(hashMaps, new Sort());
       /* for (HashMap<String, String> hashMap : hashMaps) {
            Log.i("hash", String.valueOf(hashMap));
        }*/
        Log.i("hash", String.valueOf(hashMaps.size()));

        return v;
    }

    class Sort implements Comparator<HashMap<String, String>> {

        @Override
        public int compare(HashMap<String, String> s1, HashMap<String, String> s2) {
            if (s1.get("date").equals(s2.get("date"))) return 0;
            return Integer.parseInt(s1.get("date")) < Integer.parseInt(s2.get("date")) ? 1 : -1;
        }
    }

    private void hehe() {
        if ((bedone && sedone && tedone) || fedone) {
            TeacherPostRVAdapter adapter = new TeacherPostRVAdapter(getContext(), hashMaps);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
        }
    }
}
