package com.ensias.ebarber;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ensias.ebarber.Common.Common;
import com.ensias.ebarber.adapter.MyServiceAdapter;
import com.ensias.ebarber.fireStoreApi.MyServiceHelper;
import com.ensias.ebarber.model.Services;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MyServicesActivity extends BaseActivity implements MyServiceAdapter.ServiceAdapterListener {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference myServicesRef = db.collection("BarberServices");
    private MyServiceAdapter adapter;
    RecyclerView recyclerView;
    ExtendedFloatingActionButton floatingActionButton;
    private String m_Text = "";
    AlertDialog dialog;
    private TextView titleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_services);
        intiGui();
        setListener();
        setUpRecyclerView();
        configureForUSerType();
        //getSupportActionBar().setTitle("Hello world App");
    }
    private void configureForUSerType(){
        String userType = getIntent().getStringExtra(Common.USER_TYPE);
        if(userType.equals("Barber")){
         titleText.setText(getString(R.string.my_services));
        }else {
            titleText.setText(getString(R.string.avaiable_services));
            floatingActionButton.setVisibility(View.GONE);
        }
    }
    public void setUpRecyclerView(){
        //Get the doctors by patient id
        String barberID="", userType="";
        if(getIntent() != null) {
             userType = getIntent().getStringExtra(Common.USER_TYPE);
             barberID = getIntent().getStringExtra("email");
        }
        //final String barberID = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
        Query query = myServicesRef.document(""+barberID+"")
                .collection("MyServices");

        FirestoreRecyclerOptions<Services> options = new FirestoreRecyclerOptions.Builder<Services>()
                .setQuery(query, Services.class)
                .build();

        adapter = new MyServiceAdapter(options,this,userType);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MyServicesActivity.this));
        recyclerView.setAdapter(adapter);
    }
    private void setListener(){
     floatingActionButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
             addNewService("",0);

         }
     });
    }
    private void intiGui(){

        recyclerView =findViewById(R.id.ListMyDoctors);
        floatingActionButton = findViewById(R.id.fab_add_service);
        titleText = findViewById(R.id.title_bar);
    }
    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
    private void addNewService(String name,int price){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.add_new_services));
        View viewInflated = getLayoutInflater().inflate(R.layout.service_input_dialog, null);
        final EditText inputPrice = (EditText) viewInflated.findViewById(R.id.input_price);
        final EditText inputName = (EditText) viewInflated.findViewById(R.id.input_name);
        final Button btnSave = (Button) viewInflated.findViewById(R.id.btn_save);
        final Button btnCancel = (Button) viewInflated.findViewById(R.id.btn_cancel);
        inputName.setText(name);
        inputPrice.setText(String.valueOf(price));
        builder.setView(viewInflated);


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  MyServiceHelper.addService(inputName.getText().toString(),Integer.parseInt(inputPrice.getText().toString()));
                 dialog.dismiss();
            }
        });

       btnCancel.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               dialog.dismiss();

           }
       });

         dialog= builder.create();
        dialog.show();
    }

    @Override
    public void updateButtonClicked(String name,int charges) {
     addNewService(name,charges);
    }

    @Override
    public void deleteButtonClicked(String serviceName) {
     if(getIntent().getStringExtra("userType").equals("Barber")){
         MyServiceHelper.deleteService(serviceName);
        }else {
         //book button clicked
         Common.Currentaappointementatype = serviceName;
         startActivity(new Intent(MyServicesActivity.this,TestActivity.class));
     }

    }
}