package com.example.myshop.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.myshop.R
import com.example.myshop.firestore.FireStoreClass
import com.example.myshop.model.CartItem
import com.example.myshop.ui.activities.CartlistActivity
import com.example.myshop.ui.activities.Constants
import com.example.myshop.utils.GlideLoader
import kotlinx.android.synthetic.main.item_cart_layout.view.*
import kotlinx.android.synthetic.main.item_list_layout.view.*


class CartItemAdapter(
        val context:Context,
        private val list: ArrayList<CartItem>,
        private val updateCartItem:Boolean
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_cart_layout,
        parent,
            false
        ))
    }

    @SuppressLint("StringFormatInvalid")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyViewHolder) {
            GlideLoader(context).loadProductPicture(model.image, holder.itemView.iv_cart_item_image)
            holder.itemView.tv_cart_item_title.text = model.title
            holder.itemView.tv_cart_item_price.text = model.price
            holder.itemView.tv_cart_quantity.text = model.cartQuantity
            if (model.cartQuantity == "0") {
                if (updateCartItem) {
                    holder.itemView.ib_delete_cart_item.visibility = View.GONE
                } else {
                    holder.itemView.ib_delete_cart_item.visibility = View.VISIBLE
                }

                holder.itemView.ib_remove_cart_item.visibility = View.GONE
                holder.itemView.ib_add_cart_item.visibility = View.GONE
                holder.itemView.tv_cart_quantity.text = context.resources.getString(R.string.lbl_out_of_stock)
                holder.itemView.tv_cart_quantity.setTextColor(
                        ContextCompat.getColor(context, R.color.colorSnackBarError)
                )
            } else {
                if (updateCartItem) {
                    holder.itemView.ib_remove_cart_item.visibility = View.VISIBLE
                    holder.itemView.ib_add_cart_item.visibility = View.VISIBLE
                    holder.itemView.ib_delete_cart_item.visibility = View.VISIBLE
                } else {
                    holder.itemView.ib_remove_cart_item.visibility = View.GONE
                    holder.itemView.ib_add_cart_item.visibility = View.GONE
                    holder.itemView.ib_delete_cart_item.visibility = View.GONE
                }

//                holder.itemView.tv_cart_quantity.text = context.resources.getString(R.string.lbl_out_of_stock)
                holder.itemView.tv_cart_quantity.setTextColor(
                        ContextCompat.getColor(context, R.color.colorSecondaryText)
                )
            }

            holder.itemView.ib_delete_cart_item.setOnClickListener {
                when (context) {
                    is CartlistActivity -> {
                        context.showprogressBar(context.resources.getString(R.string.please_wait))
                    }
                }
                FireStoreClass().removeItemFromCart(context, model.id)
            }
            holder.itemView.ib_remove_cart_item.setOnClickListener {
                if (model.cartQuantity == "1") {
                    FireStoreClass().removeItemFromCart(context, model.id)
                } else {
                    val cartQuantity = model.cartQuantity.toInt()
                    val itemHashMap = HashMap<String, Any>()
                    itemHashMap[Constants.CART_QUANTITY] = (cartQuantity - 1).toString()


                    if (context is CartlistActivity) {
                        context.showprogressBar(context.resources.getString(R.string.please_wait))

                    }
                    FireStoreClass().updateMyCart(context, model.id, itemHashMap)
                }
            }
            holder.itemView.ib_add_cart_item.setOnClickListener {
                val cartQuantity = model.cartQuantity.toInt()
                val itemHashMap = HashMap<String, Any>()
                if (cartQuantity + 1 <= model.stock_quantity.toInt()) {
                    itemHashMap[Constants.CART_QUANTITY] = (cartQuantity + 1).toString()


                    if (context is CartlistActivity) {
                        context.showprogressBar(context.resources.getString(R.string.please_wait))

                    }
                    FireStoreClass().updateMyCart(context, model.id, itemHashMap)

                } else {
                    if (context is CartlistActivity) {
                        context.showErrorSnackBar(
                                context.resources.getString(
                                        R.string.msg_for_avail_stock,
                                        model.stock_quantity
                                ), true
                        )
                    }
                }
            }
            }

    }

    override fun getItemCount(): Int {
        return list.size
    }
    private class MyViewHolder(view: View):RecyclerView.ViewHolder(view)

}