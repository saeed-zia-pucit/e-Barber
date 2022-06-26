package com.ensias.ebarber;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SymptomDetailsActivity extends AppCompatActivity {
RadioButton r1,r2,r3,r4,r5,r6,selectedRadioButton;
RadioGroup g1,g2,g3;
TextView t1,t2,t3;
Button show;
int type=0;
public   String Type,symptom1="Absent",symptom2="Absent",symptom3="Absent";

    public static final String Database_Path = "Patient_Table";
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptom_details);

       show=findViewById(R.id.showReport);
        r1=findViewById(R.id.radia_id1);
        r2=findViewById(R.id.radia_id2);
        r3=findViewById(R.id.radia_id3);
        r4=findViewById(R.id.radia_id4);
        r5=findViewById(R.id.radia_id5);
        r6=findViewById(R.id.radia_id6);

        g1=findViewById(R.id.groupradio1);
        g2=findViewById(R.id.groupradio2);
        g3=findViewById(R.id.groupradio3);

        t1=findViewById(R.id.text1);
        t2=findViewById(R.id.text2);
        t3=findViewById(R.id.text3);
        //

        //

        Intent intent=getIntent();
        type=intent.getIntExtra("type",0);
        if(type==1){
            t1.setText("Is there light pain in eyes");
            t2.setText("Is there redness and inflammation in the eyes");
            t3.setText("Is there an infection of the oil gland at the base of an eyelash.");

        }
        else if(type==2){
            t1.setText("Is there open sores or lesions");
            t2.setText("Is there redness on the nose, chin, cheeks, and forehead.");
            t3.setText("Is there rash, which might be painful or itchy.");
        }
        else if(type==3){
            t1.setText("Is there  issue of memory loss");
            t2.setText("Is there any anxiety problem");
            t3.setText("Is there issue of a loss of inhibition");
        }
        else if(type==4){
            t1.setText("Is there issue of  Loss of appetite");
            t2.setText("Is there General weakness in the body");
            t3.setText("Do you feel Chills and shivering?");
        }

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(SymptomDetailsActivity.this,DiseaseReportActivity.class);
                startActivity(i);

            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();

        int id=g3.getCheckedRadioButtonId();
        if (id != -1) {
            selectedRadioButton = findViewById(id);
            String selectedRbText = selectedRadioButton.getText().toString();
            SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
            // Creating an Editor object to edit(write to the file)
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.clear();

//
            symptom1=t1.getText().toString();
            symptom2=t2.getText().toString();
            symptom3=t3.getText().toString();
            if(type==1){
               // Symptom.type="eye";
                Type="eye";
                if(selectedRbText.equals("Present")){
                    myEdit.putInt("eye",70);

                }
                else{
                    myEdit.putInt("eye",30);
                }
            }
            else if(type==2){
                Type="skin";
                if(selectedRbText.equals("Present")){
                    myEdit.putInt("skin",70);
                }
                else{
                    myEdit.putInt("skin",30);
                }
            }
            else if(type==3){
                Type="head";
                if(selectedRbText.equals("Present")){
                    myEdit.putInt("head",70);
                }
                else{
                    myEdit.putInt("head",30);
                }

            }
           else{
                Type="fever";
                if(selectedRbText.equals("Present")){
                    myEdit.putInt("fever",70);
                }
                else{
                    myEdit.putInt("fever",30);
                }
            }
            myEdit.commit();
            myEdit.apply();
        }
       UploadSymptoms();
    }
    public void UploadSymptoms(){
        try {
             String patient_mail = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString().split("@")[0];
            databaseReference = FirebaseDatabase.getInstance("https://health-care-b9ce9-default-rtdb.firebaseio.com/").getReference(Database_Path).child(patient_mail).child("Symptom").push();
          //  Symptom s =new Symptom(Type,symptom1,symptom2,symptom3);

            databaseReference.child("Type").setValue(Type);
            databaseReference.child(symptom1).setValue("Present");
            databaseReference.child(symptom2).setValue("Present");
            databaseReference.child(symptom3).setValue("Present");

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}