package com.ensias.ebarber;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ensias.ebarber.Common.Common;
import com.ensias.ebarber.bookingDashboard.BookingDashboard;
import com.ensias.ebarber.fireStoreApi.UserHelper;
import com.ensias.ebarber.nearbyactivities.NearByListActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

public class HomeActivity extends BaseActivity {
    Button SignOutBtn;
    Button searchPatBtn;
    Button myDoctors;
    Button BtnNearBy;
    Button profile,languageBtn;
    Button appointment,diseasePredict;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        appointment = findViewById(R.id.appointement2);
        UserHelper.updateToken(HomeActivity.this);
        appointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent k = new Intent(HomeActivity.this, PatientAppointementsActivity.class);
                startActivity(k);
            }
        });

        searchPatBtn = (Button) findViewById(R.id.searchBtn);
        searchPatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent k = new Intent(HomeActivity.this, BookingDashboard.class);
                startActivity(k);
            }
        });
        SignOutBtn = findViewById(R.id.signOutBtn);
        SignOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        BtnNearBy = findViewById(R.id.near_by_saloon);
        BtnNearBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NearByListActivity.class);
                intent.putExtra(Common.OPEN_FOR, Common.SHOW_NEAR_BY);
                startActivity(intent);
            }
        });

        profile = findViewById(R.id.profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent k = new Intent(HomeActivity.this, ProfilePatientActivity.class);
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
                    //startActivity(new Intent(HomeActivity.this,));
                }else {
                    languageBtn.setText(getString(R.string.change_language_to_greek));
                    updateLocale(new Locale("en"));
                }

            }
        });
        setLanguageButtonText();

        Common.CurrentUserid = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
        FirebaseFirestore.getInstance().collection("User").document(Common.CurrentUserid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Common.CurrentUserName = documentSnapshot.getString("name");
            }
        });

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
