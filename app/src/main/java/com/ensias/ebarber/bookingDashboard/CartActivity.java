package com.ensias.ebarber.bookingDashboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.ensias.ebarber.Common.CartItems;
import com.ensias.ebarber.Common.Common;
import com.ensias.ebarber.ImagesActivity;
import com.ensias.ebarber.R;
import com.ensias.ebarber.UploadReport;
import com.ensias.ebarber.adapter.CartAdapter;
import com.ensias.ebarber.adapter.ImageAdapter;
import com.ensias.ebarber.model.ImageUploadInfo;
import com.ensias.ebarber.model.Services;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Services> list;
    // Creating RecyclerView.Adapter.
    CartAdapter adapter;
    ImageView backButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        backButton=findViewById(R.id.back_button);
        setUpRecyclerView();
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



    }
    private void setUpRecyclerView(){
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(CartActivity.this));

        adapter = new CartAdapter(CartActivity.this, Common.cartItems);

        recyclerView.setAdapter(adapter);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}