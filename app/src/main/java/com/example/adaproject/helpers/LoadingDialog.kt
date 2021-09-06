package com.example.adaproject.helpers

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.example.adaproject.R

class LoadingDialog(val inflater: LayoutInflater, val context: Context) {
    private lateinit var isdialog: AlertDialog
    fun startLoading(){
        /**set View*/
        val infalter = inflater
        val dialogView = infalter.inflate(R.layout.loading_item,null)

        /**set Dialog*/
        val bulider = AlertDialog.Builder(context)
        bulider.setView(dialogView)
        bulider.setCancelable(false)
        isdialog = bulider.create()
        isdialog.show()
    }
    fun isDismiss(){
        isdialog.dismiss()
    }
}