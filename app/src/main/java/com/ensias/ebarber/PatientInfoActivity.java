package com.ensias.ebarber;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
//

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
//
import com.ensias.ebarber.Common.Common;
import com.ensias.ebarber.model.Upload;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import static com.ensias.ebarber.Common.Common.convertBloodToInt;

public class PatientInfoActivity extends BaseActivity {

    EditText heightBtn;
    EditText weightBtn;
    Spinner bloodtypeSpinner;
    Button updateBtn,show,TestReport;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ProgressBar mProgressBar;
    private Uri mImageUri;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_info);
        updateBtn = findViewById(R.id.updateInfoBtn);
        heightBtn = findViewById(R.id.heightBtn);
        weightBtn = findViewById(R.id.weightBtn);
        TestReport=findViewById(R.id.test_report);

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance(getString(R.string.database_url)).getReference("uploads");

        final Spinner specialiteList = (Spinner) findViewById(R.id.bloodType);
        ArrayAdapter<CharSequence> adapterSpecialiteList = ArrayAdapter.createFromResource(this,
                R.array.blood_spinner, android.R.layout.simple_spinner_item);
        adapterSpecialiteList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        specialiteList.setAdapter(adapterSpecialiteList);

        String patient_name = getIntent().getStringExtra("patient_name");
        String patient_email = getIntent().getStringExtra("patient_email");

        FirebaseFirestore.getInstance().collection("Patient").document(patient_email).collection("moreInfo")
                .document(patient_email).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                weightBtn.setText( ""+documentSnapshot.getString("weight"));
                heightBtn.setText( ""+documentSnapshot.getString("height"));
                if(documentSnapshot.getString("bloodType") != null)
                specialiteList.setSelection(convertBloodToInt(documentSnapshot.getString("bloodType")));
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String,Object> map = new HashMap<>();
                map.put("height",""+heightBtn.getText());
                map.put("weight",""+weightBtn.getText());
                map.put("bloodType",""+specialiteList.getSelectedItem().toString());
                Log.e("tag", "onClick: "+specialiteList.getTag() );
                FirebaseFirestore.getInstance().collection("Patient").document(patient_email).collection("moreInfo")
                        .document(patient_email).set(map);
                Toast.makeText(PatientInfoActivity.this,"Update Success!",Toast.LENGTH_SHORT).show();

            }
        });

        TestReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent=new Intent(PatientInfoActivity.this,UploadReport.class);
               intent.putExtra("patient_email",getIntent().getStringExtra("patient_email"));
               startActivity(intent);
            }
        });


       if(Common.CurrentUserType.equals("patient")){
            updateBtn.setVisibility(View.GONE);
            heightBtn.setEnabled(false);
            weightBtn.setEnabled(false);
            specialiteList.setEnabled(false);
        }
    }
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            if (mUploadTask != null && mUploadTask.isInProgress()) {
                Toast.makeText(PatientInfoActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
            } else {
                uploadFile();
            }
          //  Picasso.with(this).load(mImageUri).into(mImageView);
        }
    }
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    private void uploadFile() {
        if (mImageUri != null) {
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));
            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0);
                                }
                            }, 500);
                            //
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final Uri downloadUrl = uri;
                                    Toast.makeText(PatientInfoActivity.this, " successful", Toast.LENGTH_LONG).show();
                                    Upload upload = new Upload("report name",downloadUrl.toString());
                                    String uploadId = mDatabaseRef.push().getKey();
                                    mDatabaseRef.child(uploadId).setValue(upload);

                                }
                            });
                            //

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PatientInfoActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mProgressBar.setProgress((int) progress);
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }
    private void openImagesActivity() {
        Intent intent = new Intent(this, ImagesActivity.class);
        startActivity(intent);
    }
}
