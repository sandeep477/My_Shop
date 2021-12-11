package com.example.myshop.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.myshop.R
import com.example.myshop.firestore.FireStoreClass
import com.example.myshop.utils.GlideLoader
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.auth.User
import kotlinx.android.synthetic.main.activity_settings.*

class SettingActivity : BaseActivity(), View.OnClickListener {
    private lateinit var mUserDetails:com.example.myshop.model.User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setUpActionBAr()
        btn_logout.setOnClickListener(this)
        tv_edit.setOnClickListener(this)
        ll_address.setOnClickListener(this)


    }
    private fun setUpActionBAr()
    {
        setSupportActionBar(toolbar_settings_activity)
        val actionbar = supportActionBar
        if(actionbar != null)
        {
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24)
        }
        toolbar_settings_activity.setNavigationOnClickListener { onBackPressed() }
    }
    private fun getUserDetails(){
        showprogressBar(resources.getString(R.string.please_wait))
        FireStoreClass().getUserDetails(this)
    }
    fun userDetailSuccess(user:com.example.myshop.model.User)
    {  mUserDetails = user
        hideprogressBar()
        GlideLoader(this@SettingActivity).loadUserPicture(user.image,iv_user_photo)
        tv_name.text="${user.firstname} ${user.lastname}"
        tv_gender.text="${user.gender}"
        tv_email.text="${user.email}"
        tv_mobile_number.text="${user.mobile}"
    }

    override fun onResume() {
        super.onResume()
        getUserDetails()
    }

    override fun onClick(v: View?) {
        if(v!=null)
        {
            when(v.id){
                R.id.btn_logout->{
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this@SettingActivity,LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                R.id.tv_edit->{
                    val intent = Intent(this@SettingActivity,UserProfileActivity::class.java)
                    intent.putExtra(Constants.EXTRA_USER_DETAILS,mUserDetails)
                    startActivity(intent)
                }
                R.id.ll_address->{
                    val intent = Intent(this@SettingActivity,AddressListActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }
}