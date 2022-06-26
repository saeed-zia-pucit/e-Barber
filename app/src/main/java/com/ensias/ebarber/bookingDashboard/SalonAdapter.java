package com.ensias.ebarber.bookingDashboard;



import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ensias.ebarber.Common.Common;
import com.ensias.ebarber.MyServicesActivity;
import com.ensias.ebarber.R;
import com.ensias.ebarber.model.Doctor;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SalonAdapter  extends RecyclerView.Adapter<SalonAdapter.DoctoreHolder2> {
    public static boolean specialiteSearch = false;
    static String doc;
    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    static CollectionReference addRequest = db.collection("Request");
    private List<Doctor> mTubeList;
    private List<Doctor> mTubeListFiltered;
    private StorageReference pathReference;
    private DoctorAdapterListener listener;
    private int selectedIndex=0;


    public SalonAdapter(List<Doctor> tubeList, DoctorAdapterListener doctorAdapterListener) {
        mTubeList = tubeList;
        mTubeListFiltered = tubeList;
        listener = doctorAdapterListener;
    }

    @NonNull
    @Override
    public DoctoreHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.barber_item,
                parent, false);
        return new DoctoreHolder2(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctoreHolder2 doctoreHolder, int i) {
        final Doctor doctor = mTubeListFiltered.get(i);
        final TextView t = doctoreHolder.title;
        doctoreHolder.title.setText(doctor.getName());
        if(doctoreHolder.getAdapterPosition()==selectedIndex){
          doctoreHolder.mainConst.setBackgroundColor(Color.parseColor("#439157"));
        }else {
            doctoreHolder.mainConst.setBackgroundColor(Color.parseColor("#3c4652"));
        }
        doctoreHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedIndex=doctoreHolder.getAdapterPosition();
                listener.barberCardClicked(doctor);
                notifyDataSetChanged();
            }
        });
        /// ajouter l'image

        String imageId = doctor.getEmail()+".jpg";
        pathReference = FirebaseStorage.getInstance().getReference().child("DoctorProfile/"+ imageId);
        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(doctoreHolder.image.getContext())
                        .load(uri)
                        .placeholder(R.drawable.ic_person)
                        .fit()
                        .centerCrop()
                        .into(doctoreHolder.image);//Image location
              //  Glide.with(doctoreHolder.image.getContext()).load(uri).into(doctoreHolder.image).cir;
         //profileImage.setImageURI(uri);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });


    }

    @Override
    public int getItemCount() {
        return mTubeListFiltered.size();
    }


    protected class DoctoreHolder2 extends RecyclerView.ViewHolder {

        TextView title;
        TextView price;
        CircleImageView image;
        ConstraintLayout mainConst;


        public DoctoreHolder2(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.item_title);
            price = itemView.findViewById(R.id.item_price);
            image = itemView.findViewById(R.id.item_icon);
            mainConst=itemView.findViewById(R.id.const_main);
        }



    }
    public interface DoctorAdapterListener {
        public void barberCardClicked(Doctor doctor);
        //public void deleteButtonClicked(String Servicename);
    }
    private void setViewForSelected(){

    }
}
