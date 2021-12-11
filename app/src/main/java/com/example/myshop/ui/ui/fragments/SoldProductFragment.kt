package com.example.myshop.ui.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myshop.R
import com.example.myshop.firestore.FireStoreClass
import com.example.myshop.ui.adapter.SoldItemAdapter
import com.myshoppal.models.SoldProduct
import kotlinx.android.synthetic.main.fragment_sold_product.*


class SoldProductFragment : BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sold_product, container, false)
    }

    override fun onResume() {
        super.onResume()
        getSoldProduct()
    }

    fun getSoldProduct()
    {showProgressBar(resources.getString(R.string.please_wait))
        FireStoreClass().getSoldProduct(this@SoldProductFragment)
    }
    fun successSoldProduct(product:ArrayList<SoldProduct>)
    {
        hideProgressDialog()
        if(product.size>0)
        {
            tv_no_sold_products_found.visibility = View.GONE
            rv_sold_product_items.visibility = View.VISIBLE
            rv_sold_product_items.layoutManager = LinearLayoutManager(activity)
            rv_sold_product_items.setHasFixedSize(true)

            val SoldListadapter = SoldItemAdapter(requireContext(),product)
            rv_sold_product_items.adapter = SoldListadapter
        }
        else{
            tv_no_sold_products_found.visibility = View.VISIBLE
            rv_sold_product_items.visibility = View.GONE
        }
    }
}