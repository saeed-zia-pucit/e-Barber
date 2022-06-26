package com.ensias.ebarber;

import android.content.Intent;
import android.os.Bundle;

import com.ensias.ebarber.adapter.MyPatientAdapterAdmin;
import com.ensias.ebarber.model.Patient;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MyPatientActivityAdmin extends BaseActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference myPatientsRef = db.collection("Doctor");
    private MyPatientAdapterAdmin adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_patient_admin);
        setUpRecyclerView();

    }

    public void setUpRecyclerView(){

        //final String doctorID = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
        Intent intent=getIntent();
        String doctorID=intent.getStringExtra("email");
        Query query = myPatientsRef.document(""+doctorID+"")
                .collection("MyPatients").orderBy("name", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Patient> options = new FirestoreRecyclerOptions.Builder<Patient>()
                .setQuery(query, Patient.class)
                .build();

        adapter = new MyPatientAdapterAdmin(options);
        //ListMyPatients
        RecyclerView recyclerView = findViewById(R.id.ListMyPatients);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}