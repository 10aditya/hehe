package com.hehe.cam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private TextView mStatusTextView;
    private TextView mDetailTextView;
    private EditText mEmailField;
    private EditText mPasswordField;
    private ProgressBar progressBar;
    private static final String TAG = "EmailPassword";
    // [START declare_auth]
    private FirebaseAuth mAuth;
    private TextView logIn, signUp;
    private Spinner roleSpinner, branchSpinner, yearSpinner;
    private String Branch, Year, Role;
    private DatabaseReference reference;
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Views
        //progressBar = findViewById(R.id.progress);
        //progressBar.setVisibility(View.GONE);
        mEmailField = findViewById(R.id.fieldEmail);
        mPasswordField = findViewById(R.id.fieldPassword);
        initializeViews();
        List<String> role = new ArrayList<>();
        role.add("Teacher");
        role.add("Student");

        List<String> branch = new ArrayList<>();
        branch.add("CMPN");
        branch.add("IT");
        branch.add("MECH");
        branch.add("CIVIL");
        branch.add("EXTC");
        branch.add("ETRX");
        branch.add("F.E.");

        List<String> year = new ArrayList<>();
        year.add("S.E.");
        year.add("T.E.");
        year.add("B.E.");

        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, role);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(roleAdapter);

        ArrayAdapter<String> branchAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, branch);
        branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        branchSpinner.setAdapter(branchAdapter);

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, year);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);

        roleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Role = parent.getItemAtPosition(position).toString();
                if (!Branch.equals("F.E.") && Role.equals("Student")) {
                    Log.d("sd", Role);
                    yearSpinner.setVisibility(View.VISIBLE);
                } else {
                    yearSpinner.setVisibility(View.INVISIBLE);
                }
                //Log.d("sd", Role);
                // Toast.makeText(LoginActivity.this, Role, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        branchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Branch = parent.getItemAtPosition(position).toString();
                if (!Branch.equals("F.E.") && Role.equals("Student")) {
                    Log.d("sd", Role);
                    yearSpinner.setVisibility(View.VISIBLE);
                } else {
                    yearSpinner.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Year = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // Buttons
        findViewById(R.id.emailSignInButton).setOnClickListener(this);
        findViewById(R.id.emailCreateAccountButton).setOnClickListener(this);

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();

    }

    private void initializeViews() {
        roleSpinner = findViewById(R.id.roleSpinner);
        yearSpinner = findViewById(R.id.yearSpinner);
        branchSpinner = findViewById(R.id.branchSpinner);
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }


    private void createAccount(final String email, String password) {
        Log.d(TAG, "createAccount:" + email);
/*
        if (!validateForm()) {
            return;
        }
*/

        // showProgressDialog();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            addUserToDatabase(email);
                            //  DatabaseReference reference =FirebaseDatabase.getInstance().getReference().child();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        // hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }

    private void addUserToDatabase(String email) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor spe = sharedPreferences.edit();
        spe.putString("branch", Branch.toLowerCase());
        spe.putString("role", Role.toLowerCase());
        if (!Branch.equals("F.E.") && !Role.equals("Teacher")) {
            spe.putString("year", Year.toLowerCase().replace(".", ""));
        }
        spe.apply();
        if (Branch.equals("F.E.")) {
            reference = FirebaseDatabase.getInstance().getReference().child(Branch.toLowerCase().replace(".", "")).child(Role.toLowerCase());
        } else if (Role.equals("Teacher")) {
            reference = FirebaseDatabase.getInstance().getReference().child(Branch.toLowerCase().replace(".", "")).child(Role.toLowerCase());
        } else {
            reference = FirebaseDatabase.getInstance().getReference().child(Branch.toLowerCase().replace(".", "")).child(Year.toLowerCase()).child(Role.toLowerCase());
        }
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                count = (int) dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        String id;
        if (Role.equals("Teacher")) {
            id = String.format(Locale.ENGLISH, "%s%02d", Branch.toLowerCase() + "t", count + 1);
        } else {
            id = String.format(Locale.ENGLISH, "%s%02d", Branch.toLowerCase() + "s", count + 1);
        }
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("id", id);
        hashMap.put("email", email);
        reference.child(id).setValue(hashMap);
    }

    /*  private void hideProgressDialog() {
          progressBar.setVisibility(View.GONE);

      }

      private void showProgressDialog() {
          progressBar.setVisibility(View.VISIBLE);

      }
  */
    private void signIn(final String email, final String password) {
        Log.d(TAG, "signIn:" + email);
        /*if (!validateForm()) {
            return;
        }*/


        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            Toast.makeText(LoginActivity.this, "Authentication succedd.",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                            SharedPreferences.Editor spe = sharedPreferences.edit();
                            spe.putString("branch", Branch.toLowerCase());
                            spe.putString("role", Role.toLowerCase());
                            if (!Branch.equals("F.E.") && !Role.equals("Teacher")) {
                                spe.putString("year", Year.toLowerCase().replace(".", ""));
                            }
                            spe.apply();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, email + " " + password,
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {
                            // mStatusTextView.setText(R.string.auth_failed);
                        }

                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }

    /*
        private void signOut() {
            mAuth.signOut();
            updateUI(null);
        }

        private void sendEmailVerification() {
            // Disable button
            findViewById(R.id.verifyEmailButton).setEnabled(false);

            // Send verification email
            // [START send_email_verification]
            final FirebaseUser user = mAuth.getCurrentUser();
            user.sendEmailVerification()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // [START_EXCLUDE]
                            // Re-enable button
                            findViewById(R.id.verifyEmailButton).setEnabled(true);

                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this,
                                        "Verification email sent to " + user.getEmail(),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e(TAG, "sendEmailVerification", task.getException());
                                Toast.makeText(LoginActivity.this,
                                        "Failed to send verification email.",
                                        Toast.LENGTH_SHORT).show();
                            }
                            // [END_EXCLUDE]
                        }
                    });
            // [END send_email_verification]
        }

        private boolean validateForm() {
            boolean valid = true;

            String email = mEmailField.getText().toString();
            if (TextUtils.isEmpty(email)) {
                mEmailField.setError("Required.");
                valid = false;
            } else {
                mEmailField.setError(null);
            }

            String password = mPasswordField.getText().toString();
            if (TextUtils.isEmpty(password)) {
                mPasswordField.setError("Required.");
                valid = false;
            } else {
                mPasswordField.setError(null);
            }

            return valid;
        }
    */
    private void updateUI(FirebaseUser user) {
        //hideProgressDialog();
        if (user != null) {
            String email = user.getEmail();

            /*if (Branch.equals("F.E.")) {
                reference = FirebaseDatabase.getInstance().getReference().child(Branch.toLowerCase().replace(".", "")).child(Role.toLowerCase());
            } else if (Role.equals("Teacher")) {
                reference = FirebaseDatabase.getInstance().getReference().child(Branch.toLowerCase().replace(".", "")).child(Role.toLowerCase());
            } else {
                reference = FirebaseDatabase.getInstance().getReference().child(Branch.toLowerCase().replace(".", "")).child(Role.toLowerCase());
            }*/

            startActivity(new Intent(this, MainActivity.class));
            finish();
            /*mStatusTextView.setText(getString(R.string.emailpassword_status_fmt,
                    user.getEmail(), user.isEmailVerified()));
            mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));

            findViewById(R.id.emailPasswordButtons).setVisibility(View.GONE);
            findViewById(R.id.emailPasswordFields).setVisibility(View.GONE);
            findViewById(R.id.signedInButtons).setVisibility(View.VISIBLE);

            findViewById(R.id.verifyEmailButton).setEnabled(!user.isEmailVerified());
        } else {
            mStatusTextView.setText(R.string.signed_out);
            mDetailTextView.setText(null);

            findViewById(R.id.emailPasswordButtons).setVisibility(View.VISIBLE);
            findViewById(R.id.emailPasswordFields).setVisibility(View.VISIBLE);
            findViewById(R.id.signedInButtons).setVisibility(View.GONE);*/
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.emailCreateAccountButton) {
            createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.emailSignInButton) {
            signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } /*else if (i == R.id.signOutButton) {
            signOut();
        } else if (i == R.id.verifyEmailButton) {
            sendEmailVerification();
        }*/
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (id == R.id.roleSpinner) {

        } else if (id == R.id.branchSpinner) {
        } else if (id == R.id.yearSpinner) {
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}