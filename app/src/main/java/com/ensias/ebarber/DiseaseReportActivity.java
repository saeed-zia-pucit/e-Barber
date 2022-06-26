package com.ensias.ebarber;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.ensias.ebarber.adapter.DiseaseAdapter;

import com.ensias.ebarber.model.Disease;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DiseaseReportActivity extends AppCompatActivity {

    private RecyclerView mdiseaseRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private DiseaseAdapter recyclerAdapter;
    private List<Disease> diseaselist;
     Button satisfied,consult;
    Disease diseases1;
    private static final String TAG = "DiseaseReport";

    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disease_report);

        satisfied=findViewById(R.id.fab);
        consult=findViewById(R.id.fab2);
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       // getSupportActionBar().setHomeButtonEnabled(true);

       // setTitle("Diseases Report");

        mdiseaseRecyclerView = (RecyclerView) findViewById(R.id.diseaseRecyclerView);
        mdiseaseRecyclerView.setHasFixedSize(false);

        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setReverseLayout(false);
        mLayoutManager.setStackFromEnd(false);

        diseaselist = new ArrayList<Disease>();
        recyclerAdapter = new DiseaseAdapter(diseaselist, DiseaseReportActivity.this);

        mdiseaseRecyclerView.setLayoutManager(mLayoutManager);
        mdiseaseRecyclerView.setAdapter(recyclerAdapter);
            try {
                Disease diseases3;

                Disease diseases2;
                Disease diseases4;
                Disease diseases5;
                SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
                int  eye=sharedPreferences.getInt("eye",0);
                int k=0;
                diseases1 = new Disease("Respiratory viral diseases","Can be treated at home","50","Respiratory viral diseases are contagious and commonly affect the upper or lower parts of your respiratory tract.");

                if(eye==70){
                     diseases3 = new Disease("Glaucoma disorder","Seek medical help","60","Glaucoma is a group of diseases that can damage the eye’s optic nerve and result in vision loss and blindness.");
                    diseaselist.add(diseases3);

                }

                else{
                    diseases3 = new Disease("Glaucoma disorder","Seek medical help","30","Glaucoma is a group of diseases that can damage the eye’s optic nerve and result in vision loss and blindness.");

                    diseaselist.add(diseases3);
                }
                int  skin=sharedPreferences.getInt("skin",0);
                if(skin==70){
                     diseases2 = new Disease("Exanthematous viral disease","Can be treated at home","75","Exanthematous viruses cause skin rashes. Many of them cause additional symptoms as well.");
                    diseaselist.add(diseases2);
                }
                else{
                     diseases2 = new Disease("Exanthematous viral disease","Can be treated at home","35","Exanthematous viruses cause skin rashes. Many of them cause additional symptoms as well.");
                    diseaselist.add(diseases2);
                }
                int  fever=sharedPreferences.getInt("fever",0);
                if(fever==70){
                     diseases4 = new Disease("COVID-19","Seek medical help if severe","70","SARS-CoV-2, the virus that causes the disease COVID-19, is a coronavirus. Coronaviruses are a large family of viruses and include viruses that cause the common cold.");
                     diseases1 = new Disease("Respiratory viral diseases","Can be treated at home","50","Respiratory viral diseases are contagious and commonly affect the upper or lower parts of your respiratory tract.");
                    diseaselist.add(diseases1);
                    diseaselist.add(diseases4);
                }
                else{

                     diseases4 = new Disease("COVID-19","Seek medical help if severe","10","SARS-CoV-2, the virus that causes the disease COVID-19, is a coronavirus. Coronaviruses are a large family of viruses and include viruses that cause the common cold.");
                    diseaselist.add(diseases4);
                }
                int  head=sharedPreferences.getInt("head",0);
                if(head==70){
                     diseases5 = new Disease("Neurologic viral diseases","You may have to seek medical help","75","Some viruses can infect the brain and surrounding tissues, causing neurologic viral diseases. This can result in a range of symptoms, including:");
                    diseaselist.add(diseases5);
                }
                else{
                     diseases5 = new Disease("Neurologic viral diseases","Can be treated with pharmasist help in most of the cases","25","Some viruses can infect the brain and surrounding tissues, causing neurologic viral diseases. This can result in a range of symptoms, including:");
                    diseaselist.add(diseases5);
                }

                //

                Collections.sort(diseaselist, new Comparator< Disease >() {
                    @Override public int compare(Disease p1, Disease p2) {
                        return Integer.valueOf(p2.getProbability())- Integer.valueOf(p1.getProbability()); // Ascending
                    }
                });

                recyclerAdapter.notifyDataSetChanged();

            } catch (Exception e) {
                e.printStackTrace();
            }
      consult.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              finish();
              startActivity(new Intent(DiseaseReportActivity.this,SearchPatActivity.class));
          }
      });
            satisfied.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //save report to firebase
                    UploadSymptoms();

                    finish();
                    startActivity(new Intent(DiseaseReportActivity.this, HomeActivity.class));
                }
            });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
    public void UploadSymptoms(){
        try {
            String patient_mail = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString().split("@")[0];
            databaseReference = FirebaseDatabase.getInstance("https://health-care-b9ce9-default-rtdb.firebaseio.com/").getReference("Patient_Table").child(patient_mail).child("Report").push();


            databaseReference.child("Title").setValue(diseases1.getTitle());
            databaseReference.child("Description").setValue(diseases1.getDescrip());
            databaseReference.child("Treatment").setValue(diseases1.getShortTreatment());
            databaseReference.child("Probability").setValue(diseases1.getProbability());

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}