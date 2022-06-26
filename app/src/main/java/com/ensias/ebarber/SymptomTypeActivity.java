package com.ensias.ebarber;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class SymptomTypeActivity extends AppCompatActivity {
LinearLayout l1,l2,l3,l4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptom_type);

        l1=findViewById(R.id.eyeLayout);
        l1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SymptomTypeActivity.this,SymptomDetailsActivity.class);
               intent.putExtra("type",1);
                startActivity(intent);
            }
        });
        l2=findViewById(R.id.skinlayout);
        l2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SymptomTypeActivity.this,SymptomDetailsActivity.class);
                intent.putExtra("type",2);
                startActivity(intent);
            }
        });
        l3=findViewById(R.id.headLayout);
        l3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SymptomTypeActivity.this,SymptomDetailsActivity.class);
                intent.putExtra("type",3);
                startActivity(intent);
            }
        });
        l4=findViewById(R.id.feverlayout);
        l4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SymptomTypeActivity.this,SymptomDetailsActivity.class);
                intent.putExtra("type",4);
                startActivity(intent);
            }
        });
    }
}