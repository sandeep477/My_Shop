package com.example.myshop.ui.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myshop.R
import com.example.myshop.firestore.FireStoreClass
import com.example.myshop.model.Product
import com.example.myshop.ui.activities.CartlistActivity
import com.example.myshop.ui.activities.SettingActivity
import com.example.myshop.ui.adapter.DashboardAdapter
import kotlinx.android.synthetic.main.fragment_dashboard.*

class DashboardFragment :BaseFragment() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
 //if we want to use the options menu in fragment we need to add it.
    setHasOptionsMenu(true)
  }
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
   // dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
    val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
    //dashboardViewModel.text.observe(viewLifecycleOwner, Observer { textView.text = it })
    return root
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.dashboard_menu,menu)
    super.onCreateOptionsMenu(menu, inflater)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    val id=item.itemId
    when(id){
      R.id.action_cart->{
        startActivity(Intent(activity,CartlistActivity::class.java))
      }
      R.id.action_setting->{
        startActivity(Intent(activity,SettingActivity::class.java))
      }
    }
    return super.onOptionsItemSelected(item)
  }
  fun successDashboardItemsList(dashboardItemsList:ArrayList<Product>)
  {
    hideProgressDialog()
    if(dashboardItemsList.size>0) {
      tv_no_products_found.visibility=View.GONE
      rv_my_dashboard_items.visibility=View.VISIBLE

      rv_my_dashboard_items.layoutManager = LinearLayoutManager(activity)
      val adapter = DashboardAdapter(requireActivity(),dashboardItemsList)
      rv_my_dashboard_items.setHasFixedSize(true)
      rv_my_dashboard_items.adapter=adapter
    }
    else{
      rv_my_dashboard_items.visibility=View.GONE
      tv_no_products_found.visibility=View.VISIBLE
    }
  }
private fun getDashboardItems()
{
  showProgressBar(resources.getString(R.string.please_wait))
  FireStoreClass().getDashboardItemsList(this)

}
  override fun onResume() {
    super.onResume()
    getDashboardItems()
  }
}