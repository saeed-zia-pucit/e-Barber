package com.ensias.ebarber.fireStoreApi;

import androidx.annotation.NonNull;

import com.ensias.ebarber.model.Doctor;
import com.ensias.ebarber.model.Services;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class DoctorHelper {
    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    static CollectionReference DoctorRef = db.collection("Doctor");

    public static void addDoctor(String name, String adresse, String tel,String specialite){
        Doctor doctor = new Doctor(name,adresse,tel, FirebaseAuth.getInstance().getCurrentUser().getEmail(),specialite);

        DoctorRef.document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).set(doctor).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                addCommonServicesToFirebase();

            }
        });

    }
    public static void deleteDoctor(String email){

        DoctorRef.document(email).delete().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private static  void addCommonServicesToFirebase(){
        List<Services> tempList=new ArrayList<>();
        tempList.add(new Services("Hair Cut",10,"scissorse"));
        tempList.add(new Services("Cut N' Color",15,"scissorse"));
        tempList.add(new Services("Shaving",10,"razor"));
        tempList.add(new Services("Eyebrow Shaping",8,"eyebrow"));
        tempList.add(new Services("Essential Facial",10,"facial"));
//        tempList.add(new Services("Fade Only",10));
        tempList.add(new Services("Beard Trimming",10,"beard_trimming"));


        for(Services item : tempList){
            MyServiceHelper.addService(item.getServiceName(),item.getServiceCharges(),item.getImageName());
        }

    }
}
