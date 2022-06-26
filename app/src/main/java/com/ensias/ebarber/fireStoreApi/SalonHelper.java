package com.ensias.ebarber.fireStoreApi;

import com.ensias.ebarber.model.Doctor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class SalonHelper {
    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    static CollectionReference salonRef = db.collection("saloons");


    public static void deleteSalon(String email){

        salonRef.document(email).delete();
    }
}
