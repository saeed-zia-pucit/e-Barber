package com.ensias.ebarber.fireStoreApi;

import com.ensias.ebarber.model.Services;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MyServiceHelper {

    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    static CollectionReference ServiceRef = db.collection("BarberServices");

    public static void addService(String name, int charges,String imageName) {
        Services service = new Services(name,charges,imageName);
        ServiceRef.document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).collection("MyServices").document(name).set(service);

    }

    public static void deleteService(String name) {

        ServiceRef.document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).collection("MyServices").document(name).delete();

    }
    public static void deleteAllService(String email) {

        ServiceRef.document(email).delete();

    }
}