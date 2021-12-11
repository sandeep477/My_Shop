package com.example.myshop.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myshop.R
import com.example.myshop.ui.activities.Constants
import com.example.myshop.ui.activities.SoldProductDetailsActivity
import com.example.myshop.utils.GlideLoader
import com.myshoppal.models.SoldProduct
import kotlinx.android.synthetic.main.item_list_layout.view.*

class SoldItemAdapter(val context: Context, private val soldProductList:ArrayList<SoldProduct>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_list_layout,parent,false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = soldProductList[position]
        if(holder is MyViewHolder){
            GlideLoader(context).loadProductPicture(model.image,holder.itemView.iv_item_image)
            holder.itemView.tv_item_name.text = model.title
            holder.itemView.tv_item_price.text = "$${model.price}"
            holder.itemView.ib_delete_product.visibility = View.GONE
            holder.itemView.setOnClickListener {
                val intent  = Intent(context,SoldProductDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_SOLD_PRODUCT_DETAILS,model)
                context.startActivity(intent)
            }
        }

    }

    override fun getItemCount(): Int {
        return soldProductList.size
    }
    class MyViewHolder(view: View):RecyclerView.ViewHolder(view)
}