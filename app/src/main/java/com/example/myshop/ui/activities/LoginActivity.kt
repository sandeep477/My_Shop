package com.example.myshop.ui.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager.LayoutParams.*
import com.example.myshop.R
import com.example.myshop.firestore.FireStoreClass
import com.example.myshop.model.User
import com.example.myshop.utils.BoldTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.android.synthetic.main.activity_login.*

@Suppress("DEPRECATION", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class LoginActivity : BaseActivity(),View.OnClickListener {

    private  lateinit var tvRegister: BoldTextView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        @Suppress("DEPRECATION")
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R)
        {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }
        else{
            window.setFlags(
                FLAG_FULLSCREEN,
                FLAG_FULLSCREEN
            )
        }
    }
    override fun onClick(v: View?) {
        if(v !=null)
        {
            when(v.id)
            {
                R.id.tv_forgot_password ->{
                    val intent = Intent(this@LoginActivity, ForgotPasswordActivity2::class.java)
                    startActivity(intent)
                }
                R.id.tv_register ->{
                    val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                    startActivity(intent)
                }
                R.id.btn_login -> {
                    loginRegisteredUser()
                }
            }
        }
    }
    private fun validateLoginDetails():Boolean
    {
       return when
       {   TextUtils.isEmpty(et_email.text.toString().trim { it <= ' ' })-> {
           showErrorSnackBar(resources.getString(R.string.err_msg_enter_email),true)
               false}

           TextUtils.isEmpty(et_password.text.toString().trim { it<=' ' }) ->{
               showErrorSnackBar(resources.getString(R.string.err_msg_enter_password),true )
               false
           }
           else ->{

               true
           }
       }
    }
    private fun loginRegisteredUser()
    {
        if(validateLoginDetails())
        {showprogressBar(resources.getString(R.string.please_wait))
            val email = et_email.text.toString().trim { it <=' ' }
            val password = et_password.text.toString().trim { it <= ' ' }

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener {task->
                        if(task.isSuccessful)
                        {
                          FireStoreClass().getUserDetails(this@LoginActivity)

                        }
                        else{
                            hideprogressBar()
                            showErrorSnackBar(task.exception!!.message.toString(),true)
                        }
                    }
                    .addOnFailureListener {e->
                        if (e is FirebaseAuthInvalidCredentialsException) {
                            showErrorSnackBar("Invalid password",true);
                        } else if (e is FirebaseAuthInvalidUserException) {
                            showErrorSnackBar("Incorrect email address",true);
                        } else {
                            showErrorSnackBar(e.localizedMessage,true)
                        }
                    }
        }
    }

    fun userLoggedInSuccess(user:User)
    {
        hideprogressBar()
        
        if(user.profileCompleted==0)
        {
           val intent = Intent(this@LoginActivity, UserProfileActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS,user)
            startActivity(intent)
            finish()
        }
        else{
            startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
            finish()
        }

    }
}

