package com.example.myshop.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myshop.R
import com.example.myshop.firestore.FireStoreClass
import com.example.myshop.model.Address
import com.example.myshop.ui.adapter.AddressListAdapter
import com.example.myshop.utils.SwipeToDeleteCallback
import com.example.myshop.utils.SwipeToEditCallback
import kotlinx.android.synthetic.main.activity_address_list.*
import kotlinx.android.synthetic.main.activity_settings.*

class AddressListActivity :BaseActivity() {
    private var mSelectedAddress:Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_list)
        setUpActionBAr()
        getAddressListAll()
        tv_add_address.setOnClickListener {
            val intent = Intent(this@AddressListActivity,AddEditAddressActivity::class.java)
            startActivityForResult(intent,Constants.ADD_ADRESS_REQUEST_CODE)
        }
        if(intent.hasExtra(Constants.EXTRA_ADDRESS_SELECT))
        {
            mSelectedAddress = intent.getBooleanExtra(Constants.EXTRA_ADDRESS_SELECT,false)
        }
        if(mSelectedAddress)
        {
            tv_title_address_list.text =resources.getString(R.string.title_select_address)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK)
        {
            getAddressListAll()
        }
    }
    private fun setUpActionBAr()
    {
        setSupportActionBar(toolbar_address_list_activity)
        val actionbar = supportActionBar
        if(actionbar != null)
        {
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24)
        }
        toolbar_address_list_activity.setNavigationOnClickListener { onBackPressed() }
    }
    private fun getAddressListAll()
    {
        showprogressBar(resources.getString(R.string.please_wait))
        FireStoreClass().getAddressList(this)
    }
    fun successAddressListDownload(addressList:ArrayList<Address>)
    {
        hideprogressBar()
        if(addressList.size>0)
        {
            rv_address_list.visibility = View.VISIBLE
            tv_no_address_found.visibility=View.GONE
            rv_address_list.layoutManager = LinearLayoutManager(this@AddressListActivity)
            rv_address_list.setHasFixedSize(true)
            val adapter = AddressListAdapter(this,addressList,mSelectedAddress)
            rv_address_list.adapter = adapter
           if(!mSelectedAddress){
               val editSwipeHandler = object: SwipeToEditCallback(this) {
                   override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                       val adapter_  = rv_address_list.adapter as  AddressListAdapter
                       adapter_.notifyEditItem(
                           this@AddressListActivity,
                           viewHolder.adapterPosition
                       )
                   }
               }
               val editItemTouchHandler = ItemTouchHelper(editSwipeHandler)
               editItemTouchHandler.attachToRecyclerView(rv_address_list)
               val deleteSwipeHandler = object : SwipeToDeleteCallback(this){
                   override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                       showprogressBar(resources.getString(R.string.please_wait))
                       FireStoreClass().deleteAddress(this@AddressListActivity,addressList[viewHolder.adapterPosition].id)
                   }
               }
               val deleteItemTouchHandler = ItemTouchHelper(deleteSwipeHandler)
               deleteItemTouchHandler.attachToRecyclerView(rv_address_list)

           }
        }
        else{
            rv_address_list.visibility = View.VISIBLE
            tv_no_address_found.visibility=View.GONE
        }

    }

    fun deleteAddressSuccess()
    {  hideprogressBar()
        Toast.makeText(this,"Your Address Deleted",Toast.LENGTH_SHORT).show()
        getAddressListAll()
    }
}