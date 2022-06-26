package com.ensias.ebarber.nearbyactivities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ensias.ebarber.R
import com.google.firebase.firestore.FirebaseFirestore

class NearBy : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_near_by)
    }
    fun addNew(){
        val db = FirebaseFirestore.getInstance()

        val document = db.collection("users").document("abc")

//        document.set(data)
//            .addOnSuccessListener{ _ ->
//                //Set Location After your document created on firestore db
//                document.set(35.7853889,  -120.4056973, "fieldName")
//                //fieldName is optional, if you will not pass it will set location in default field named "g"
//                //Also will add field named "geoLocation" as GeoPoint including latitude and longitude
//                //to count distance when querying the data
//            }
//            .addOnFailureListener { exception ->
//                //Document write failed
//            }
    }
}