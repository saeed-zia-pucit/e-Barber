package com.ensias.ebarber.fireStoreApi;

import com.ensias.ebarber.model.Admin;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminHelper {
    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    static CollectionReference AdminRef = db.collection("Admin");

    public static void addAdmin(String name, String adresse, String tel){
        Admin admin = new Admin(name,adresse,tel, FirebaseAuth.getInstance().getCurrentUser().getEmail());

        AdminRef.document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).set(admin);

    }
}