package com.ensias.ebarber.bookingDashboard;

import android.animation.Animator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ensias.ebarber.BaseActivity;
import com.ensias.ebarber.Common.CartItems;
import com.ensias.ebarber.Common.Common;
import com.ensias.ebarber.DoctorAdapterFiltred;
import com.ensias.ebarber.MainActivity;
import com.ensias.ebarber.MyServicesActivity;
import com.ensias.ebarber.R;
import com.ensias.ebarber.TestActivity;
import com.ensias.ebarber.Utils.CircleAnimationUtil;
import com.ensias.ebarber.adapter.MyServiceAdapter;
import com.ensias.ebarber.model.CartItem;
import com.ensias.ebarber.model.Doctor;
import com.ensias.ebarber.model.Services;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;


public class BookingDashboard extends BaseActivity implements ServiceAdapter.ServiceAdapterListener, SalonAdapter.DoctorAdapterListener {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference doctorRef = db.collection("Doctor");
    private CollectionReference myServicesRef = db.collection("BarberServices");
    private SalonAdapter barberAdapter;
    private ServiceAdapter serviceAdapter;
    private RecyclerView serviceRecycler;
    private Button addToCartButton,continueButton;
    private ImageView backButton,cartIcon;
    private List<Doctor> barberList;
    private RecyclerView recyclerView;
    private Doctor selectedSalon=null;
    private Services selectedService=null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_dashboard);
        intiGui();
        setUpBarberRecyclerView();
        setListener();


    }

    private void  setUpBarberRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false));
        Query query = doctorRef.orderBy("name", Query.Direction.DESCENDING);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                barberList=task.getResult().toObjects(Doctor.class);
                barberAdapter = new SalonAdapter(barberList,BookingDashboard.this);
                recyclerView.setAdapter(barberAdapter);
                recyclerView.setNestedScrollingEnabled(false);
                Doctor doctor= task.getResult().toObjects(Doctor.class).get(0);
                barberCardClicked(doctor);


            }
        });

    }

    public void setUpServiceRecyclerView(String barberID){
        //Get the doctors by patient id
        String userType="Customer";
//        if(getIntent() != null) {
//            userType = getIntent().getStringExtra(Common.USER_TYPE);
//            barberID = getIntent().getStringExtra("email");
//        }
        //final String barberID = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
        Query query = myServicesRef.document(""+barberID+"")
                .collection("MyServices");

        FirestoreRecyclerOptions<Services> options = new FirestoreRecyclerOptions.Builder<Services>()
                .setQuery(query, Services.class)
                .build();

        serviceAdapter = new ServiceAdapter(options,BookingDashboard.this,userType,BookingDashboard.this);
        //ListMyDoctors
        serviceRecycler.setHasFixedSize(true);
        serviceRecycler.setLayoutManager(new LinearLayoutManager(BookingDashboard.this,LinearLayoutManager.HORIZONTAL, false));
        serviceRecycler.setAdapter(serviceAdapter);
        serviceAdapter.startListening();
        serviceRecycler.setNestedScrollingEnabled(false);


    }
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
       // serviceAdapter.stopListening();
    }
    private void intiGui(){
        serviceRecycler =findViewById(R.id.service_recycler);
        backButton= findViewById(R.id.back_button);
        addToCartButton=findViewById(R.id.add_to_cart_button);
        continueButton=findViewById(R.id.continue_button);
        recyclerView = findViewById(R.id.barber_recycler);
        cartIcon=findViewById(R.id.cart_icon);


//        floatingActionButton = findViewById(R.id.fab_add_service);
//        titleText = findViewById(R.id.title_bar);
    }
    private void makeFlyAnimation(ImageView targetView) {

        new CircleAnimationUtil().attachActivity(this).setTargetView(targetView).setMoveDuration(1000).setDestView(cartIcon).setAnimationListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //addItemToCart();
                Toast.makeText(BookingDashboard.this, "Continue Shopping...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).startAnimation();


    }

    private void setListener(){
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Common.cartItems.size()>0) {
                    startActivity(new Intent(BookingDashboard.this,CartActivity.class));
                }else {
                    Toast.makeText(BookingDashboard.this, "Cart is empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.Currentaappointementatype = selectedService.getServiceName();
                Common.CurreentDoctor=selectedSalon.getEmail();
                Common.CurrentDoctorName= selectedSalon.getName();
                Common.currentOrderPrice=selectedService.getServiceCharges();
                startActivity(new Intent(BookingDashboard.this,TimeSlotActivity.class));
            }
        });
        cartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BookingDashboard.this,CartActivity.class));

            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void barberCardClicked(Doctor salon) {
        if(selectedSalon!= null && ! selectedSalon.equals(salon)){
            Common.cartItems.clear();
        }
        setUpServiceRecyclerView(salon.getEmail());
        selectedSalon=salon;
    }

    @Override
    public void onServiceCardClick(Services service,ImageView imageView) {
        selectedService=service;
        //makeFlyAnimation(imageView);
        showConfirmAlert(imageView);
    }
    public void showConfirmAlert(ImageView imageView) {

        AlertDialog.Builder alert = new AlertDialog.Builder(BookingDashboard.this);
        alert.setTitle("Add To Cart Item");
        alert.setMessage("Add To Cart This Service");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                Common.cartItems.add(new CartItem(selectedService.getServiceName(), selectedSalon.getName(), selectedService.getImageName(), selectedService.getServiceCharges(),selectedSalon.getEmail()));
                dialog.dismiss();
                makeFlyAnimation(imageView);
            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}

