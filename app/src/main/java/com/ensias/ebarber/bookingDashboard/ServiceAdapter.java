package com.ensias.ebarber.bookingDashboard;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.ensias.ebarber.Common.Common;
import com.ensias.ebarber.R;
import com.ensias.ebarber.model.Services;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.storage.StorageReference;



public class ServiceAdapter extends FirestoreRecyclerAdapter<Services, ServiceAdapter.MyServicesAppointementHolder> {
    StorageReference pathReference ;
    ServiceAdapterListener serviceAdapterListener;
    String userType;
    private int selectedIndex=-1;
    Context context;


    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.
     * @param options
     */
    public ServiceAdapter(@NonNull FirestoreRecyclerOptions<Services> options, ServiceAdapterListener listener, String userType,Context context) {
        super(options);
        this.serviceAdapterListener = listener;
        this.userType = userType;
        this.context=context;
    }

    @Override
    protected void onBindViewHolder(@NonNull MyServicesAppointementHolder myServicesAppointementHolder, int i, @NonNull Services services) {
        myServicesAppointementHolder.name.setText(services.getServiceName());
        myServicesAppointementHolder.charges.setText(services.getServiceCharges()+"");
      if(services.getImageName() != null) {
          myServicesAppointementHolder.imageView.setImageResource(context.getResources().getIdentifier(services.getImageName(), "drawable", context.getPackageName()));
      }
        myServicesAppointementHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedIndex=myServicesAppointementHolder.getAdapterPosition();
             //   serviceAdapterListener.onServiceCardClick(services.getServiceName(),services.getServiceCharges());
                serviceAdapterListener.onServiceCardClick(services,myServicesAppointementHolder.imageView);

                notifyDataSetChanged();

            }
        });
        if(myServicesAppointementHolder.getAdapterPosition()==selectedIndex){
            myServicesAppointementHolder.mainConst.setBackgroundColor(Color.parseColor("#439157"));
        }else {
            myServicesAppointementHolder.mainConst.setBackgroundColor(Color.parseColor("#3c4652"));
        }
        //  holder.imageView.setImageResource(listdata[position].getImgId());
//        myServicesAppointementHolder.relativeLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                   Toast.makeText(view.getContext(),"click on item: "+services.getServiceName(),Toast.LENGTH_LONG).show();
//            }
//        });

    }


    @NonNull
    @Override
    public MyServicesAppointementHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.barber_service_item, parent, false);
        return new ServiceAdapter.MyServicesAppointementHolder(v);
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
        public void onServiceCardClick(Services service,ImageView imageView);
    }

}


