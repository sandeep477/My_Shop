package com.example.myshop.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.util.Log.*
import androidx.fragment.app.Fragment
import com.example.myshop.Order
import com.example.myshop.model.Address
import com.example.myshop.model.CartItem
import com.example.myshop.model.Product
import com.example.myshop.model.User
import com.example.myshop.ui.activities.*
import com.example.myshop.ui.ui.fragments.DashboardFragment
import com.example.myshop.ui.ui.fragments.OrdersFragment
import com.example.myshop.ui.ui.fragments.ProductsFragment
import com.example.myshop.ui.ui.fragments.SoldProductFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.myshoppal.models.SoldProduct

@Suppress("DEPRECATION")
class FireStoreClass {

    // Access a Cloud Firestore instance.
    private val mFireStore = FirebaseFirestore.getInstance()

    /**
     * A function to make an entry of the registered user in the FireStore database.
     */
    fun registerUser(activity: RegisterActivity, userInfo: User) {

        // TODO Step 3: Replace the hard coded string with constant value which is added in the Constants object.
        // The "users" is collection name. If the collection is already created then it will not create the same one again.
        mFireStore.collection(Constants.USERS)
                // Document ID for users fields. Here the document it is the User ID.
                .document(userInfo.id)
                // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge later on instead of replacing the fields.
                .set(userInfo, SetOptions.merge())
                .addOnSuccessListener {

                    // Here call a function of base activity for transferring the result to it.
                    activity.userRegistrationSuccess()
                }
                .addOnFailureListener { e ->
                    activity.hideprogressBar()
                    e(
                            activity.javaClass.simpleName,
                            "Error while registering the user.",
                            e
                    )
                }
    }

    // TODO Step 1: Create a function to get the user id of the current logged in user.
    // START
    /**
     * A function to get the user id of current logged user.
     */
    fun getCurrentUserID(): String {
        // An Instance of currentUser using FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser

        // A variable to assign the currentUserId if it is not null or else it will be blank.
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }
    // END

