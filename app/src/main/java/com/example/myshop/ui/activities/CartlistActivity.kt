package com.example.myshop.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myshop.R
import com.example.myshop.firestore.FireStoreClass
import com.example.myshop.model.CartItem
import com.example.myshop.model.Product
import com.example.myshop.ui.adapter.CartItemAdapter
import kotlinx.android.synthetic.main.activity_cartlist.*
import kotlinx.android.synthetic.main.activity_register.*

class CartlistActivity : BaseActivity() {
private lateinit var mProductList: ArrayList<Product>
private lateinit var mcartItem:ArrayList<CartItem>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cartlist)
        setupActionBar()
        btn_checkout.setOnClickListener {
            val intent = Intent(this@CartlistActivity,AddressListActivity::class.java)
            intent.putExtra(Constants.EXTRA_ADDRESS_SELECT,true)
            startActivity(intent)
        }
    }
    private fun setupActionBar() {

        setSupportActionBar(toolbar_cart_list_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24)
        }

        toolbar_cart_list_activity.setNavigationOnClickListener { onBackPressed() }
    }
    @SuppressLint("SetTextI18n")


    private fun getProductList()
    {
        showprogressBar(resources.getString(R.string.please_wait))
        FireStoreClass().getAllProductList(this@CartlistActivity)
    }
    fun successProductListFromFireStore(productlist:ArrayList<Product>)
    {    hideprogressBar()
         mProductList = productlist
         getCartItem()
    }

    private fun getCartItem()
    {
        //showprogressBar(resources.getString(R.string.please_wait))
        FireStoreClass().getCartItems(this@CartlistActivity)
    }
    fun successCartItemList(cartList:ArrayList<CartItem>)
    {
        hideprogressBar()
        for(product in mProductList)
        {
            for(cart in cartList)
            {
                if(product.product_id == cart.product_id) {
                    cart.stock_quantity = product.stock_quantity
                }
                if(product.stock_quantity.toInt()==0) {
                    cart.cartQuantity = product.stock_quantity
                }
            }
        }
        mcartItem = cartList
        if(cartList.size > 0) {
            rv_cart_items_list.visibility = View.VISIBLE
            ll_checkout.visibility = View.VISIBLE
            tv_no_cart_item_found.visibility = View.GONE
            rv_cart_items_list.layoutManager = LinearLayoutManager(this@CartlistActivity)
            rv_cart_items_list.setHasFixedSize(true)
            val cartListAdapter = CartItemAdapter(this, mcartItem,true)
            rv_cart_items_list.adapter = cartListAdapter
            var subTotal:Double = 0.0
            for (item in cartList) {
                val availableQuantity = item.stock_quantity.toInt()
                if(availableQuantity>0) {
                    val price = item.price
                    val quantity = item.cartQuantity
                    subTotal += (price.toDouble() * quantity.toInt())
                    tv_sub_total.text = "$$subTotal"
                    tv_shipping_charge.text = "$10.0"
                }
            }
            if (subTotal > 0) {
                ll_checkout.visibility = View.VISIBLE
                val total = subTotal + 10
                tv_total_amount.text = "$ $total"
            } else {
                rv_cart_items_list.visibility = View.GONE
                ll_checkout.visibility = View.GONE
                tv_no_cart_item_found.visibility = View.VISIBLE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        //getCartItem()
        getProductList()
    }
    fun itemRemovedSuccess()
    {
        hideprogressBar()
        Toast.makeText(this,
            resources.getString(R.string.mss_item_removes_success),
            Toast.LENGTH_SHORT).show()
        getCartItem()
    }
    fun itemUpdateSuccess(){
        hideprogressBar()
        getCartItem()
    }

}