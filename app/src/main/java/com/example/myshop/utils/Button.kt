package com.example.myshop.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton

class Button(context: Context,attr:AttributeSet):AppCompatButton(context,attr) {
    init{
        applyEdit()
    }
    private fun applyEdit(){
        val typeface: Typeface = Typeface.createFromAsset(context.assets,"Montserrat-Bold.ttf")
        setTypeface(typeface)
    }
}