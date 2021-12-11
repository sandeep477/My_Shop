  package com.example.myshop.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.myshop.R
import com.example.myshop.firestore.FireStoreClass
import com.example.myshop.model.CartItem
import com.example.myshop.model.Product
import com.example.myshop.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_product_details.*
import kotlinx.android.synthetic.main.activity_products.*

  class ProductDetailsActivity : BaseActivity(),View.OnClickListener {

   private lateinit var ProductDetails:Product
    private  lateinit var  ProductID:String
    private lateinit var ProductOwnerID:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)
        setUpActionBar()
        ProductOwnerID=""
        if(intent.hasExtra(Constants.EXTRA_PRODUCT_ID))
        {
            ProductID = intent.getStringExtra(Constants.EXTRA_PRODUCT_ID).toString()
        }
        if(intent.hasExtra(Constants.EXTRA_PRODUCT_OWNER))
        {
            ProductOwnerID= intent.getStringExtra(Constants.EXTRA_PRODUCT_OWNER).toString()
        }
        if(ProductOwnerID.equals(FireStoreClass().getCurrentUserID()))
        {
            btn_add_to_cart.visibility=View.GONE
            btn_go_to_cart.visibility=View.GONE
        }
        else{
            btn_add_to_cart.visibility=View.VISIBLE
        }
         getProductDetails(ProductID)
        btn_go_to_cart.setOnClickListener(this)
        btn_add_to_cart.setOnClickListener(this)
    }
    fun setUpActionBar()
    {
        setSupportActionBar(toolbar_product_details_activity)
        val actionbar = supportActionBar
        if(actionbar != null)
        {
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24)
        }
        toolbar_product_details_activity.setNavigationOnClickListener { onBackPressed()  }
    }
    fun getProductDetails(productID:String)
    {
        showprogressBar(resources.getString(R.string.please_wait))
        FireStoreClass().getProductsDetail_(this@ProductDetailsActivity,productID)
    }
    fun successProductDetails(product: Product)
    { ProductDetails = product
        GlideLoader(this).loadProductPicture(product.image,iv_product_detail_image)
        tv_product_details_price.text=product.title
        tv_product_details_available_quantity.text = product.stock_quantity
        tv_product_details_description.text=product.description
        tv_product_details_title.text =product.title
      //  btn_add_to_cart.visibility =View.VISIBLE
        if(product.stock_quantity.toInt()==0)
        {
            hideprogressBar()
            tv_product_details_quantity.text = resources.getString(R.string.lbl_out_of_stock)
            btn_add_to_cart.visibility =View.GONE
            tv_product_details_available_quantity.setTextColor(ContextCompat.getColor(this@ProductDetailsActivity,R.color.colorSnackBarError))
        }
        else
        {
            if(FireStoreClass().getCurrentUserID() == product.user_id)
            {
                hideprogressBar()
            }
            else{
                FireStoreClass().checkIfItemExistInCart(this,ProductID)
            }
        }
    }
    private fun addToCart()
    {
        val addToCart = CartItem(
           FireStoreClass().getCurrentUserID(),
                ProductOwnerID,
                ProductDetails.product_id,
                ProductDetails.title,
                ProductDetails.price,
                ProductDetails.image,
                Constants.DEFAULT_CART_QUANTITY,
                ProductDetails.stock_quantity)
        showprogressBar(resources.getString(R.string.please_wait))
        FireStoreClass().addCartItems(this,addToCart)
    }
      fun productExistIncart()
    {
        hideprogressBar()
        btn_add_to_cart.visibility = View.GONE
        btn_go_to_cart.visibility =View.VISIBLE
    }
    override fun onClick(v: View?) {
        if(v!=null)
        {
            when(v.id)
            {
                R.id.btn_add_to_cart->{
                    addToCart()
                }
                R.id.btn_go_to_cart->{
                    startActivity(Intent(this@ProductDetailsActivity,CartlistActivity::class.java))
                    btn_add_to_cart.visibility=View.GONE
                    btn_go_to_cart.visibility=View.GONE
                }
            }

        }
    }
    fun successAddToCartItems()
    {
       hideprogressBar()
        Toast.makeText(
                this@ProductDetailsActivity,
                resources.getString(R.string.success_message_add_to_cart_item),
                Toast.LENGTH_SHORT
        ).show()
        btn_go_to_cart.visibility = View.VISIBLE
        btn_add_to_cart.visibility = View.GONE
    }


}