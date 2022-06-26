package com.ensias.ebarber.bookingDashboard;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.ensias.ebarber.R;
import com.ensias.ebarber.model.Services;

import java.util.List;

public class ServicesAdapter extends RecyclerView.Adapter<ServicesAdapter.MyServicesAppointementHolder> {

    Context context;
    List<Services> serviceList;
    ServiceAdapterListener serviceAdapterListener;
    String userType;
    private int selectedIndex=0;


    public ServicesAdapter(Context context, List<Services> TempList,ServiceAdapterListener listener) {

        this.serviceList = TempList;
        this.context = context;
        this.serviceAdapterListener = listener;
        this.userType = userType;
    }

    @NonNull
    @Override
    public ServicesAdapter.MyServicesAppointementHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.barber_service_item, parent, false);

        ServicesAdapter.MyServicesAppointementHolder viewHolder = new ServicesAdapter.MyServicesAppointementHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ServicesAdapter.MyServicesAppointementHolder myServicesAppointementHolder, int position) {
        myServicesAppointementHolder.name.setText(serviceList.get(myServicesAppointementHolder.getAdapterPosition()).getServiceName());
        myServicesAppointementHolder.charges.setText(serviceList.get(myServicesAppointementHolder.getAdapterPosition()).getServiceCharges()+"");

        myServicesAppointementHolder.imageView.setImageResource(context.getResources().getIdentifier(serviceList.get(myServicesAppointementHolder.getAdapterPosition()).getImageName(), "drawable", context.getPackageName()));
        myServicesAppointementHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedIndex=myServicesAppointementHolder.getAdapterPosition();
                serviceAdapterListener.onServiceCardClick(serviceList.get(myServicesAppointementHolder.getAdapterPosition()).getServiceName(),serviceList.get(myServicesAppointementHolder.getAdapterPosition()).getServiceCharges());
                notifyDataSetChanged();

            }
        });
        if(myServicesAppointementHolder.getAdapterPosition()==selectedIndex){
            myServicesAppointementHolder.mainConst.setBackgroundColor(Color.parseColor("#439157"));
        }else {
            myServicesAppointementHolder.mainConst.setBackgroundColor(Color.parseColor("#3c4652"));
        }
    }


    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    class MyServicesAppointementHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;
        public TextView name,charges;
        public ConstraintLayout mainConst;
        public MyServicesAppointementHolder(@NonNull View itemView) {
            super(itemView);
            this.name = (TextView) itemView.findViewById(R.id.item_title);
            this.charges = (TextView) itemView.findViewById(R.id.item_price);
            this.imageView = (ImageView)itemView.findViewById(R.id.item_icon);
            this.mainConst=(ConstraintLayout) itemView.findViewById(R.id.main_layout);

        }
    }
    public interface ServiceAdapterListener{
        public void onServiceCardClick(String name,int charges);
    }
}
