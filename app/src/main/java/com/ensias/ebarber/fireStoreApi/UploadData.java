package com.ensias.ebarber.fireStoreApi;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UploadData {

    // Root Database Name for Firebase Database.
    public static final String Database_Path = "Disease_tables23";
    DatabaseReference databaseReference;
    public UploadData() {

    }
    public void UploadSymptoms(){
       try {
           final String patient_mail = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
           databaseReference = FirebaseDatabase.getInstance("https://health-care-b9ce9-default-rtdb.firebaseio.com/").getReference(Database_Path);
          // Symptom s =new Symptom();

          // databaseReference.child("patientMmail").child("Symptom").setValue(s);

       }
       catch (Exception e){
           e.printStackTrace();
       }
    }
    public void UploadReport(){
        final String patient_mail = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
        databaseReference = FirebaseDatabase.getInstance("https://health-care-b9ce9-default-rtdb.firebaseio.com/").getReference(Database_Path);
        //databaseReference.child(patient_mail).child("Symptom").setValue(new Symptom(Symptom.type,Symptom.symptom1,Symptom.symptom2,Symptom.symptom3));
    }

}
