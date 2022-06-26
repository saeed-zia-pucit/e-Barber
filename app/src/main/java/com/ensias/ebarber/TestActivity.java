package com.ensias.ebarber;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ensias.ebarber.Common.Common;
import com.ensias.ebarber.Common.NonSwipeViewPager;
import com.ensias.ebarber.adapter.MyViewPagerAdapter;
import com.shuhart.stepview.StepView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Unbinder;

import static com.ensias.ebarber.Common.Common.step;

public class TestActivity extends BaseActivity {

    StepView stepView;
    NonSwipeViewPager viewPager;
    Button btn_previous_step;
    Button btn_next_step;
    Unbinder unbinder;
    LocalBroadcastManager localBroadcastManager;
    private BroadcastReceiver buttonNextReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(step == 2){
                Common.currentTimeSlot = intent.getIntExtra(Common.KEY_TIME_SLOT,-1);
            }
            btn_next_step.setEnabled(true);
            setColorButton();
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        stepView = findViewById(R.id.step_view);
        viewPager = findViewById(R.id.view_pager);
        btn_next_step  = findViewById(R.id.btn_next_step);
        btn_previous_step = findViewById(R.id.btn_previous_step);

        setupStepView();
        setColorButton();

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(buttonNextReceiver, new IntentFilter(Common.KEY_ENABLE_BUTTON_NEXT));

        viewPager.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                setColorButton();

            }

            @Override
            public void onPageSelected(int position) {
                stepView.go(position,true);
                if( position == 0) {
                    btn_previous_step.setEnabled(false);
                    btn_next_step.setVisibility(View.VISIBLE);
                }
                else
                    btn_previous_step.setEnabled(true);
                if(position == 2)
                    btn_next_step.setEnabled(false);
                else
                    btn_next_step.setEnabled(true);


                setColorButton();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        btn_next_step.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(step < 3 || step == 0 ){
                    step++ ;
                  //  Common.Currentaappointementatype=spinner.getSelectedItem().toString();
                   // Log.e("Spinnr", Common.Currentaappointementatype);

//                    if(step==1){
//                        if(Common.CurreentDoctor != null) {
//                            Common.currentTimeSlot = -1;
//                            Common.currentDate = Calendar.getInstance();
//                            loadTimeSlotOfDoctor(Common.CurreentDoctor);
//                        }
//                    }
                     if(step == 1){
                       // if(Common.currentTimeSlot != -1)
                            confirmeBooking();
                    }
                    viewPager.setCurrentItem(step);
                }


            }
        });
        btn_previous_step.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(step == 3 || step > 0 ){
                    step-- ;
                    viewPager.setCurrentItem(step);
                }
            }
        });

        loadTimeSlotOfDoctor("testdoc@testdoc.com");
    }



    private void confirmeBooking() {

        Intent intent = new Intent(Common.KEY_CONFIRM_BOOKING);
        localBroadcastManager.sendBroadcast(intent);
       // btn_previous_step.setVisibility(View.GONE);
        btn_next_step.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        step = 0 ;
        localBroadcastManager.unregisterReceiver(buttonNextReceiver);
        super.onDestroy();
    }

    private void loadTimeSlotOfDoctor(String doctorId) {
        Intent intent = new Intent(Common.KEY_DISPLAY_TIME_SLOT);
        localBroadcastManager.sendBroadcast(intent);
    }

    private void setColorButton() {
        if(btn_previous_step.isEnabled()){
            btn_previous_step.setBackgroundResource(R.color.design_default_color_primary_dark);
        }
        else{
            btn_previous_step.setBackgroundResource(R.color.colorAccent);
        }
        if(btn_next_step.isEnabled()){
            btn_next_step.setBackgroundResource(R.color.design_default_color_primary_dark);
        }
        else{
            btn_next_step.setBackgroundResource(R.color.colorAccent);
        }
    }

    private void setupStepView() {
        List<String> stepList = new ArrayList<>();
        stepList.add(getString(R.string.time_and_date));
        stepList.add(getString(R.string.appointment_details));
        stepView.setSteps(stepList);

    }

}
