package com.example.myshop.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CartItem(
        val user_id:String="",
        val product_owner_id:String="",
        val product_id:String="",
        val title :String="",
        val price:String="",
        val image:String="",
        var cartQuantity:String="",
        var stock_quantity:String="",
        var id:String=""
):Parcelable
