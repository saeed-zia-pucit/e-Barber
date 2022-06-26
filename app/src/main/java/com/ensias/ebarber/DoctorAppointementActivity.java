package com.ensias.ebarber;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.ensias.ebarber.Interface.AdapterListener;
import com.ensias.ebarber.adapter.DoctorAppointementAdapter;
import com.ensias.ebarber.model.ApointementInformation;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DoctorAppointementActivity extends BaseActivity implements AdapterListener {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference doctorAppointementRef = db.collection("Doctor");
    private DoctorAppointementAdapter adapter;
    View noRecordFound;
    ConstraintLayout parentLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_appointement);
        noRecordFound = findViewById(R.id.no_record_found_container);
        parentLayout =(ConstraintLayout) findViewById(R.id.parent_layout);
        setUpRecyclerView();

    }

    public void setUpRecyclerView(){
        //Get the doctors by patient id
        final String doctorID = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
        Query query = doctorAppointementRef.document(""+doctorID+"")
                .collection("apointementrequest")
                .orderBy("time", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ApointementInformation> options = new FirestoreRecyclerOptions.Builder<ApointementInformation>()
                .setQuery(query, ApointementInformation.class)
                .build();

        adapter = new DoctorAppointementAdapter(options,this);


        //ListMyDoctors
        RecyclerView recyclerView = findViewById(R.id.DoctorAppointement);
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

    @Override
    public void isRecordFound(boolean status) {
        if (adapter.getItemCount()!=0) {
            noRecordFound.setVisibility(View.GONE);
        }else {
            noRecordFound.setVisibility(View.VISIBLE);
        }
    }
}
