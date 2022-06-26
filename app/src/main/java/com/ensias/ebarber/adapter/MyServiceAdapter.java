package com.ensias.ebarber.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ensias.ebarber.Common.Common;
import com.ensias.ebarber.R;
import com.ensias.ebarber.model.Services;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.storage.StorageReference;


public class MyServiceAdapter extends FirestoreRecyclerAdapter<Services, MyServiceAdapter.MyServicesAppointementHolder> {
    StorageReference pathReference ;
    ServiceAdapterListener serviceAdapterListener;
    String userType;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.
     * @param options
     */
    public MyServiceAdapter(@NonNull FirestoreRecyclerOptions<Services> options,ServiceAdapterListener listener,String userType) {
        super(options);
        this.serviceAdapterListener = listener;
        this.userType = userType;
    }

    @Override
    protected void onBindViewHolder(@NonNull MyServicesAppointementHolder myServicesAppointementHolder, int i, @NonNull Services services) {
        myServicesAppointementHolder.name.setText(services.getServiceName());
        myServicesAppointementHolder.charges.setText("Charges: $"+services.getServiceCharges()+"");
        if(userType.equals("Customer")){
            myServicesAppointementHolder.deleteBtn.setText("Book");
            myServicesAppointementHolder.UpdateBtn.setVisibility(View.GONE);
        }
        //  holder.imageView.setImageResource(listdata[position].getImgId());
//        myServicesAppointementHolder.relativeLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                   Toast.makeText(view.getContext(),"click on item: "+services.getServiceName(),Toast.LENGTH_LONG).show();
//            }
//        });
        myServicesAppointementHolder.UpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serviceAdapterListener.updateButtonClicked(services.getServiceName(),services.getServiceCharges());
            }
        });
        myServicesAppointementHolder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.currentOrderPrice=services.getServiceCharges();
               serviceAdapterListener.deleteButtonClicked(services.getServiceName());
            }
        });
    }


    @NonNull
    @Override
    public MyServicesAppointementHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_item, parent, false);
        return new MyServicesAppointementHolder(v);
    }

    class MyServicesAppointementHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;
        public TextView name,charges;
        public Button UpdateBtn,deleteBtn;
        public RelativeLayout relativeLayout;
        public MyServicesAppointementHolder(@NonNull View itemView) {
            super(itemView);
            this.name = (TextView) itemView.findViewById(R.id.service_title);
            this.charges = (TextView) itemView.findViewById(R.id.text_view_price);
            this.UpdateBtn = (Button) itemView.findViewById(R.id.bookBtn);
            this.deleteBtn = (Button) itemView.findViewById(R.id.deleteBtn);

            relativeLayout = (RelativeLayout)itemView.findViewById(R.id.relativeLayout);

        }
    }

public interface ServiceAdapterListener{
        public void updateButtonClicked(String name,int charges);
        public void deleteButtonClicked(String Servicename);
}


}

