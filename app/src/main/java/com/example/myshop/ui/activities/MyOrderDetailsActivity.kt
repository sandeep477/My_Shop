package com.example.myshop.ui.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myshop.Order
import com.example.myshop.R
import com.example.myshop.ui.adapter.CartItemAdapter
import kotlinx.android.synthetic.main.activity_cartlist.*
import kotlinx.android.synthetic.main.activity_my_order_details.*
import kotlinx.android.synthetic.main.activity_settings.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MyOrderDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_order_details)

        setUpActionBAr()
        var myOrderDetails:Order = Order()
        if(intent.hasExtra(Constants.EXTRA_MY_ORDER_DETAILS))
        {
            myOrderDetails = intent.getParcelableExtra<Order>(Constants.EXTRA_MY_ORDER_DETAILS)!!
        }
        setupUI(myOrderDetails)
    }
    private fun setUpActionBAr()
    {
        setSupportActionBar(toolbar_my_order_details_activity)
        val actionbar = supportActionBar
        if(actionbar != null)
        {
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24)
        }
        toolbar_my_order_details_activity.setNavigationOnClickListener { onBackPressed() }
    }
    @SuppressLint("SetTextI18n")
    private fun setupUI(orderDetails:Order)
    {
        tv_sold_product_details_id.text = orderDetails.title

        val dateFormat = "dd MMM yyyy HH:MM"
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        val calender:Calendar = Calendar.getInstance()
        calender.timeInMillis = orderDetails.order_datetime
        val orderDateTime = formatter.format(calender.time)
        tv_sold_product_details_date_.text= orderDateTime

        val diffInMilliSecond:Long =System.currentTimeMillis() - orderDetails.order_datetime
        val diffInHour:Long = TimeUnit.MILLISECONDS.toHours(diffInMilliSecond)

        when{
            diffInHour < 1 ->{
                tv_order_status.text = resources.getString(R.string.order_status_pending)
                tv_order_status.setTextColor(
                        ContextCompat.getColor(this@MyOrderDetailsActivity,R.color.colorAccent)
                )
            }
            diffInHour < 2 ->{
                tv_order_status.text = resources.getString(R.string.order_status_in_process)
                tv_order_status.setTextColor(
                        ContextCompat.getColor(this@MyOrderDetailsActivity,R.color.colorOrderStatusInProgress)
                )
            }
            else->{
                tv_order_status.text = resources.getString(R.string.order_status_deliverd)
                tv_order_status.setTextColor(
                        ContextCompat.getColor(this@MyOrderDetailsActivity,R.color.colorOrderStatusDelivered)
                )
            }

         }
        rv_my_order_items_list.layoutManager = LinearLayoutManager(this@MyOrderDetailsActivity)
        rv_my_order_items_list.setHasFixedSize(true)
        val cartListAdapter = CartItemAdapter(this@MyOrderDetailsActivity,orderDetails.items,false)
        rv_my_order_items_list.adapter = cartListAdapter

        tv_my_order_details_address_type.text = orderDetails.address.type
        tv_my_order_details_full_name.text = orderDetails.address.name
        tv_my_order_details_address.text ="${orderDetails.address.address},${orderDetails.address.zipCode}"
        tv_my_order_details_additional_note.text = orderDetails.address.additionalNote
        if(orderDetails.address.otherDetails.isNotEmpty())
        {
            tv_my_order_details_other_details.visibility = View.VISIBLE
            tv_my_order_details_other_details.text = orderDetails.address.otherDetails
        }
        else{
            tv_my_order_details_other_details.visibility = View.GONE
        }
        tv_my_order_details_mobile_number.text = orderDetails.address.mobileNumber
        tv_order_details_sub_total.text = orderDetails.sub_total_amount
        tv_order_details_shipping_charge.text=orderDetails.shipping_charge
        tv_order_details_total_amount.text  =orderDetails.total_amount
    }
}