    // TODO Step 4: Create a function to get the logged user details from Cloud Firestore.
    // START
    /**
     * A function to get the logged user details from from FireStore Database.
     */
    fun getUserDetails(activity: Activity) {

        // Here we pass the collection name from which we wants the data.
        mFireStore.collection(Constants.USERS)
                // The document id to get the Fields of user.
                .document(getCurrentUserID())
                .get()
                .addOnSuccessListener { document ->

                    i(activity.javaClass.simpleName, document.toString())

                    // Here we have received the document snapshot which is converted into the User Data model object.
                    val user = document.toObject(User::class.java)!!

                    val sharedPreference = activity.getSharedPreferences(
                            Constants.MYSHOP_PREFERENCES,
                            Context.MODE_PRIVATE
                    )
                    val editor:SharedPreferences.Editor = sharedPreference.edit()
                    //key LOGGED_IN_USERNAME
                    //value sandy upa
                    editor.putString(
                            Constants.LOGGED_IN_USERNAME,
                            "${user.firstname} ${user.lastname}"
                    )
                    editor.apply()
                    // START
                    when(activity) {
                         is LoginActivity-> {
                            // Call a function of base activity for transferring the result to it.
                            activity.userLoggedInSuccess(user)
                        }
                        is SettingActivity->{
                            activity.userDetailSuccess(user)
                        }
                    }
                    // END
                }
                .addOnFailureListener { e ->
                    // Hide the progress dialog if there is any error. And print the error in log.
                    when(activity) {
                        is LoginActivity -> {activity.hideprogressBar()}
                        is SettingActivity->{activity.hideprogressBar()}
                    }

                    e(
                            activity.javaClass.simpleName,
                            "Error while getting user details.",
                            e
                    )
                }
    }
    fun upadateUserDetails(activity: Activity,userHashMap: HashMap<String,Any>)
    {
     mFireStore.collection(Constants.USERS)
         .document(getCurrentUserID())
         .update(userHashMap)
         .addOnSuccessListener {
             when(activity){
                 is UserProfileActivity ->{
                     activity.userprofileUpdateSuccess()
                 }
             }
         }
         .addOnFailureListener {
             when(activity){
                 is UserProfileActivity ->{
                     activity.hideprogressBar()
                 }
             }
         }
    }
    fun uploadImageTocloudStorage(activity: Activity,imageFileUri: Uri?,imageType:String){
        val sRef : StorageReference = FirebaseStorage.getInstance().reference
                .child(imageType+System.currentTimeMillis()+"."+
                Constants.getimageExtension(activity,imageFileUri))

        sRef.putFile(imageFileUri!!).addOnSuccessListener { taskSnapShot->
            e("FireBaseImageUrl", taskSnapShot.metadata!!.reference!!.downloadUrl.toString())
            //get the downloadable url from the task snapshot
            taskSnapShot.metadata!!.reference!!.downloadUrl
                .addOnSuccessListener { uri->
                    e("Downloadable Image URL",uri.toString())
                    when(activity){
                        is UserProfileActivity ->{
                            activity.imageUploadSuccess(uri.toString())
                        }
                        is ProductsActivity->{
                            activity.imageUploadSuccess(uri.toString())
                        }
                    }
                } }
            .addOnFailureListener{exception->
                when(activity){
                    is UserProfileActivity ->{
                        activity.hideprogressBar()
                    }
                    is ProductsActivity->{
                        activity.hideprogressBar()
                    }
                }
                e(activity.javaClass.simpleName, exception.message, exception)
            }
    }
    fun uploadProductDetails(activity:ProductsActivity,productinfo: Product)
    {
      mFireStore.collection(Constants.PRODUCTS)
              .document()
              .set(productinfo,SetOptions.merge())
              .addOnSuccessListener {
                  activity.productUploadSuccess()
              }
              .addOnFailureListener {
                  activity.hideprogressBar()
                  e(activity.javaClass.simpleName,
                          "Error while uploading product details")
              }
    }
    fun getProductDetails(fragment: Fragment)
    {
        mFireStore.collection(Constants.PRODUCTS)
            .whereEqualTo(Constants.USER_ID,getCurrentUserID())
            .get()
            .addOnSuccessListener { document->
                e("Product_List",document.documents.toString())
                val productList:ArrayList<Product> =ArrayList()
                for(i in document.documents)
                {
                    val product =i.toObject(Product::class.java)
                    product!!.product_id = i.id
                            productList.add(product)
                }
                when(fragment){
                    is ProductsFragment->{
                        fragment.successProductListFromFireStore(productList)
                    }
                }
            }
    }
    fun getDashboardItemsList(fragment: DashboardFragment){
        mFireStore.collection(Constants.PRODUCTS)
                .get()
                .addOnSuccessListener { document ->
                    e(fragment.javaClass.simpleName, document.documents.toString())
                    val productList: ArrayList<Product> = ArrayList()
                    for (i in document.documents) {
                        val product = i.toObject(Product::class.java)
                        product!!.product_id = i.id
                        productList.add(product)
                    }
                    when {
                        else -> {
                            fragment.successDashboardItemsList(productList)
                        }
                    }
                }
                .addOnFailureListener {e->
                    fragment.hideProgressDialog()
                    e(fragment.javaClass.simpleName,"Errot ehile getting dashboard items",e)
                }
    }
    fun deleteProduct(productId:String,fragment: ProductsFragment)
   {
       mFireStore.collection(Constants.PRODUCTS)
               .document(productId)
               .delete()
               .addOnSuccessListener {
                fragment.productDeleteSuccess()
               }
               .addOnFailureListener {
                   e->
                   fragment.hideProgressDialog()
                   e(fragment.requireActivity().javaClass.simpleName,"Error while deleting the product",e)
               }
   }
    fun getProductsDetail_(activity:ProductDetailsActivity,product_id:String)
    {
        mFireStore.collection(Constants.PRODUCTS)
            .document(product_id)
            .get()
            .addOnSuccessListener {document->
                e(activity.javaClass.simpleName,document.toString())
                val product: Product? =document.toObject(Product::class.java)
                if(product!=null) {
                    product.product_id=product_id
                    activity.successProductDetails(product)
                }
            }
            .addOnFailureListener {
                    e ->
                // Hide the progress dialog if there is an error.
                activity.hideprogressBar()

                e(activity.javaClass.simpleName, "Error while getting the product details.", e)

            }
    }
    fun addCartItems(activity: ProductDetailsActivity,addToCart:CartItem)
    {
          mFireStore.collection(Constants.CART_ITEM)
                  .document()
                  .set(addToCart, SetOptions.merge())
                  .addOnSuccessListener {
                      activity.successAddToCartItems()
                  }
                  .addOnFailureListener {
                      e ->
                      // Hide the progress dialog if there is an error.
                      activity.hideprogressBar()

                      Log.e(activity.javaClass.simpleName, "Error while getting the product details.", e)

                  }
    }
    fun addAddressDetails(activity:AddEditAddressActivity,add_address:Address)
    {
        mFireStore.collection(Constants.ADD_ADDRESS)
            .document()
            .set(add_address, SetOptions.merge())
            .addOnSuccessListener {
                activity.addUpdateAddressSuccess()
            }
            .addOnFailureListener {

                activity.hideprogressBar()
                Log.e(activity.javaClass.simpleName,"Error while uploading address")
            }
    }
    fun updateAddress(activity: AddEditAddressActivity, addressInfo: Address, addressId: String) {

        mFireStore.collection(Constants.ADD_ADDRESS)
            .document(addressId)
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {

                // Here call a function of base activity for transferring the result to it.
                activity.addUpdateAddressSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideprogressBar()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating the Address.",
                    e
                )
            }
    }

    fun getAddressList(activity:AddressListActivity)
    {
        mFireStore.collection(Constants.ADD_ADDRESS)
            .whereEqualTo(Constants.USER_ID,getCurrentUserID())
            .get()
            .addOnSuccessListener { document->
            val AddressList:ArrayList<Address> = ArrayList()
            for(i in document.documents)
            {
                var address_ = i.toObject(Address::class.java)
                if (address_ != null) {
                    address_.id = i.id
                }
                if (address_ != null) {
                    AddressList.add(address_)
                }

            }
                activity.successAddressListDownload(AddressList)
            }
            .addOnFailureListener {
                activity.hideprogressBar()
                e(activity.javaClass.simpleName, "Error while getting the address details.")
            }

    }
    fun deleteAddress(activity: AddressListActivity,addressId:String)
    {
        mFireStore.collection(Constants.ADD_ADDRESS)
            .document(addressId)
            .delete()
            .addOnSuccessListener {
                when {
                    else -> {
                        activity.deleteAddressSuccess()
                    }
                }

            }
            .addOnFailureListener {
                    e->
                when {
                    else -> {
                        activity.hideprogressBar()
                    }
                }
                e(activity.javaClass.simpleName,"Error while deleting the Address",e)

            }
    }
    fun checkIfItemExistInCart(activity:ProductDetailsActivity,productId_:String)
    {
        mFireStore.collection(Constants.CART_ITEM)
                .whereEqualTo(Constants.PRODUCT_ID,productId_)
                .whereEqualTo(Constants.USER_ID,getCurrentUserID())
                .get()
                .addOnSuccessListener { document->
                    e(activity.javaClass.simpleName,document.documents.toString())

                    if(document.documents.size >0)
                    {
                        activity.productExistIncart()
                    }
                    else{
                        activity.hideprogressBar()
                    }
                }
                .addOnFailureListener {
                    e ->
                    // Hide the progress dialog if there is an error.
                    activity.hideprogressBar()
                    e(activity.javaClass.simpleName, "Error while getting the product details.", e)

                }
    }
    fun getCartItems(activity:Activity)
    {
        mFireStore.collection(Constants.CART_ITEM)
            .whereEqualTo(Constants.USER_ID,getCurrentUserID())
            .get()
            .addOnSuccessListener {
                document->
                val list:ArrayList<CartItem> =ArrayList()
                for(i in document.documents)
                {
                    val cartItem =  i.toObject(CartItem::class.java)
                    cartItem?.id  =i.id
                    if (cartItem != null) {
                        list.add(cartItem)
                    }

                }
                when(activity)
                {
                    is CartlistActivity->{
                        activity.successCartItemList(list)
                    }
                    is CheckoutActivity->{
                        activity.successCartItemList(list)
                    }
                }
            }
            .addOnFailureListener {

                when(activity){
                    is CartlistActivity->{activity.hideprogressBar()
                        Log.e(activity.javaClass.simpleName,"Error while getting the cart list item")
                    }
                    is CheckoutActivity->{activity.hideprogressBar()
                        Log.e(activity.javaClass.simpleName,"Error while getting the cart list item")
                    }
                }
            }
    }
    fun getAllProductList(activity:Activity){
        mFireStore.collection(Constants.PRODUCTS)
            .get()
            .addOnSuccessListener {document->
                val productList :ArrayList<Product> = ArrayList()
                for(item in document.documents)
                {
                 val product = item.toObject(Product::class.java)
                    product!!.product_id = item.id
                    productList.add(product)
                }
                when(activity)
                {
                    is CartlistActivity->{activity.successProductListFromFireStore(productList)}
                    is CheckoutActivity->{activity.successProductListFromFireStore(productList)}
                }

            }
            .addOnFailureListener {e->
                when(activity)
                {
                    is CartlistActivity->{ activity.hideprogressBar()
                    }
                    is CheckoutActivity->{ activity.hideprogressBar()
                    }
                }
                e("Getting Product list","Error while getting all Product list",e)


            }
    }
    fun removeItemFromCart(context:Context,cart_id:String)
    {
        mFireStore.collection(Constants.CART_ITEM)
            .document(cart_id)
            .delete()
            .addOnSuccessListener {
                when(context){
                    is CartlistActivity->{
                        context.itemRemovedSuccess()
                    }
                }
            }
            .addOnFailureListener {e->
                when(context){
                    is CartlistActivity->{
                        context.hideprogressBar()
                    }
                }
                e(context.javaClass.simpleName,"Error while deleting the cart item",e)

            }

    }
    fun updateMyCart(context:Context,cart_id:String,itemHashMap:HashMap<String,Any>)
    {
        mFireStore.collection(Constants.CART_ITEM)
            .document(cart_id)
            .update(itemHashMap)
            .addOnSuccessListener {
              when(context){
                  is CartlistActivity->{
                      context.itemUpdateSuccess()
                  }
              }
            }
            .addOnFailureListener {
                    e->
                when(context){
                    is CartlistActivity->{
                        context.hideprogressBar()
                        e(context.javaClass.simpleName,"Error while deleting the product",e)

                    }

                }

            }
    }
    fun placeOrder(activity:CheckoutActivity,order: Order){
        mFireStore.collection(Constants.ORDER)
                .document()
                .set(order, SetOptions.merge())
                .addOnSuccessListener {
                    activity.successPlaceOrder()
                }
                .addOnFailureListener {
                    activity.hideprogressBar()
                    Log.e(activity.javaClass.simpleName,"Error while placing order")
                }
    }
    fun updateAllDetails(activity:CheckoutActivity,cartList:ArrayList<CartItem>,order:Order)
    {
        val writeBatch = mFireStore.batch()
        for(cartItem in cartList)
        {
           // val productHashMap =HashMap<String,Any>()
            //productHashMap[Constants.STOCK_QUANTITY] = (cartItem.stock_quantity.toInt() - cartItem.cartQuantity.toInt()).toString()

                val  soldproduct = SoldProduct(
                        cartItem.product_owner_id,
                        cartItem.title,
                        cartItem.price,
                        cartItem.cartQuantity,
                        cartItem.image,
                        order.id,
                        order.order_datetime,
                        order.sub_total_amount,
                        order.total_amount,
                       cartItem.id,
                        order.shipping_charge,
                        order.address
                )
            val documentReference = mFireStore.collection(Constants.SOLD_PRODUCT)
                                               .document(cartItem.product_owner_id)
            writeBatch.set(documentReference,soldproduct)
        }
        for(cartItem in cartList)
        {   val hashmap=HashMap<String,Any>()
            val stock = cartItem.stock_quantity.toLong()
            val quant = cartItem.cartQuantity.toLong()
            hashmap[Constants.STOCK_QUANTITY] = (stock -quant ).toString()
            mFireStore.collection(Constants.PRODUCTS)
                    .document(cartItem.product_id)
                    .update(hashmap)
           val documentReference = mFireStore.collection(Constants.CART_ITEM)
                                            .document(cartItem.id)
            writeBatch.delete(documentReference)



        }
        writeBatch.commit().addOnSuccessListener {
            activity.allDetailsUpdatedSuccessfully()
        }
                .addOnFailureListener {
                    activity.hideprogressBar()
                    Log.e(activity.javaClass.simpleName,"Error while updating all the details after order palced")
                }
    }
    fun getMyOrderList(fragment:OrdersFragment)
    {
        mFireStore.collection(Constants.ORDER)
                .whereEqualTo(Constants.USER_ID,getCurrentUserID())
                .get()
                .addOnSuccessListener {document->
                    var orderList:ArrayList<Order> = ArrayList()
                    for(i in document.documents)
                    {
                     val order:Order? = i.toObject(Order::class.java)
                        order?.id = i.id
                        if (order != null) {
                            orderList.add(order)
                        }
                    }
                    fragment.populateOrderListInUI(orderList)
                }
                .addOnFailureListener {
                    fragment.hideProgressDialog()
                    Log.e(fragment.javaClass.simpleName,"Error while getting orders List ")
                }
    }
    fun getSoldProduct(fragment:SoldProductFragment)
    {
        mFireStore.collection(Constants.SOLD_PRODUCT)
            .whereEqualTo(Constants.USER_ID,getCurrentUserID())
            .get()
            .addOnSuccessListener {  document->
                var soldproduct:ArrayList<SoldProduct> = ArrayList()
                for(i in document.documents)
                {
                    val product = i.toObject(SoldProduct::class.java)
                    product?.id = i.id
                    product?.let { soldproduct.add(it) }
                }
                fragment.successSoldProduct(soldproduct)

            }
            .addOnFailureListener {
                fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName,"Error while getting sold products")
            }
    }


}