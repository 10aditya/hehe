package com.hehe.cam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hehe.cam.models.LectureModel;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private DatabaseReference reference;
    private FloatingActionButton fab;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String role = sp.getString("role", "teacher");
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (role.equals("teacher"))
            fragmentTransaction.replace(R.id.base_frame, new TeacherFragment());
        else {
            fragmentTransaction.replace(R.id.base_frame, new StudentFragment());
            startService(new Intent(this.getBaseContext(), StudentService.class));
        }
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.commitAllowingStateLoss();

       /* Button button = findViewById(R.id.uploadday);
        final EditText day = findViewById(R.id.day);
        final EditText start = findViewById(R.id.start);
        final EditText end = findViewById(R.id.end);
        final EditText subject = findViewById(R.id.subject);
        final EditText tid = findViewById(R.id.tid);
        final EditText lec = findViewById(R.id.lecno);
        final EditText room = findViewById(R.id.room);

        reference = FirebaseDatabase.getInstance().getReference().child("cmpn").child("te");
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference.child("tt").child(day.getText().toString())
                        .child(lec.getText().toString())
                        .setValue(new LectureModel(
                                end.getText().toString(),
                                start.getText().toString(),
                                tid.getText().toString(),
                                subject.getText().toString(),
                                room.getText().toString()
                        ));
            }
        });*/
    }

}
