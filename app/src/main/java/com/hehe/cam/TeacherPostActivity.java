package com.hehe.cam;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class TeacherPostActivity extends AppCompatActivity implements View.OnClickListener {

    private static String DOS = "";
    private Spinner typeSpinner;
    private String type;
    private static TextView dos;
    private TextView fileName;
    private TextView desc;
    private Button chooseImage, chooseFile;
    private ImageView imageView;
    private LinearLayout mediaLayout;
    private Uri file = null;
    private File ImageFile = null, docfile = null;
    private CheckBox se, te, be, fe;
    private FloatingActionButton postButton;
    private UploadTask uploadTask;
    private Uri imageURi;
    private String ImageDownloadURL = null;
    private Uri fileURI;
    private String fileDowloadURL = null, branch, year, role, id;
    private int count;
    private boolean fileUploaded = false;
    private boolean imageUploaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_post);
        initializeViews();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        role = preferences.getString("role", "teacher");
        year = preferences.getString("year", "");
        id = preferences.getString("id", "");
        branch = preferences.getString("branch", "cmpn");
        Log.i("id : ", id);
        Log.i("role : ", role);
        Log.i("year : ", year);
        Log.i("branch : ", branch);
        typeSpinner.setPrompt("Type");
        getSupportActionBar().setTitle("Create new post...");
        final List<String> types = new ArrayList<>();
        types.add("Assignment");
        types.add("Practical");
        types.add("Workshop");
        types.add("Competition");
        types.add("Notice");
        types.add("Announcement");
        ArrayAdapter<String> typesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
        typesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typesAdapter);
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = parent.getItemAtPosition(position).toString();
                if (type.equals("Assignment") || type.equals("Practical")) {
                    dos.setVisibility(View.VISIBLE);
                } else dos.setVisibility(View.GONE);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dos.setOnClickListener(this);
        chooseFile.setOnClickListener(this);
        chooseImage.setOnClickListener(this);
        postButton.setOnClickListener(this);

    }

    private void initializeViews() {
        se = findViewById(R.id.se);
        te = findViewById(R.id.te);
        be = findViewById(R.id.be);
        fe = findViewById(R.id.fe);
        imageView = findViewById(R.id.selected_image);
        chooseFile = findViewById(R.id.uploadFile);
        chooseImage = findViewById(R.id.uploadImage);
        typeSpinner = findViewById(R.id.typeSpinner);
        dos = findViewById(R.id.dos);
        mediaLayout = findViewById(R.id.mediaLayout);
        fileName = findViewById(R.id.fileName);
        desc = findViewById(R.id.desc);
        postButton = findViewById(R.id.post);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.uploadImage) {
            getImage();
        } else if (v.getId() == R.id.uploadFile) {
            getFile();
        } else if (v.getId() == R.id.post) {
            post();
        } else if (v.getId() == R.id.dos) {
            showDatePickerDialog(v);
        }
    }

    private void post() {

        if (file != null) {
            boolean p = uploadImage();
        }
        if (docfile != null) {
            boolean t = uploadFile();
        }
        if(file==null && docfile==null){postAgainAgain();}

    }

    private boolean uploadFile() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        final StorageReference riversRef = storageReference.child(docfile.getName());
        uploadTask = riversRef.putFile(fileURI);
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(TeacherPostActivity.this, "Post Failed, Try Again!", Toast.LENGTH_SHORT).show();
                    try {
                        throw Objects.requireNonNull(task.getException());
                    } catch (Exception e) {
                        Toast.makeText(TeacherPostActivity.this, "Post Failed, Try Again!", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }

                // Continue with the task to get the download URL
                return riversRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    fileDowloadURL = task.getResult().toString();
                    fileUploaded = true;
                    postAgain();
                } else {
                    Toast.makeText(TeacherPostActivity.this, "Post Failed, Try Again!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return fileUploaded;
    }

    private void postAgain() {
        if (docfile != null && file != null) {
            if (imageUploaded && fileUploaded) {
                postAgainAgain();
                ImageFile.delete();
            }
        } else if (docfile != null && fileUploaded) {
            postAgainAgain();
        } else if (file != null && imageUploaded) {
            postAgainAgain();
            ImageFile.delete();
        }
    }

    public Uri compressImage(String imageUri) {

        String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return Uri.fromFile(new File(filename));

    }

    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;
    }

    private String getRealPathFromURI2(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    private void postAgainAgain() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
        String formattedDate = df.format(c).replace("/", "");
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("tid", id);
        hashMap.put("date", formattedDate);
        if (ImageDownloadURL != null) {
            hashMap.put("imageurl", ImageDownloadURL);
        }
        if (fileDowloadURL != null) {
            hashMap.put("fileurl", fileDowloadURL);
        }
        if (DOS.equals("")) {
            hashMap.put("dos", DOS);
        }
        hashMap.put("type", type);
        hashMap.put("desc", desc.getText().toString());

        if (fe.isChecked()) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("fe").child("posts");
            reference.push().setValue(hashMap);
        }
        if (se.isChecked()) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(branch).child("se").child("posts");
            reference.push().setValue(hashMap);
            //hashMap.type
        }
        if (te.isChecked()) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(branch).child("te").child("posts");
            reference.push().setValue(hashMap);
            //hashMap.type
        }
        if (be.isChecked()) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(branch).child("be").child("posts");
            reference.push().setValue(hashMap);
            //hashMap.type
        }

        startActivity(new Intent(this, MainActivity.class));
    }

    private boolean uploadImage() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        final StorageReference riversRef = storageReference.child(ImageFile.getName());
        uploadTask = riversRef.putFile(file);
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(TeacherPostActivity.this, "Post Failed, Try Again!", Toast.LENGTH_SHORT).show();
                    try {
                        throw Objects.requireNonNull(task.getException());
                    } catch (Exception e) {
                        Toast.makeText(TeacherPostActivity.this, "Post Failed, Try Again!", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }

                // Continue with the task to get the download URL
                return riversRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    try {
                        imageURi = task.getResult();
                        ImageDownloadURL = imageURi.toString();
                        imageUploaded = true;
                        postAgain();
                    } catch (Exception e) {
                        Toast.makeText(TeacherPostActivity.this, "Post Failed, Try Again!", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Toast.makeText(TeacherPostActivity.this, "Post Failed, Try Again!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return imageUploaded;
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

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
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
        if (requestCode == 1000) {
            String uri = getRealPathFromURI(data.getData().toString());
            mediaLayout.setVisibility(View.VISIBLE);
            imageView.setImageBitmap(BitmapFactory.decodeFile(uri));
            Log.i("ImagePath", uri);
            file = compressImage(uri);
            ImageFile = new File(String.valueOf(file));
        } else if (requestCode == 2000) {
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

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @SuppressLint("DefaultLocale")
        public void onDateSet(DatePicker view, int year, int month, int day) {
            TeacherPostActivity.dos.setText(String.format("%d/%d/%d", day, month, year));
            TeacherPostActivity.DOS = String.format("%d%d%d", year, month, day);
            // Do something with the date chosen by the user
        }
    }
}
