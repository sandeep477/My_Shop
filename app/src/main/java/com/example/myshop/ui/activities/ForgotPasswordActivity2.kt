package com.example.myshop.ui.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myshop.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot_password2.*



// TODO Step 1: Create a Forgot Password Activity.
// START
/**
 * Forgot Password Screen of the application.
 */
class ForgotPasswordActivity2 : AppCompatActivity() {

    /**
     * This function is auto created by Android when the Activity Class is created.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        //This call the parent constructor
        super.onCreate(savedInstanceState)
        // This is used to align the xml view to this class
        setContentView(R.layout.activity_forgot_password2)

        // TODO Step 7: Call the setup action bar function.
        // START
        setupActionBar()
        // END
        btn_submit.setOnClickListener {
                        val email:String = et_email.text.toString().trim{it <=' '}
                        if(email.isNotEmpty())
                        {
                            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                                    .addOnCompleteListener { task->
                                        if(task.isSuccessful)
                                        {
                                            Toast.makeText(this,"your email sent succesfully",Toast.LENGTH_SHORT).show()

                                        }
                                        else{
                                            Toast.makeText(this,"your entered email is not working",Toast.LENGTH_SHORT).show()
                                        }
                                    }
                        }
        }

    }


    // TODO Step 6: Create a function to setup the action bar.
    // START
    /**
     * A function for actionBar Setup.
     */
    private fun setupActionBar() {

        setSupportActionBar(toolbar_forgot_password_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_forgot_password_activity.setNavigationOnClickListener { onBackPressed() }
    }
    // END
}
// END