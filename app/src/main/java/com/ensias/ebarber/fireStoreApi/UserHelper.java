package com.ensias.ebarber.fireStoreApi;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ensias.ebarber.Interface.MyCallBack;
import com.ensias.ebarber.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class UserHelper {
    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    static CollectionReference UsersRef = db.collection("User");

    public static void addUser(String name, String adresse, String tel,String type){
        User user = new User(name,adresse,tel,FirebaseAuth.getInstance().getCurrentUser().getEmail(),type);
        UsersRef.document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).set(user);

    }
    public static void updateToken(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String token = preferences.getString("fcmToken", null);
        if(token != null){
            UsersRef.document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).update("fcmToken",token).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("fcmToken", null);
                    editor.apply();
                }


            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
//

        }

    }
    public static void getToken(String email, MyCallBack callback){
        DocumentReference userRef = db.collection("User").document("" + email + "");
        userRef.addSnapshotListener( new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                String value = documentSnapshot.getString("fcmToken");
                callback.onCallback(value);

            }
        });

    }
    public static void deleteUser(String email){

        UsersRef.document(email).delete();
    }

}
