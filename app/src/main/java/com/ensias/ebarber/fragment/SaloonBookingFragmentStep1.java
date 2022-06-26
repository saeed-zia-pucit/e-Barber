package com.ensias.ebarber.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ensias.ebarber.R;
import com.ensias.ebarber.adapter.MyServiceAdapter;
import com.ensias.ebarber.model.Services;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class SaloonBookingFragmentStep1 extends Fragment implements MyServiceAdapter.ServiceAdapterListener {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference myServicesRef = db.collection("BarberServices");

    private MyServiceAdapter adapter;

    RecyclerView recyclerView;

    public SaloonBookingFragmentStep1() {
        // Required empty public constructor
    }
    private void addDummyItemInRecycler(){

    }
    public static SaloonBookingFragmentStep1 newInstance(String param1, String param2) {
        SaloonBookingFragmentStep1 fragment = new SaloonBookingFragmentStep1();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View view = inflater.inflate(R.layout.fragment_saloon_booking_step1, container, false);
         intiGui(view);
        setUpRecyclerView();
        return view;
    }

    public void setUpRecyclerView(){
        //Get the doctors by patient id
        final String barberID = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
        Query query = myServicesRef.document(""+barberID+"")
                .collection("MyServices");

        FirestoreRecyclerOptions<Services> options = new FirestoreRecyclerOptions.Builder<Services>()
                .setQuery(query, Services.class)
                .build();

        adapter = new MyServiceAdapter(options,this,"");
        //ListMyDoctors
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }
    private void initNonGui(View view){

    }
    private void intiGui(View view){
        recyclerView = view.findViewById(R.id.ListMyDoctors);

    }
    @Override
    public void onStart() {
        super.onStart();
        //adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void updateButtonClicked(String name,int charges) {

    }

    @Override
    public void deleteButtonClicked(String position) {

    }
}