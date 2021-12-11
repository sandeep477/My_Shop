package com.example.myshop.ui.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myshop.R
import com.example.myshop.model.Address
import com.example.myshop.ui.activities.AddEditAddressActivity
import com.example.myshop.ui.activities.CheckoutActivity
import com.example.myshop.ui.activities.Constants
import kotlinx.android.synthetic.main.item_address_layout.view.*

open class AddressListAdapter(private val context:Context,
                              private val addressList:ArrayList<Address>,private val selectedAddress:Boolean):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.item_address_layout,parent,false)
        )
    }
    fun notifyEditItem(activity: Activity, position:Int)
    {
        val intent = Intent(context, AddEditAddressActivity::class.java)
        intent.putExtra(Constants.EXTRA_ADDRESS_EDIT_DETAILS,addressList[position])
        activity.startActivityForResult(intent,Constants.ADD_ADRESS_REQUEST_CODE)
        notifyItemChanged(position)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
       val model =addressList[position]
        if(holder is MyViewHolder){
            holder.itemView.tv_address_full_name.text = model.name
            holder.itemView.tv_address_type.text=model.type
            holder.itemView.tv_address_mobile_number.text = model.mobileNumber
            holder.itemView.tv_address_details.text = "${model.address},${model.zipCode}"

        }
        if(selectedAddress)
        {
            holder.itemView.setOnClickListener{
            val intent = Intent(context,CheckoutActivity::class.java)
                intent.putExtra(Constants.EXTRA_ADDRESS_SELECTED,model)
                context.startActivity(intent)
            }
        }

    }

    override fun getItemCount(): Int {
        return addressList.size
    }
    class MyViewHolder(view: View):RecyclerView.ViewHolder(view)
}