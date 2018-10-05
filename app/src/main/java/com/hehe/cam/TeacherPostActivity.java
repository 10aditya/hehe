package com.hehe.cam;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TeacherPostActivity extends AppCompatActivity implements View.OnClickListener {

    private Spinner typeSpinner;
    private String type;
    private TextView dos, fileName, desc;
    private Button chooseImage, chooseFile;
    private ImageView imageView;
    private LinearLayout mediaLayout;
    private Uri file=null;
    private File ImageFile=null,docfile=null;
    private CheckBox se, te, be;
    private FloatingActionButton postButton;
    private UploadTask uploadTask;
    private Uri imageURi;
    private String ImageDownloadURL;
    private Uri fileURI;
    private String fileDowloadURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_post);
        initializeViews();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        final List<String> types = new ArrayList<>();
        types.add("Assignment");
        types.add("Practical");
        types.add("Workshop");
        types.add("Competition");
        types.add("Notice");
        ArrayAdapter<String> typesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
        typesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typesAdapter);
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = parent.getItemAtPosition(position).toString();
                if (type.equals("Assignment") || type.equals("Practical")) {
                    dos.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        chooseFile.setOnClickListener(this);
        chooseImage.setOnClickListener(this);
        postButton.setOnClickListener(this);
    }

    private void initializeViews() {
        se = findViewById(R.id.se);
        te = findViewById(R.id.te);
        be = findViewById(R.id.be);
        imageView = findViewById(R.id.selected_image);
        chooseFile = findViewById(R.id.uploadFile);
        chooseImage = findViewById(R.id.uploadImage);
        typeSpinner = findViewById(R.id.typeSpinner);
        dos = findViewById(R.id.dos);
        mediaLayout = findViewById(R.id.mediaLayout);
        fileName = findViewById(R.id.fileName);
        desc = findViewById(R.id.desc);
        postButton.findViewById(R.id.post);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.uploadImage) {
            getImage();
        } else if (v.getId() == R.id.uploadFile) {
            getFile();
        } else if (v.getId() == R.id.post) {
            post();
        }
    }

    private void post() {
        if(file!=null){
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            final StorageReference riversRef = storageReference.child(ImageFile.getName());
            uploadTask = riversRef.putFile(file);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        Toast.makeText(TeacherPostActivity.this, "Post Failed, Try Again!", Toast.LENGTH_SHORT).show();
                        throw Objects.requireNonNull(task.getException());
                    }

                    // Continue with the task to get the download URL
                    return riversRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        imageURi = task.getResult();
                        ImageDownloadURL = imageURi.toString();
                    }
                }
            });
        }
        if(docfile!=null){
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            final StorageReference riversRef = storageReference.child(docfile.getName());
            uploadTask = riversRef.putFile(fileURI);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        Toast.makeText(TeacherPostActivity.this, "Post Failed, Try Again!", Toast.LENGTH_SHORT).show();
                        throw Objects.requireNonNull(task.getException());
                    }

                    // Continue with the task to get the download URL
                    return riversRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        fileDowloadURL = task.getResult().toString();
                    }
                }
            });
        }

    }

    private void getFile() {
        String[] mimeTypes =
                {"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                        "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                        "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                        "text/plain",
                        "application/pdf"};
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        StringBuilder mimeTypesStrBuilder = new StringBuilder();
        for (String mimeType : mimeTypes) {
            mimeTypesStrBuilder.append(mimeType).append("|");
        }
        String mimeTypesStr = mimeTypesStrBuilder.toString();
        intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
        startActivityForResult(Intent.createChooser(intent, "Choose File"), 2000);

    }

    private void getImage() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(i, "Choose Image"), 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            Toast.makeText(TeacherPostActivity.this, "Action Failed, Try Again!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (data.getData() == null) {
            Toast.makeText(TeacherPostActivity.this, "Action Failed, Try Again!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (resultCode == 1000) {
            String uri = getRealPathFromURI(data.getData().toString());
            mediaLayout.setVisibility(View.VISIBLE);
            imageView.setImageBitmap(BitmapFactory.decodeFile(uri));
            Log.i("ImagePath", uri);
            ImageFile = new File(uri);
            file = Uri.parse(uri);
        } else if (resultCode == 2000) {
            // Get the Uri of the selected file
            fileURI = data.getData();
            //Log.d(TAG, "File Uri: " + uri.toString());
            // Get the path
            String path = null;
            try {
                path = TeacherPostActivity.getPath(this, fileURI);
                docfile = new File(path);
                fileName.setText(docfile.getName());
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(TeacherPostActivity.this, "Action Failed, Try Again!", Toast.LENGTH_SHORT).show();

            }
            //Log.d(TAG, "File Path: " + path);
            // Get the file instance
            // File file = new File(path);
            // Initiate the upload

        }
    }

    @SuppressLint("Recycle")
    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        @SuppressLint("Recycle")
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }
}
