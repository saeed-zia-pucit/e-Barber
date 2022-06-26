package com.ensias.ebarber;

import static android.widget.AdapterView.OnClickListener;
import static android.widget.AdapterView.OnItemSelectedListener;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.ensias.ebarber.Common.Common;
import com.ensias.ebarber.fireStoreApi.AdminHelper;
import com.ensias.ebarber.fireStoreApi.DoctorHelper;
import com.ensias.ebarber.fireStoreApi.PatientHelper;
import com.ensias.ebarber.fireStoreApi.UserHelper;
import com.ensias.ebarber.nearbyactivities.MapLocationActivity;

public class FirstSigninActivity extends BaseActivity {
    private static final String TAG = "FirstSigninActivity";
    private EditText fullName;
    private EditText userAddress;
    private EditText teL;
    private Button confirmeBtn, selectLocationButton;
    private boolean isLocationSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_signin);
        confirmeBtn = (Button) findViewById(R.id.confirmeBtn);
        selectLocationButton = findViewById(R.id.choose_location);
        fullName = (EditText) findViewById(R.id.firstSignFullName);
        userAddress = (EditText) findViewById(R.id.user_address);
        teL = (EditText) findViewById(R.id.firstSignTel);

        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.planets_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

//        final Spinner specialiteList = (Spinner) findViewById(R.id.specialite_spinner);
//        ArrayAdapter<CharSequence> adapterSpecialiteList = ArrayAdapter.createFromResource(this,
//                R.array.specialite_spinner, android.R.layout.simple_spinner_item);
//        adapterSpecialiteList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //specialiteList.setAdapter(adapterSpecialiteList);
        String newAccountType = spinner.getSelectedItem().toString();

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = spinner.getSelectedItem().toString();
                Log.e(TAG, "onItemSelected:" + selected);
                if (selected.equals("Barber")) {
                    // specialiteList.setVisibility(View.VISIBLE);
                    fullName.setHint("Type Saloon Name");
                    selectLocationButton.setVisibility(View.VISIBLE);
                } else {
                    //   specialiteList.setVisibility(View.GONE);
                    selectLocationButton.setVisibility(View.GONE);
                    fullName.setHint(getString(R.string.your_name));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // specialiteList.setVisibility(View.GONE);
            }
        });
        selectLocationButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(! fullName.getText().toString().equals("")){
                    Common.SALON_TITLE = fullName.getText().toString();
                    Intent intent = new Intent(FirstSigninActivity.this, MapLocationActivity.class);
                    intent.putExtra(Common.OPEN_FOR,Common.SELECT_LOCATION);
                    startActivity(intent);
                }else {
                    Toast.makeText(FirstSigninActivity.this, "Please enter salon Name", Toast.LENGTH_SHORT).show();
                }

            }
        });
        confirmeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String fullname, birtDay, tel, type, specialite;
                fullname = fullName.getText().toString();
                //birtDay = birthday.getText().toString();
                //tel = teL.getText().toString();
                type = spinner.getSelectedItem().toString();
                // specialite = specialiteList.getSelectedItem().toString();
                if (!fullname.equals("") && !userAddress.getText().toString().equals("")) {

                    UserHelper.addUser(fullname, userAddress.getText().toString(), "tel", type);
                    if (type.equals("Customer")) {
                        PatientHelper.addPatient(fullname, userAddress.getText().toString(), "tel");

                    } else if (type.equals("Admin")) {
                        AdminHelper.addAdmin(fullname, userAddress.getText().toString(), "tel");
                        System.out.println("Add admin " + fullname + " to admin collection");
                    } else {
                        DoctorHelper.addDoctor(fullname, userAddress.getText().toString(), "tel", "specialite");

                    }
                    Intent k = new Intent(FirstSigninActivity.this, MainActivity.class);
                    startActivity(k);
                } else {
                    Toast.makeText(FirstSigninActivity.this, "Field Missing", Toast.LENGTH_SHORT).show();
                }
            }


        });
    }



}
