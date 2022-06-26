package com.ensias.ebarber;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import com.ensias.ebarber.Common.Common;
import com.ensias.ebarber.fireStoreApi.UserHelper;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class DoctorHomeActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener{
    static String doc;
    Button SignOutBtn2;
    Button BtnRequst;
    Button listPatients;
    Button appointementBtn,languageBtn;
    @OnClick(R.id.profile)
    void profileBtnClick(){
        Intent k = new Intent(DoctorHomeActivity.this, ProfileDoctorActivity.class);
        startActivity(k);
    }
    Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_home); //ici layout de page d'acceuil MEDECIN
        unbinder = ButterKnife.bind(this,this);
        Common.CurreentDoctor = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
        Common.CurrentUserType = "doctor";
        listPatients = findViewById(R.id.listPatients);
        BtnRequst=findViewById(R.id.btnRequst);
        SignOutBtn2=findViewById(R.id.signOutBtn);
        UserHelper.updateToken(DoctorHomeActivity.this);
        appointementBtn = findViewById(R.id.appointement);
        SignOutBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        BtnRequst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent k = new Intent(DoctorHomeActivity.this, ConfirmedAppointmensActivity.class);
                startActivity(k);
            }
        });
        listPatients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent k = new Intent(DoctorHomeActivity.this, MyPatientsActivity.class);
//                startActivity(k);
                Intent k = new Intent(DoctorHomeActivity.this, MyServicesActivity.class);
                k.putExtra("userType","Barber");
                k.putExtra("email",FirebaseAuth.getInstance().getCurrentUser().getEmail());
                startActivity(k);
            }
        });
        appointementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              // doc = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
                //showDatePickerDialog(v.getContext());
                Intent k = new Intent(DoctorHomeActivity.this, DoctorAppointementActivity.class);
                startActivity(k);
            }
        });
        languageBtn = findViewById(R.id.change_language);
        languageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Locale current = getResources().getConfiguration().locale;
                if(current.getLanguage().equals("en")) {
                    languageBtn.setText(getString(R.string.change_language_to_english));
                    updateLocale(new Locale("el"));
                }else {
                    languageBtn.setText(getString(R.string.change_language_to_greek));
                    updateLocale(new Locale("en"));
                }

            }
        });
        setLanguageButtonText();
    }

    public void showDatePickerDialog(Context wf){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                wf,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date = "month_day_year: " + month + "_" + dayOfMonth + "_" + year;
        openPage(view.getContext(),doc,date);
    }

    private void openPage(Context wf, String d,String day){
        Intent i = new Intent(wf, AppointementActivity.class);
        i.putExtra("key1",d+"");
        i.putExtra("key2",day);
        i.putExtra("key3","doctor");
        wf.startActivity(i);
    }

    private void setLanguageButtonText(){
        Locale current = getResources().getConfiguration().locale;
        if(current.getLanguage().equals("en")) {
            languageBtn.setText(getString(R.string.change_language_to_greek));
        }else {
            languageBtn.setText(getString(R.string.change_language_to_english));
        }

    }
}
