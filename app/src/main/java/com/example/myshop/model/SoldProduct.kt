package com.myshoppal.models

import android.os.Parcelable
import com.example.myshop.model.Address
import kotlinx.android.parcel.Parcelize

/**
 * A data model class for Sold Product with required fields.
 */
@Parcelize
data class SoldProduct(
        val user_id: String = "",
        val title: String = "",
        val price: String = "",
        val sold_quantity: String = "",
        val image: String = "",
        val order_id: String = "",
        val order_date: Long = 0L,
        val sub_total_amount: String = "",
        val total_amount: String = "",
       var id: String = "",
        val shipping_charge: String = "",
        val address: Address = Address(),
) : Parcelable
// TODO Step 5: Create a data model class for Sold Product with required fields.
// START
// END