package com.example.myshop.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myshop.Order
import com.example.myshop.R
import com.example.myshop.firestore.FireStoreClass
import com.example.myshop.model.Address
import com.example.myshop.model.CartItem
import com.example.myshop.model.Product
import com.example.myshop.ui.adapter.CartItemAdapter
import kotlinx.android.synthetic.main.activity_checkout_activity.*

class CheckoutActivity : BaseActivity() {
    private var mAddressDetails :Address?=null
    private lateinit var mProductlist:ArrayList<Product>
    private lateinit var mCartItem: ArrayList<CartItem>
    private var mSubTotal:Double=0.0
    private var mTotalAmount:Double=0.0
    private lateinit var mOrderDetails:Order
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout_activity)
        setUpActionBAr()
        if(intent.hasExtra(Constants.EXTRA_ADDRESS_SELECTED))
        {
            mAddressDetails = intent.getParcelableExtra(Constants.EXTRA_ADDRESS_SELECTED)
        }
        if(mAddressDetails !=null)
        {
            tv_checkout_address_type.text = mAddressDetails!!.type
            tv_checkout_full_name.text = mAddressDetails!!.name
            tv_checkout_address.text ="${mAddressDetails!!.address},${mAddressDetails!!.zipCode}"
            tv_checkout_additional_note.text = mAddressDetails!!.additionalNote
            if(mAddressDetails!!.otherDetails.isNotEmpty())
            {
                tv_checkout_other_details.text = mAddressDetails!!.otherDetails
            }
            tv_checkout_mobile_number.text = mAddressDetails!!.mobileNumber
        }
        getProductList()
        btn_place_order.setOnClickListener {
            placeAnOrder()
        }
    }
    private fun setUpActionBAr()
    {
        setSupportActionBar(toolbar_checkout_activity)
        val actionbar = supportActionBar
        if(actionbar != null)
        {
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24)
        }
        toolbar_checkout_activity.setNavigationOnClickListener { onBackPressed() }

    }
    private fun getCartItem()
    {
        FireStoreClass().getCartItems(this)
    }
    @SuppressLint("SetTextI18n")
    fun successCartItemList(cartList:ArrayList<CartItem>)
    {   hideprogressBar()
        for(product in mProductlist)
        {
            for(cartItem in cartList)
            {
                if(product.product_id==cartItem.product_id)
                {
                    cartItem.stock_quantity = product.stock_quantity
                }
            }
        }
        rv_cart_list_items.layoutManager = LinearLayoutManager(this@CheckoutActivity)
        rv_cart_list_items.setHasFixedSize(true)
        val adapter = CartItemAdapter(this,cartList,false)
        rv_cart_list_items.adapter = adapter
        mCartItem = cartList
        for(item in mCartItem)
        {
            val availableQuantity =item.stock_quantity.toInt()
            if(availableQuantity>0)
            {
                val price = item.price.toInt()
                val quantity=item.cartQuantity.toInt()
                mSubTotal += (price*quantity)
            }
        }
        tv_checkout_sub_total.text = "$$mSubTotal"
        tv_checkout_shipping_charge.text= "$10.0"
        if(mSubTotal>0)
        {
            ll_checkout_place_order.visibility= View.VISIBLE
            mTotalAmount = mSubTotal+10.0
            tv_checkout_total_amount.text="$$mTotalAmount"
        }
        else{
            ll_checkout_place_order.visibility = View.GONE
        }
    }
    fun successProductListFromFireStore(productlist:ArrayList<Product>)
    { mProductlist = productlist
        getCartItem()
    }
    fun getProductList()
    {
        showprogressBar(resources.getString(R.string.please_wait))
        FireStoreClass().getAllProductList(this)
    }
    fun placeAnOrder()
    {          showprogressBar(resources.getString(R.string.please_wait))
        if(mAddressDetails !=null){
         mOrderDetails = Order(
                FireStoreClass().getCurrentUserID(),
                mCartItem,
                mAddressDetails!!,
                "My order ${System.currentTimeMillis()}",
                mCartItem[0].image,
                mSubTotal.toString(),
                "10.0",
                mTotalAmount.toString(),
                System.currentTimeMillis())
        }
        showprogressBar(resources.getString(R.string.please_wait))
        FireStoreClass().placeOrder(this@CheckoutActivity,mOrderDetails)
    }
    fun successPlaceOrder()
    {
     FireStoreClass().updateAllDetails(this,mCartItem,mOrderDetails)
    }
    fun allDetailsUpdatedSuccessfully()
    {
        hideprogressBar()
        Toast.makeText(this,"Your order was paced successfully",Toast.LENGTH_SHORT).show()
        val intent = Intent(this@CheckoutActivity,DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

}