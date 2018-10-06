package com.hehe.cam;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class LectureActivity extends AppCompatActivity {
    private String branch, role, id, BRANCH = null, YEAR = null;
    private Spinner branchSpinner;
    EditText subject, start, end;
    DatabaseReference reference = null;
    private Spinner yearSpinner;
    RecyclerView recyclerView;
    List<HashMap<String, String>> hashMaps;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture);
        getSupportActionBar().setTitle("Lecture Attendance");

        hashMaps = new ArrayList<>();
        List<String> List = new ArrayList<>();
        List.add("CMPN");
        List.add("IT");
        List.add("MECH");
        List.add("CIVIL");
        List.add("EXTC");
        List.add("ETRX");

        List<String> year = new ArrayList<>();
        year.add("F.E.");
        year.add("S.E.");
        year.add("T.E.");
        year.add("B.E.");

        branchSpinner = findViewById(R.id.branchSpinner);
        yearSpinner = findViewById(R.id.yearSpinner);

        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post();
            }
        });
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, year);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);
        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                YEAR = String.valueOf(parent.getItemAtPosition(position));
                getStudents();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> branchAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, List);
        branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        branchSpinner.setAdapter(branchAdapter);
        branchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                BRANCH = (String) parent.getItemAtPosition(position);
                getStudents();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        recyclerView = findViewById(R.id.attendance_rv);
        subject = findViewById(R.id.subject);
        start = findViewById(R.id.start);
        end = findViewById(R.id.end);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        role = preferences.getString("role", "teacher");
        // year1 = preferences.getString("year", "");
        id = preferences.getString("id", "");
        branch = preferences.getString("branch", "cmpn");


    }

    private void post() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference = reference.child(BRANCH.toLowerCase()).child(YEAR.toLowerCase().replace(".", "")).child("lectures");
        HashMap<String, String> map = new HashMap<>();
        map.put("tid", id);
        map.put("start", start.getText().toString());
        map.put("end", end.getText().toString());
        map.put("subject", subject.getText().toString());

    }

    private void getStudents() {
        if (BRANCH != null && YEAR != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(BRANCH.toLowerCase().replace(".", "")).child(YEAR.toLowerCase().replace(".", "")).child("student");
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("id", snapshot.child("id").getValue(String.class));
                        hashMap.put("name", snapshot.child("name").getValue(String.class));
                        hashMaps.add(hashMap);
                    }
                    setAdapter();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void setAdapter() {
        RecyclerView.LayoutManager linearlayout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearlayout);
        recyclerView.setAdapter(new Adapter(LectureActivity.this, hashMaps, recyclerView));
    }

    private class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
        final Random mRandom = new Random(System.currentTimeMillis());

        private Context context;
        private List<HashMap<String, String>> hashMaps;
        private RecyclerView linearlayout;
        private boolean[] isPresent;

        public Adapter(Context context, List<HashMap<String, String>> hashMaps, RecyclerView linearlayout) {
            this.context = context;
            this.hashMaps = hashMaps;
            this.linearlayout = linearlayout;
            isPresent = new boolean[100];
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.attendance_card_view, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int i) {
            holder.card.setCardBackgroundColor(generateRandomColor());
            holder.name.setText(hashMaps.get(i).get("name"));
            holder.roll.setText(hashMaps.get(i).get("id").substring(6));
            holder.present.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isPresent[Integer.parseInt(holder.roll.getText().toString())] = true;
                    smoothScroller.setTargetPosition(i + 1);
                    linearlayout.getLayoutManager().startSmoothScroll(smoothScroller);
                }
            });
            holder.absent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isPresent[Integer.parseInt(holder.roll.getText().toString())] = false;
                    smoothScroller.setTargetPosition(i + 1);
                    linearlayout.getLayoutManager().startSmoothScroll(smoothScroller);
                }
            });
        }

        @Override
        public int getItemCount() {
            return hashMaps.size();
        }

        RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(LectureActivity.this) {
            @Override
            protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView name, roll, present, absent;
            CardView card;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                card = itemView.findViewById(R.id.card);
                name = itemView.findViewById(R.id.name);
                roll = itemView.findViewById(R.id.roll);
                present = itemView.findViewById(R.id.present);
                absent = itemView.findViewById(R.id.absent);
            }
        }

        public int generateRandomColor() {
            // This is the base color which will be mixed with the generated one
            final int baseColor = Color.LTGRAY;

            final int baseRed = Color.red(baseColor);
            final int baseGreen = Color.green(baseColor);
            final int baseBlue = Color.blue(baseColor);

            final int red = (baseRed + mRandom.nextInt(256)) / 2;
            final int green = (baseGreen + mRandom.nextInt(256)) / 2;
            final int blue = (baseBlue + mRandom.nextInt(256)) / 2;

            return Color.rgb(red, green, blue);
        }
    }
}
