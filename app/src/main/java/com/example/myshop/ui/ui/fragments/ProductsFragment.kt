package com.example.myshop.ui.ui.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myshop.R
import com.example.myshop.firestore.FireStoreClass
import com.example.myshop.model.Product
import com.example.myshop.ui.activities.ProductsActivity
import com.example.myshop.ui.adapter.ProductListAdaper
import kotlinx.android.synthetic.main.fragment_products.*

class ProductsFragment : BaseFragment() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setHasOptionsMenu(true)

  }
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    //homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
    val root = inflater.inflate(R.layout.fragment_products, container, false)
        //homeViewModel.text.observe(viewLifecycleOwner, Observer { textView.text = it })
    return root
  }
  fun delete_Item(productId:String)
  {
    showAlertDialogToDeleteProduct(productId)
    //FireStoreClass().deleteProduct(productId,this)
  }
  fun productDeleteSuccess()
  {
    hideProgressDialog()
    Toast.makeText(activity,resources.getString(R.string.product_delete_success_message),Toast.LENGTH_SHORT).show()
    getProductListFromFireStore()
  }
  fun successProductListFromFireStore(productList:ArrayList<Product>)
  {
    hideProgressDialog()
    if(productList.size>0)
    {
      rv_my_product_items.visibility=View.VISIBLE
      tv_no_products_found.visibility=View.GONE
      rv_my_product_items.layoutManager=LinearLayoutManager(activity)
      rv_my_product_items.setHasFixedSize(true)
      val myadater = ProductListAdaper(requireActivity(),productList,this)
      rv_my_product_items.adapter=myadater
    }
    else{
      rv_my_product_items.visibility=View.GONE
      tv_no_products_found.visibility=View.VISIBLE

    }

  }

  override fun onResume() {
    super.onResume()
    getProductListFromFireStore()
  }
  fun getProductListFromFireStore()
  {
    showProgressBar(resources.getString(R.string.please_wait))
    FireStoreClass().getProductDetails(this@ProductsFragment)
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.add_product_menu,menu)
    super.onCreateOptionsMenu(menu, inflater)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    val id =item.itemId
    when(id){
      R.id.add_product->{
        startActivity(Intent(activity, ProductsActivity::class.java))
      }
    }
    return super.onOptionsItemSelected(item)
  }
  private fun showAlertDialogToDeleteProduct(productID: String) {

    val builder = AlertDialog.Builder(requireActivity())
    //set title for alert dialog
    builder.setTitle(resources.getString(R.string.delete_dialog_title))
    //set message for alert dialog
    builder.setMessage(resources.getString(R.string.delete_dialog_message))
    builder.setIcon(android.R.drawable.ic_dialog_alert)

    //performing positive action
    builder.setPositiveButton(resources.getString(R.string.yes)) { dialogInterface, _ ->

      // TODO Step 7: Call the function to delete the product from cloud firestore.
      // START
      // Show the progress dialog.
      showProgressBar(resources.getString(R.string.please_wait))

      // Call the function of Firestore class.
      FireStoreClass().deleteProduct(productID,this@ProductsFragment)
      // END

      dialogInterface.dismiss()
    }

    //performing negative action
    builder.setNegativeButton(resources.getString(R.string.no)) { dialogInterface, _ ->

      dialogInterface.dismiss()
    }
    // Create the AlertDialog
    val alertDialog: AlertDialog = builder.create()
    // Set other dialog properties
    alertDialog.setCancelable(false)
    alertDialog.show()
  }
}