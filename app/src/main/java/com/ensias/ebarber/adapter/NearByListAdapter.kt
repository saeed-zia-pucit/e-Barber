package com.ensias.ebarber.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ensias.ebarber.DoctorAdapterFilteredAdmin
import com.ensias.ebarber.Interface.MyCallBack
import com.ensias.ebarber.Interface.NearByListener
import com.ensias.ebarber.R
import com.ensias.ebarber.model.Doctor
import com.ensias.ebarber.model.MarkerData
import java.text.DecimalFormat
import kotlin.math.round


class NearByListAdapter(private val mList: ArrayList<MarkerData>,private  val myCallBack: NearByListener) : RecyclerView.Adapter<NearByListAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.near_by_salon_item, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.title.setText(mList.get(position).title);
        var dist:Double= mList.get(position).distance/1000
        val df = DecimalFormat("0.00")
       holder.distance.setText("distance: "+df.format(dist)+ "km")
        holder.mapButton.setOnClickListener{
            myCallBack.onMapButtonClick(position)
        }
        holder.bookButton.setOnClickListener{
            myCallBack.onBookButton(position)
        }
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
//        val imageView: ImageView = itemView.findViewById(R.id.doctor_item_image)
        val title: TextView = itemView.findViewById(R.id.salon_title)
        val distance: TextView = itemView.findViewById(R.id.distance)
        val mapButton : Button = itemView.findViewById(R.id.map_btn);
        val bookButton : Button = itemView.findViewById(R.id.book_btn);
    }

}