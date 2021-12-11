package com.example.myshop.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.example.myshop.R
import com.example.myshop.firestore.FireStoreClass
import com.example.myshop.model.Address
import kotlinx.android.synthetic.main.activity_add_edit_address.*
import kotlinx.android.synthetic.main.activity_settings.*

class AddEditAddressActivity : BaseActivity() {
    private  var mAddressDetails:Address?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_address)
        setUpActionBAr()

        if(intent.hasExtra(Constants.EXTRA_ADDRESS_EDIT_DETAILS))
        {
            mAddressDetails = intent.getParcelableExtra(Constants.EXTRA_ADDRESS_EDIT_DETAILS)
        }

        btn_submit_address.setOnClickListener {
            saveAddressToFirestore() }
        rg_type.setOnCheckedChangeListener {_, checkedId ->
            if(checkedId == R.id.rb_other)
            {
                til_other_details.visibility= View.VISIBLE
            }
            else{
                til_other_details.visibility = View.GONE
            }
        }
    }
    private fun setUpActionBAr()
    {
        setSupportActionBar(toolbar_add_edit_address_activity)
        val actionbar = supportActionBar
        if(actionbar != null)
        {
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24)
        }
        toolbar_add_edit_address_activity.setNavigationOnClickListener { onBackPressed() }

    }
    private fun validateData(): Boolean {
        return when {

            TextUtils.isEmpty(et_full_name.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_full_name),
                    true
                )
                false
            }

            TextUtils.isEmpty(et_phone_number.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_phone_number),
                    true
                )
                false
            }

            TextUtils.isEmpty(et_address.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_address), true)
                false
            }

            TextUtils.isEmpty(et_zip_code.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_zip_code), true)
                false
            }

            rb_other.isChecked && TextUtils.isEmpty(
                et_zip_code.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_zip_code), true)
                false
            }
            else -> {
                true
            }
        }
    }
    private  fun saveAddressToFirestore() {

        val fullName: String = et_full_name.text.toString().trim { it <= ' ' }
        val phoneNumber: String = et_phone_number.text.toString().trim { it <= ' ' }
        val zipCode: String = et_zip_code.text.toString().trim { it <= ' ' }
        val additionalNotes: String = et_additional_note.text.toString().trim { it <= ' ' }
        val otherDetails: String = et_other_details.text.toString().trim { it <= ' ' }
        val address: String = et_address.text.toString().trim { it <= ' ' }

        if (validateData()) {

            // Show the progress dialog.
            showprogressBar(resources.getString(R.string.please_wait))

            val addressType: String = when {
                rb_home.isChecked -> {
                    Constants.HOME
                }
                rb_office.isChecked -> {
                    Constants.OFFICE
                }
                else -> {
                    Constants.OTHER
                }
            }


            val addressModel = Address(
                FireStoreClass().getCurrentUserID(),
                fullName,
                phoneNumber,
                address,
                zipCode,
                additionalNotes,
                address,
                addressType,
                otherDetails)
            if (mAddressDetails != null && mAddressDetails!!.id.isNotEmpty()) {
                FireStoreClass().updateAddress(
                    this@AddEditAddressActivity,
                    addressModel,
                    mAddressDetails!!.id
                )
            } else {
                FireStoreClass().addAddressDetails(this@AddEditAddressActivity, addressModel)
            }

        }
    }

    fun addUpdateAddressSuccess() {

        // Hide progress dialog
        hideprogressBar()
       val notifySuccessMessgae:String = if(mAddressDetails !=null && mAddressDetails!!.id.isNotEmpty()){
           resources.getString(R.string.msg_your_address_updated_successfully)
       }else{
           resources.getString(R.string.msg_your_address_add_successfully)
       }
        Toast.makeText(
            this@AddEditAddressActivity,
            notifySuccessMessgae,
            Toast.LENGTH_SHORT
        ).show()
        setResult(RESULT_OK)
        finish()
    }

}