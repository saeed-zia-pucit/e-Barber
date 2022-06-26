package com.ensias.ebarber.bookingDashboard;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.ensias.ebarber.Common.Common;
import com.ensias.ebarber.HomeActivity;
import com.ensias.ebarber.Interface.ITimeSlotLoadListener;
import com.ensias.ebarber.R;
import com.ensias.ebarber.adapter.MyTimeSlotAdapter;
import com.ensias.ebarber.model.ApointementInformation;
import com.ensias.ebarber.model.CartItem;
import com.ensias.ebarber.model.TimeSlot;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;
import dmax.dialog.SpotsDialog;

public class TimeSlotActivity extends AppCompatActivity implements ITimeSlotLoadListener {

    DocumentReference doctorDoc;
    ITimeSlotLoadListener iTimeSlotLoadListener;
    AlertDialog dialog;

    LocalBroadcastManager localBroadcastManager;

    RecyclerView recycler_time_slot;
    HorizontalCalendarView calendarView;
    SimpleDateFormat simpleDateFormat;
    Button addToCartButton,viewCart;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_booking_step_two);
        iTimeSlotLoadListener = this;
        simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy");

        dialog = new SpotsDialog.Builder().setContext(TimeSlotActivity.this).setCancelable(false).build();
        init();
        loadAvailabelTimeSlotOfDoctor(Common.CurreentDoctor,simpleDateFormat.format(Common.currentDate.getTime()));

//        localBroadcastManager = LocalBroadcastManager.getInstance(TimeSlotActivity.this);
//        localBroadcastManager.registerReceiver(displayTimeSlot,new IntentFilter(Common.KEY_DISPLAY_TIME_SLOT));
        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Common.cartItems.add(new CartItem(Common.Curre))
                startActivity(new Intent(TimeSlotActivity.this,ConfirmOrderActivity.class));
            }
        });
        viewCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TimeSlotActivity.this,CartActivity.class));
            }
        });

    }
    BroadcastReceiver displayTimeSlot = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Calendar date = Calendar.getInstance();
            date.add(Calendar.DATE,0);
            loadAvailabelTimeSlotOfDoctor(Common.CurreentDoctor,simpleDateFormat.format(date.getTime()));

        }
    };

    private void loadAvailabelTimeSlotOfDoctor(String doctorId, final String bookDate) {
      //  dialog.show();

        doctorDoc = FirebaseFirestore.getInstance()
                .collection("Doctor")
                .document(Common.CurreentDoctor);
        doctorDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        CollectionReference date =FirebaseFirestore.getInstance()
                                .collection("Doctor")
                                .document(Common.CurreentDoctor)
                                .collection(bookDate);

                        date.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful())
                                {
                                    QuerySnapshot querySnapshot = task.getResult();
                                    if (querySnapshot.isEmpty())
                                    {
                                        iTimeSlotLoadListener.onTimeSlotLoadEmpty();
                                    }else {
                                        List<TimeSlot> timeSlots = new ArrayList<>();
                                        for (QueryDocumentSnapshot document:task.getResult())
                                            timeSlots.add(document.toObject(TimeSlot.class));
                                        iTimeSlotLoadListener.onTimeSlotLoadSuccess(timeSlots);
                                    }

                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                iTimeSlotLoadListener.onTimeSlotLoadFailed(e.getMessage());
                            }
                        });
                    }
                }
            }
        });
    }




    @Override
    public void onDestroy() {
//        localBroadcastManager.unregisterReceiver(displayTimeSlot);
        super.onDestroy();
    }

//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        super.onCreateView(inflater, container, savedInstanceState);
//        View itemView = inflater.inflate(R.layout.fragment_booking_step_two,container,false);
//        unbinder = ButterKnife.bind(this,itemView);
//
//        init(itemView);
//        loadAvailabelTimeSlotOfDoctor(Common.CurreentDoctor,simpleDateFormat.format(Common.currentDate.getTime()));
//
//        return itemView;
//
//    }

    private void init() {
        addToCartButton=findViewById(R.id.add_to_cart_button);
        viewCart=findViewById(R.id.view_cart);
        recycler_time_slot=findViewById(R.id.recycle_time_slot);
        recycler_time_slot.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(TimeSlotActivity.this,3);
        recycler_time_slot.setLayoutManager(gridLayoutManager);
        //recycler_time_slot.addItemDecoration(new SpaceI);

        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.DATE,0);
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.DATE,5);
        HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(this,R.id.calendarView)
                .range(startDate,endDate)
                .datesNumberOnScreen(1)
                .mode(HorizontalCalendar.Mode.DAYS)
                .defaultSelectedDate(startDate)
                .build();

        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                if(Common.currentDate.getTimeInMillis() != date.getTimeInMillis()){
                    Common.currentDate = date;
                    loadAvailabelTimeSlotOfDoctor(Common.CurreentDoctor,simpleDateFormat.format(date.getTime()));

                }

            }
        });
    }

    @Override
    public void onTimeSlotLoadSuccess(List<TimeSlot> timeSlotList) {
        MyTimeSlotAdapter adapter = new MyTimeSlotAdapter(this,timeSlotList);
        recycler_time_slot.setAdapter(adapter);
        dialog.dismiss();
    }

    @Override
    public void onTimeSlotLoadFailed(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    }

    @Override
    public void onTimeSlotLoadEmpty() {
        MyTimeSlotAdapter adapter = new MyTimeSlotAdapter(this);
        recycler_time_slot.setAdapter(adapter);
        dialog.dismiss();
    }



    void confirmeApointement() {
        ApointementInformation apointementInformation = new ApointementInformation();
        apointementInformation.setApointementType(Common.Currentaappointementatype);
        apointementInformation.setDoctorId(Common.CurreentDoctor);
        apointementInformation.setDoctorName(Common.CurrentDoctorName);
        apointementInformation.setPatientName(Common.CurrentUserName);
        apointementInformation.setPatientId(Common.CurrentUserid);
        apointementInformation.setChemin("Doctor/" + Common.CurreentDoctor + "/" + Common.simpleFormat.format(Common.currentDate.getTime()) + "/" + String.valueOf(Common.currentTimeSlot));
        apointementInformation.setType("Checked");
        apointementInformation.setTime(new StringBuilder(Common.convertTimeSlotToString(Common.currentTimeSlot))
                .append("at")
                .append(simpleDateFormat.format(Common.currentDate.getTime())).toString());
        apointementInformation.setSlot(Long.valueOf(Common.currentTimeSlot));

        DocumentReference bookingDate = FirebaseFirestore.getInstance()
                .collection("Doctor")
                .document(Common.CurreentDoctor)
                .collection(Common.simpleFormat.format(Common.currentDate.getTime()))
                .document(String.valueOf(Common.currentTimeSlot));

        bookingDate.set(apointementInformation)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(TimeSlotActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                        Common.currentTimeSlot = -1;
                        Common.currentDate = Calendar.getInstance();
                        Common.step = 0;
                        //sendPush(Common.CurreentDoctor);
//                        startActivity(new Intent(getContext(), HomeActivity.class));
//                        getActivity().finish();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TimeSlotActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                FirebaseFirestore.getInstance().collection("Doctor").document(Common.CurreentDoctor)
                        .collection("apointementrequest").document(apointementInformation.getTime().replace("/", "_")).set(apointementInformation);
                FirebaseFirestore.getInstance().collection("Patient").document(apointementInformation.getPatientId()).collection("calendar")
                        .document(apointementInformation.getTime().replace("/", "_")).set(apointementInformation);

            }
        });

//
    }
}