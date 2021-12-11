package com.example.myshop.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.myshop.R
import com.example.myshop.model.Product
import com.example.myshop.ui.activities.Constants
import com.example.myshop.ui.activities.ProductDetailsActivity
import com.example.myshop.utils.BoldTextView
import com.example.myshop.utils.GlideLoader
import kotlinx.android.synthetic.main.item_dashboard_layout.view.*

class DashboardAdapter(val context: Context, private val items:ArrayList<Product>): RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_dashboard_layout,parent,false)
        )
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val productdata = items[position]
        GlideLoader(context).loadProductPicture(productdata.image,holder.imagedashboard)
        holder.titledashboard.text = productdata.title
        holder.pricedashboard.text=productdata.price
        holder.view.setOnClickListener {
            val intent = Intent(context,ProductDetailsActivity::class.java)
            intent.putExtra(Constants.EXTRA_PRODUCT_ID,productdata.product_id)
            intent.putExtra(Constants.EXTRA_PRODUCT_OWNER,productdata.user_id)
            context.startActivity(intent)
        }
    }
    override fun getItemCount(): Int {
        return items.size
    }
}
class ViewHolder(val view: View):RecyclerView.ViewHolder(view)
{
    val imagedashboard: ImageView = view.iv_dashboard_item_image
    val titledashboard: BoldTextView = view.tv_dashboard_item_title
    val pricedashboard: BoldTextView = view.tv_dashboard_item_price
}