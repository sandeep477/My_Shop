package com.example.myshop.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.myshop.R
import com.example.myshop.TextView
import com.example.myshop.model.Product
import com.example.myshop.ui.activities.Constants
import com.example.myshop.ui.activities.ProductDetailsActivity
import com.example.myshop.ui.adapter.ProductListAdaper.ViewHolder
import com.example.myshop.ui.ui.fragments.ProductsFragment
import com.example.myshop.utils.GlideLoader
import kotlinx.android.synthetic.main.item_list_layout.view.*

class ProductListAdaper(val context: Context,var listItem:ArrayList<Product>,val fragment: ProductsFragment):RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context)
            .inflate(R.layout.item_list_layout,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item:Product = listItem[position]
        GlideLoader(context).loadProductPicture(item.image,holder.image)
        holder.name.text=item.title
        holder.price.text=item.price
        holder.delete.setOnClickListener {
            fragment.delete_Item(item.product_id)
        }
        holder.view.setOnClickListener {
            val intent = Intent(context,ProductDetailsActivity::class.java)
            intent.putExtra(Constants.EXTRA_PRODUCT_OWNER,item.user_id)
            intent.putExtra(Constants.EXTRA_PRODUCT_ID,item.product_id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return listItem.size
    }
    class ViewHolder(val view:View):RecyclerView.ViewHolder(view){
         val image: ImageView = view.iv_item_image
        val name: TextView = view.tv_item_name
        val price: TextView = view.tv_item_price
        val delete: ImageButton = view.ib_delete_product
    }
}

