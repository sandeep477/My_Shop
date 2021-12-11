package com.example.myshop.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myshop.Order
import com.example.myshop.R
import com.example.myshop.ui.activities.Constants
import com.example.myshop.ui.activities.MyOrderDetailsActivity
import com.example.myshop.utils.GlideLoader
import kotlinx.android.synthetic.main.item_list_layout.view.*

class MyOrderListAdapter(var context: Context, private var orderList:ArrayList<Order>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
       return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_list_layout,parent,false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = orderList[position]
        if(holder is MyViewHolder)
        {
            GlideLoader(context).loadProductPicture(model.image,holder.itemView.iv_item_image)
            holder.itemView.tv_item_price.text = "$${model.total_amount}"
            holder.itemView.tv_item_name.text=model.title
            holder.itemView.ib_delete_product.visibility = View.GONE
            holder.itemView.setOnClickListener {
                val intent = Intent(context, MyOrderDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_MY_ORDER_DETAILS,model)
                context.startActivity(intent)
            }

        }

    }

    override fun getItemCount(): Int {
       return orderList.size
    }
    class MyViewHolder(view: View):RecyclerView.ViewHolder(view)
}