package com.example.myshop.ui.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {
    val ADD_ADRESS_REQUEST_CODE:Int=121
    val EXTRA_PRODUCT_OWNER: String="extra_product_owner"
    const val SOLD_PRODUCT:String="sold_product"
    //Collection in firestore
    const val USERS:String = "user"
    const val PRODUCTS:String="products"
    const val MYSHOP_PREFERENCES :String= "MyShopPreference"
    const val  LOGGED_IN_USERNAME:String="logged_in_username"
    const val EXTRA_USER_DETAILS :String ="extra_details"
    const val READ_STORAGE_PERMISSION_CODE:Int=2
    const val PICK_IMAGE_REQUEST_CODE = 1
    const val USER_PROFILE_IMAGE = "user_profile_image"
    const val COMPLETE_PROFILE = "profileCompleted"
    const val PRODUCT_IMAGE="Product_image"
    const val USER_ID="user_id"
    const val EXTRA_PRODUCT_DETAILS = "extra_product_details"
    const val EXTRA_PRODUCT_ID="extra_product_detail"
    const val DEFAULT_CART_QUANTITY="1"
    const val CART_ITEM="cart_item"

    const val FIRST_NAME="first_name"
    const val LAST_NAME="last_name"
    const val MALE:String = "male"
    const val FEMALE:String="female"
    const val GENDER:String = "gender"
    const val MOBILNO:String = "mobile"
    const val IMAGE:String = "image"
    const val PRODUCT_ID="product_id"
    const val CART_QUANTITY = "cartQuantity"
    const val HOME = "Home"
    const val OTHER="Other"
    const val OFFICE="Office"
    const val ADD_ADDRESS:String="add_address"
    const val EXTRA_ADDRESS_EDIT_DETAILS = "address_details"
    const val EXTRA_ADDRESS_SELECT="select_address"
    const val EXTRA_ADDRESS_SELECTED = "selected_address"
    const val ORDER ="order"
    const val STOCK_QUANTITY = "stock_quantity"
    const val EXTRA_MY_ORDER_DETAILS="extra_my_order_details"
    const val EXTRA_SOLD_PRODUCT_DETAILS = "extra_sold_product_details"
    fun showImageChooser(activity: Activity){
        val galleryIntent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE) }
    fun getimageExtension(activity: Activity,uri:Uri?):String?{
        return  MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}
