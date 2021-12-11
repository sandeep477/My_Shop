package com.example.myshop.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class RegularTextView(context:Context,attr:AttributeSet):AppCompatTextView(context,attr) {

    init{
        applyFont()
    }
    private fun applyFont()
    {
        val regularface:Typeface = Typeface.createFromAsset(context.assets,"Montserrat-Regular.ttf")
        typeface = regularface
    }
}