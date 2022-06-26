package com.ensias.ebarber.bookingDashboard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ensias.ebarber.Common.Common;
import com.ensias.ebarber.HomeActivity;
import com.ensias.ebarber.R;
import com.ensias.ebarber.model.ApointementInformation;
import com.ensias.ebarber.model.CartItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ConfirmOrderActivity extends AppCompatActivity {
    SimpleDateFormat simpleDateFormat;

    Unbinder unbinder;
    @BindView(R.id.txt_booking_berber_text)
    TextView txt_booking_berber_text;
    @BindView(R.id.txt_booking_time_text)
    TextView txt_booking_time_text;
    @BindView(R.id.txt_booking_type)
    TextView txt_booking_type;
    @BindView(R.id.txt_booking_phone)
    TextView txt_booking_phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);
        ButterKnife.bind(this);
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        setData();

    }

    private void setData() {
        txt_booking_berber_text.setText(Common.CurrentDoctorName);
        txt_booking_time_text.setText(new StringBuilder(Common.convertTimeSlotToString(Common.currentTimeSlot))
                .append("at")
                .append(simpleDateFormat.format(Common.currentDate.getTime())));
        txt_booking_type.setText(Common.Currentaappointementatype);

        int tempPrice=0;
        for (CartItem cartItem : Common.cartItems) {
          tempPrice=tempPrice+cartItem.getPrice();
        }
        txt_booking_phone.setText(String.valueOf(tempPrice));


    }

    @OnClick(R.id.btn_confirm)
    public void onConfirmButtonClick() {
        for (CartItem cartItem : Common.cartItems) {

            ApointementInformation apointementInformation = new ApointementInformation();
            apointementInformation.setApointementType(cartItem.getServiceName());
            apointementInformation.setDoctorId(cartItem.getServiceProviderId());
            apointementInformation.setDoctorName(cartItem.getSalonName());
            apointementInformation.setPatientName(Common.CurrentUserName);
            apointementInformation.setPatientId(Common.CurrentUserid);
            apointementInformation.setChemin("Doctor/" + cartItem.getServiceProviderId() + "/" + Common.simpleFormat.format(Common.currentDate.getTime()) + "/" + String.valueOf(Common.currentTimeSlot));
            apointementInformation.setType("Checked");
            apointementInformation.setTime(new StringBuilder(Common.convertTimeSlotToString(Common.currentTimeSlot))
                    .append("at")
                    .append(simpleDateFormat.format(Common.currentDate.getTime())).toString());
            apointementInformation.setSlot(Long.valueOf(Common.currentTimeSlot));

            confirmeApointement(apointementInformation);
        }
    }

    void confirmeApointement(ApointementInformation apointementInformation) {

        DocumentReference bookingDate = FirebaseFirestore.getInstance()
                .collection("Doctor")
                .document(Common.CurreentDoctor)
                .collection(Common.simpleFormat.format(Common.currentDate.getTime()))
                .document(String.valueOf(Common.currentTimeSlot));

        bookingDate.set(apointementInformation)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_SHORT).show();
                        Common.currentTimeSlot = -1;
                        Common.currentDate = Calendar.getInstance();
                        Common.step = 0;
                        //sendPush(Common.CurreentDoctor);
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        finish();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
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