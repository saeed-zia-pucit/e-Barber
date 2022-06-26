package com.ensias.ebarber;

import android.content.Context;
import android.content.Intent;
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
import androidx.recyclerview.widget.RecyclerView;

import com.ensias.ebarber.Interface.MyCallBack;
import com.ensias.ebarber.fireStoreApi.DoctorHelper;
import com.ensias.ebarber.fireStoreApi.MyServiceHelper;
import com.ensias.ebarber.fireStoreApi.SalonHelper;
import com.ensias.ebarber.fireStoreApi.UserHelper;
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

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class DoctorAdapterFilteredAdmin  extends RecyclerView.Adapter<DoctorAdapterFilteredAdmin.DoctoreHolder2> implements Filterable {
    public static boolean specialiteSearch = false;
    static String doc;
    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    static CollectionReference addRequest = db.collection("Request");
    private List<Doctor> mTubeList;
    private List<Doctor> mTubeListFiltered;
    StorageReference pathReference ;
    Context mContext;
    MyCallBack myCallBack;


    public DoctorAdapterFilteredAdmin(List<Doctor> tubeList, Context context, MyCallBack myCallBack){
        mTubeList = tubeList;
        mTubeListFiltered = tubeList;
        mContext=context;
        this.myCallBack=myCallBack;
    }

    @NonNull
    @Override
    public DoctoreHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.doctor_item_admin,
                parent, false);
        return new DoctoreHolder2(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctoreHolder2 doctoreHolder, int i) {
        final Doctor doctor = mTubeListFiltered.get(i);
        final TextView t = doctoreHolder.title ;
        doctoreHolder.title.setText(doctor.getName());
        /// ajouter l'image

        String imageId = doctor.getEmail()+".jpg";
        pathReference = FirebaseStorage.getInstance().getReference().child("DoctorProfile/"+ imageId);
        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(doctoreHolder.image.getContext())
                        .load(uri)
                        .placeholder(R.drawable.hairstyle)
                        .fit()
                        .centerCrop()
                        .into(doctoreHolder.image);//Image location

                // profileImage.setImageURI(uri);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
        doctoreHolder.specialite.setText("Specialite : "+doctor.getSpecialite());
        final String idPat = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
        final String idDoc = doctor.getEmail();
        // doctoreHolder.image.setImageURI(Uri.parse("drawable-v24/ic_launcher_foreground.xml"));
        doctoreHolder.addDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> note = new HashMap<>();
                note.put("id_patient", idPat);
                note.put("id_doctor", idDoc);
                addRequest.document().set(note)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Snackbar.make(t, "Demande envoyée", Snackbar.LENGTH_SHORT).show();
                            }
                        });
                doctoreHolder.addDoc.setVisibility(View.INVISIBLE);
            }
        });
        doctoreHolder.RemoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doc= doctor.getEmail();
//                Common.CurreentDoctor = doctor.getEmail();
//                Common.CurrentDoctorName = doctor.getName();
//                Common.CurrentPhone = doctor.getTel();
//                openPage(v.getContext());
                DoctorHelper.deleteDoctor(doc);
                UserHelper.deleteUser(doc);
                SalonHelper.deleteSalon(doc);
                MyServiceHelper.deleteAllService(doc);
                myCallBack.onCallback("");
//                 List<Doctor> temp= mTubeListFiltered;
//                 mTubeListFiltered.clear();
//                 temp.remove(doctor);
//                 mTubeListFiltered.addAll(temp);
//                notifyDataSetChanged();
//                mTubeListFiltered.size();
            }
        });

        doctoreHolder.showPatientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                doc= doctor.getEmail();
             Intent intent=   new Intent(mContext,MyPatientActivityAdmin.class);
             intent.putExtra("email",doc);
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
             mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mTubeListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String pattern = constraint.toString().toLowerCase();
                if(pattern.isEmpty()){
                    mTubeListFiltered = mTubeList;
                } else {
                    List<Doctor> filteredList = new ArrayList<>();
                    for(Doctor tube: mTubeList){
                        if(specialiteSearch == false) {
                            if (tube.getName().toLowerCase().contains(pattern) || tube.getName().toLowerCase().contains(pattern)) {
                                filteredList.add(tube);
                            }
                        }
                        else{
                            if (tube.getSpecialite().toLowerCase().contains(pattern) || tube.getSpecialite().toLowerCase().contains(pattern)) {
                                filteredList.add(tube);
                            }
                        }
                    }
                    mTubeListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mTubeListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mTubeListFiltered = (ArrayList<Doctor>) results.values;
                notifyDataSetChanged();
            }
        };
    }
    class DoctoreHolder2 extends RecyclerView.ViewHolder {

        Button RemoveBtn;
        TextView title;
        TextView specialite;
        ImageView image;
        Button addDoc;
        Button load;
        Button showPatientBtn;
        public DoctoreHolder2(@NonNull View itemView) {
            super(itemView);
            addDoc = itemView.findViewById(R.id.addDocBtn);
            title= itemView.findViewById(R.id.doctor_view_title);
            specialite=itemView.findViewById(R.id.text_view_description);
            image=itemView.findViewById(R.id.doctor_item_image);
            RemoveBtn=itemView.findViewById(R.id.removeBtn);
            showPatientBtn=itemView.findViewById(R.id.showPatientBtn);
        }
    }

}