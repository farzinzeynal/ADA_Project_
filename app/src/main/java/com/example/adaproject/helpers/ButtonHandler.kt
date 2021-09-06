package com.example.adaproject.helpers

import android.graphics.Color
import android.widget.Button

class ButtonHandler(button:Button) {
    private var _button = button
    fun disable(){
        //_button.isEnabled = false
        _button.isClickable = false
        _button.setTextColor(Color.parseColor("#ca8c9f"))
    }
    fun enable()
    {
        //_button.isEnabled = true
        _button.isClickable = true
        _button.setTextColor(Color.parseColor("#AF5D76"))
    }

}