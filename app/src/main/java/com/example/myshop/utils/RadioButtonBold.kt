package com.example.myshop.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatRadioButton

class RadioButtonBold(context:Context,attributeSet: AttributeSet):AppCompatRadioButton(context,attributeSet) {
    init{
        applyfont()
    }
    fun applyfont(){
        val typeface: Typeface = Typeface.createFromAsset(context.assets,"Montserrat-Bold.ttf")
        setTypeface(typeface)
    }
}