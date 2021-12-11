package com.example.myshop.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.myshop.R
import com.example.myshop.firestore.FireStoreClass
import com.example.myshop.model.Product
import com.example.myshop.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_products.*
import java.io.IOException

class ProductsActivity : BaseActivity(), View.OnClickListener {
    var SelectedImageFileUri: Uri? =null
    var ProductImageUrl:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products)

        setUpActionBar()
        iv_add_update_product.setOnClickListener(this)
        btn_submit_add_product.setOnClickListener(this)
    }
    fun setUpActionBar()
    {
        setSupportActionBar(toolbar_add_product_activity)
        val actionbar = supportActionBar
        if(actionbar != null)
        {
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24)
        }
        toolbar_add_product_activity.setNavigationOnClickListener { onBackPressed()  }
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.iv_add_update_product -> {
                    if(ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        Constants.showImageChooser(this)
                    } else {
                        ActivityCompat.requestPermissions(
                            this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }
                R.id.btn_submit_add_product->{
                    if(validateProductDetails())
                    {
                         uploadProductImage()
                    }
                }
            }
        }
    }
         override  fun onRequestPermissionsResult(
                requestCode: Int,
                permissions: Array<out String>,
                grantResults: IntArray
            ) {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
                if(requestCode==Constants.READ_STORAGE_PERMISSION_CODE)
                {
                    if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                    {
                        Constants.showImageChooser(this)
                    }
                    else{
                        Toast.makeText(this,resources.getString(R.string.read_storage_permission_denied),Toast.LENGTH_SHORT).show()
                    }
                }
            }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK)
        {
            if(requestCode==Constants.PICK_IMAGE_REQUEST_CODE)
            {
                 if(data !=null) {
                     iv_add_update_product.setImageDrawable(
                         ContextCompat.getDrawable(
                             this,
                             R.drawable.ic_baseline_edit_24
                         )
                     )
                      SelectedImageFileUri = data.data!!
                     try {
                         GlideLoader(this).loadUserPicture(SelectedImageFileUri!!, iv_product_image)
                     }
                     catch(e : IOException)
                     {
                      e.printStackTrace()
                         Toast.makeText(this,"Image Selection Failed",Toast.LENGTH_SHORT).show()
                     }
                 }
            }
        }
        else if(resultCode == Activity.RESULT_CANCELED)
        {
            Toast.makeText(this,"You have not selected any item",Toast.LENGTH_SHORT).show()

        }
    }

    private fun validateProductDetails(): Boolean {
        return when {

            SelectedImageFileUri == null -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_select_product_image), true)
                false
            }

            TextUtils.isEmpty(et_product_title.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_title), true)
                false
            }

            TextUtils.isEmpty(et_product_price.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_price), true)
                false
            }

            TextUtils.isEmpty(et_product_description.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                        resources.getString(R.string.err_msg_enter_product_description),
                        true
                )
                false
            }

            TextUtils.isEmpty(et_product_quantity.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                        resources.getString(R.string.err_msg_enter_product_quantity),
                        true
                )
                false
            }
            else -> {
                true
            }
        }
    }

    private fun uploadProductImage(){
        showprogressBar(resources.getString(R.string.please_wait))
        FireStoreClass().uploadImageTocloudStorage(this,SelectedImageFileUri,Constants.PRODUCT_IMAGE)
    }

    private fun uploadproductdetails()
    {
        val username = this.getSharedPreferences(Constants.MYSHOP_PREFERENCES, Context.MODE_PRIVATE)
                       .getString(Constants.LOGGED_IN_USERNAME,"")!!
        val product_detail = Product(FireStoreClass().getCurrentUserID(),
                username,
                et_product_title.text.toString().trim(){it<=' '},
                et_product_price.text.toString().trim(){it<=' '},
                et_product_description.text.toString().trim(){it<=' '},
                et_product_quantity.text.toString().trim(){it<=' '},
                ProductImageUrl,
                )
        FireStoreClass().uploadProductDetails(this,product_detail)
    }
    fun productUploadSuccess(){
        hideprogressBar()
        Toast.makeText(this,resources.getString(R.string.product_upload_success_message),Toast.LENGTH_SHORT).show()
    finish()
    }

    fun imageUploadSuccess(imageUrl:String)
    {
        //hideprogressBar()
        // Toast.makeText(this,"Your Image is updated Successfully",Toast.LENGTH_SHORT).show()
        ProductImageUrl=imageUrl
        uploadproductdetails()
    }


}