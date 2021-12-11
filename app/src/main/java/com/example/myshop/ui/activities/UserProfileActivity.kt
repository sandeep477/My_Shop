package com.example.myshop.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myshop.R
import com.example.myshop.firestore.FireStoreClass
import com.example.myshop.model.User
import com.example.myshop.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_user_profile.*
import java.io.IOException

/**
 * A user profile screen.
 */
class UserProfileActivity : BaseActivity() , View.OnClickListener {

    /**
     * This function is auto created by Android when the Activity Class is created.
     */

    private var mUserDetails:User?=null
    private  var mSelectedImageFileUri:Uri?=null
    private var mUserProfileImageUrl:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        //This call the parent constructor
        super.onCreate(savedInstanceState)
        // This is used to align the xml view to this class
        setContentView(R.layout.activity_user_profile)
        setUpActionBar()
        if(intent.hasExtra(Constants.EXTRA_USER_DETAILS))
        {
            mUserDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }


        et_first_name.setText(mUserDetails?.firstname)
        et_last_name.setText(mUserDetails?.lastname)
        et_email.isEnabled = false
        et_email.setText(mUserDetails?.email)

        if(mUserDetails?.profileCompleted==0)
        {  tv_title.text = resources.getString(R.string.title_complete_profile)
            et_last_name.isEnabled = false
            et_first_name.isEnabled = false
        }
        else{
            setUpActionBar()
            tv_title.text = resources.getString(R.string.title_edit_profile)
            mUserDetails?.let { GlideLoader(this).loadUserPicture(it.image,iv_user_photo) }
            if(mUserDetails?.mobile !=0L)
            {
                et_mobile_number.setText(mUserDetails?.mobile.toString())
            }
            if(mUserDetails?.gender == Constants.MALE){
                rb_male.isChecked
            }
            else{
                rb_female.isChecked
            }
        }

        //iv_user_photo.setOnClickListener(this@UserProfileActivity)
        btn_submit.setOnClickListener(this@UserProfileActivity)
        iv_user_photo.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)
            {
                //Toast.makeText(this,"You have a permission",Toast.LENGTH_SHORT).show()
                Constants.showImageChooser(this)
            }
            else{
                ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), Constants.READ_STORAGE_PERMISSION_CODE)
            }
        }
    }

    fun setUpActionBar()
    {
        setSupportActionBar(toolbar_user_profile_activity)
        val actionbar = supportActionBar
        if(actionbar!=null)
        {
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24)
        }
        toolbar_user_profile_activity.setNavigationOnClickListener { onBackPressed() }
    }
    fun updateUserProfile()
    {
        val userHashMap = HashMap<String,Any>()
        val firstname  = et_first_name.text.toString().trim{it <=' '}
        if(firstname != mUserDetails?.firstname)
        {
            userHashMap[Constants.FIRST_NAME]=firstname
        }
        val lastname  = et_last_name.text.toString().trim{it <=' '}
        if(lastname != mUserDetails?.lastname)
        {
            userHashMap[Constants.LAST_NAME]=lastname
        }
        val mobile = et_mobile_number.text.toString().trim { it <=' ' }
        val gender = if(!rb_male.isSelected){
            Constants.MALE
        }
        else{
            Constants.FEMALE
        }
        if(mUserProfileImageUrl.isNotEmpty())
        {
          userHashMap[Constants.IMAGE]=mUserProfileImageUrl
        }
        if(mobile.isNotEmpty() && mobile != mUserDetails?.mobile.toString())
        {
            userHashMap[Constants.MOBILNO]=mobile.toLong()
        }
        userHashMap[Constants.GENDER]=gender
        userHashMap[Constants.COMPLETE_PROFILE]=1
        FireStoreClass().upadateUserDetails(this,userHashMap)

    }
    fun userprofileUpdateSuccess()
    {
        hideprogressBar()
        Toast.makeText(this,resources.getString(R.string.msg_profile_upadte_success),Toast.LENGTH_SHORT).show()

        startActivity(Intent(this@UserProfileActivity, DashboardActivity::class.java))
        finish()
    }
    fun imageUploadSuccess(imageUrl:String)
    {
      // hideprogressBar()
       // Toast.makeText(this,"Your Image is updated Successfully",Toast.LENGTH_SHORT).show()
      mUserProfileImageUrl  = imageUrl
        updateUserProfile()
    }
    override fun onClick(v: View?){
        if(v !=null)
       when(v.id) { R.id.btn_submit->{
                    if(validateUserProfileDetails())
                    {showprogressBar(resources.getString(R.string.please_wait))
                        if(mSelectedImageFileUri !=null)
                        {
                            FireStoreClass().uploadImageTocloudStorage(this,mSelectedImageFileUri,Constants.USER_PROFILE_IMAGE)
                        }
                        else{
                            updateUserProfile()
                        }

                    }
                }
            }
        }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode== Constants.READ_STORAGE_PERMISSION_CODE)
        {
            if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                Constants.showImageChooser(this)
            }
            else{
                Toast.makeText(this,"Read store permission denied",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK)
        {
            if(requestCode == Constants.PICK_IMAGE_REQUEST_CODE)
            {try{
                if(data !=null)
                {
                     mSelectedImageFileUri = data.data!!
                    //iv_user_photo.setImageURI(selectedImageFileUri )
                    GlideLoader(this).loadUserPicture(mSelectedImageFileUri!!,iv_user_photo)
                }}
            catch(e :IOException){
                   e.printStackTrace()
                   Toast.makeText(this,"image selection failed",Toast.LENGTH_SHORT).show()
            }
            }
        }
    }
    private fun validateUserProfileDetails():Boolean{
        return when{
            TextUtils.isEmpty(et_mobile_number.toString().trim { it <=' ' })->{
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_your_mobile_no),true)
                false
            }
            else->{
                true
            }
        }
    }
}