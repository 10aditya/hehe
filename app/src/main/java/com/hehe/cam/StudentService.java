package com.hehe.cam;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StudentService extends Service {

    private static String channel_id = "1010101", branch, year, role, id;
    ;
    private int last;
    SharedPreferences.Editor spe;
    private int count;
    private SharedPreferences preferences;

    @Override
    public void onCreate() {
        super.onCreate();
//        Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        spe = preferences.edit();
        role = preferences.getString("role", "teacher");
        year = preferences.getString("year", "");
        id = preferences.getString("id", "");
        branch = preferences.getString("branch", "cmpn");
        last = preferences.getInt("last", 0);
        count = 0;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(branch).child(year).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                count =
                        (int) dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        reference.child(branch).child(year).child("posts").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String s) {

                count++;

                if (last < count) {
                    spe = preferences.edit();
                    Intent i = new Intent(StudentService.this, MainActivity.class);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(StudentService.this, channel_id)
                            .setSmallIcon(R.drawable.notification_icon)
                            .setContentTitle("New " + snapshot.child("type").getValue(String.class) + "...!!")
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                            .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                            // .setVibrate(new long[]{100})
                            //    .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                            .setAutoCancel(true)
                            .setContentIntent(PendingIntent.getActivity(StudentService.this, 0, i, 0));
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(StudentService.this);

                    synchronized (notificationManager) {
                        notificationManager.notify();
                    }

                    spe.putInt("last", count);
                    spe.apply();
                    notificationManager.notify(10001, builder.build());// notificationId is a unique int for each notification that you must define
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
