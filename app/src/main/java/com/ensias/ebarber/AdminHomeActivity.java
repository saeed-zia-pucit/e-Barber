package com.ensias.ebarber;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ensias.ebarber.fireStoreApi.UserHelper;
import com.google.firebase.auth.FirebaseAuth;

public class AdminHomeActivity extends AppCompatActivity {
Button signout,show,profile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        show=findViewById(R.id.showUsers);
        signout=findViewById(R.id.signOut);
        profile=findViewById(R.id.profile);
        UserHelper.updateToken(AdminHomeActivity.this);

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminHomeActivity.this,ShowAllDoctors.class));
            }
        });
     profile.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
          startActivity(new Intent(AdminHomeActivity.this,ProfileAdminActivity.class));

         }
     });

    }
}