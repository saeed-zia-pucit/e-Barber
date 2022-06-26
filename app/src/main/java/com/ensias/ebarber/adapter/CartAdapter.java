package com.ensias.ebarber.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ensias.ebarber.Common.Common;
import com.ensias.ebarber.R;
import com.ensias.ebarber.model.CartItem;
import com.ensias.ebarber.model.ImageUploadInfo;
import com.ensias.ebarber.model.Services;

import java.util.List;


public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    Context context;
    List<CartItem> servicesList;

    public CartAdapter(Context context, List<CartItem> TempList) {

        this.servicesList = TempList;

        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CartItem service = Common.cartItems.get(position);

        holder.NameTextView.setText(service.getServiceName());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.cartItems.remove(holder.getAdapterPosition());
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {

        return Common.cartItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView NameTextView;
        public Button delete,bookButton;

        public ViewHolder(View itemView) {
            super(itemView);

            delete=(Button)itemView.findViewById(R.id.deleteBtn);
            bookButton=(Button)itemView.findViewById(R.id.bookBtn);
            bookButton.setVisibility(View.GONE);

            NameTextView = (TextView) itemView.findViewById(R.id.service_title);
        }
    }
}
