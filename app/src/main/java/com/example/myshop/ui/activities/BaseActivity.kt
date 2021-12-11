package com.example.myshop.ui.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Handler
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.myshop.R
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar.make
import kotlinx.android.synthetic.main.dialog_progress.*
@Suppress("DEPRECATION")
open class BaseActivity : AppCompatActivity() {


    private var mdoubleBackToExitPressOnce=false
    private lateinit var myprogress :Dialog
    override fun onDestroy() {
        super.onDestroy()
        if (this::myprogress.isInitialized && myprogress.isShowing()) {
            myprogress.dismiss()
        }
    }
    fun showErrorSnackBar(message:String, error:Boolean){
        val snackbar = make(findViewById(android.R.id.content), message, BaseTransientBottomBar.LENGTH_LONG)
        val snackbarview = snackbar.view

        if(error)
        {
            snackbarview.setBackgroundColor(ContextCompat.getColor(this@BaseActivity,R.color.colorSnackBarError))
        }
        else
        {
            snackbarview.setBackgroundColor(ContextCompat.getColor(this@BaseActivity,R.color.colorSnackBarSuccess))
        }
        snackbar.show()
    }
    fun showprogressBar(text:String)
    {
        myprogress = Dialog(this)
        /* Set the screen content from a layout resource
        The resource  will be inflated ,adding all top level views to the screen
         */
        myprogress.setContentView(R.layout.dialog_progress)
        myprogress.tv_progress_text.text = text//if we like to change so we can do it
        myprogress.setCancelable(false)
        myprogress.setCanceledOnTouchOutside(false)
        //statrt the dialog and display on the screen
        myprogress.show()

    }
    fun hideprogressBar()
    {
        myprogress.dismiss()
    }

    fun doubleBackPressed()
    {
        if(mdoubleBackToExitPressOnce)
        {
            super.onBackPressed()
            return
        }
        else {
            this.mdoubleBackToExitPressOnce = true
            Toast.makeText(
                this,
                resources.getString(R.string.please_click_back_again_to_exit),
                Toast.LENGTH_SHORT
            ).show()
            @Suppress("DEPRECATION")
            Handler().postDelayed({ mdoubleBackToExitPressOnce = false }, 2000)
        }

    }
}