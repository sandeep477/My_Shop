package com.example.myshop.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import java.util.jar.Attributes

class BoldTextView(context: Context,attribute: AttributeSet) :AppCompatTextView(context,attribute) {
    init{
          applyfont()
    }
    private fun applyfont()
    {
        val boldTypeFace :Typeface = Typeface.createFromAsset(context.assets,"Montserrat-Bold.ttf")
        setTypeface(boldTypeFace)//or typeface = boldType
    }
}