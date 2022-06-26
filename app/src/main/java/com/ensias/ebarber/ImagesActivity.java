package com.ensias.ebarber;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;

import com.ensias.ebarber.adapter.ImageAdapter;
import com.ensias.ebarber.model.ImageUploadInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ImagesActivity extends AppCompatActivity {

    // Creating DatabaseReference.
    DatabaseReference databaseReference;

    // Creating RecyclerView.
    RecyclerView recyclerView;

    // Creating RecyclerView.Adapter.
    RecyclerView.Adapter adapter;

    // Creating Progress dialog
    ProgressDialog progressDialog;

    // Creating List of ImageUploadInfo class.
    List<ImageUploadInfo> list = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        // Assign id to RecyclerView.
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        // Setting RecyclerView size true.
        recyclerView.setHasFixedSize(true);

        // Setting RecyclerView layout as LinearLayout.
        recyclerView.setLayoutManager(new LinearLayoutManager(ImagesActivity.this));

        // Assign activity this to progress dialog.
        progressDialog = new ProgressDialog(ImagesActivity.this);

        // Setting up message in Progress dialog.
        progressDialog.setMessage("Loading Images From Firebase.");

        // Showing progress dialog.
        progressDialog.show();

        // Setting up Firebase image upload folder path in databaseReference.
        // The path is already defined in MainActivity.

        FirebaseAuth.getInstance().getCurrentUser().getMetadata();
      //  String emailId= FirebaseAuth.getInstance().getCurrentUser().getEmail().toString().split("@")[0];;
        String emailId=getIntent().getStringExtra("patient_email").toString().split("@")[0];

        databaseReference = FirebaseDatabase.getInstance("https://health-care-b9ce9-default-rtdb.firebaseio.com/").getReference(UploadReport.Database_Path).child(emailId);

        // Adding Add Value Event Listener to databaseReference.
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                    ImageUploadInfo imageUploadInfo = postSnapshot.getValue(ImageUploadInfo.class);

                    list.add(imageUploadInfo);
                }

                adapter = new ImageAdapter(getApplicationContext(), list);

                recyclerView.setAdapter(adapter);

                // Hiding the progress dialog.
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                // Hiding the progress dialog.
                progressDialog.dismiss();

            }
        });

    }
}