package com.kx.kotlin.util

import android.content.Context
import android.widget.Toast

class ToastUtils {

    private var toast: Toast? = null

    constructor(context: Context?, message: String) : this(context, message, Toast.LENGTH_SHORT)

    constructor(context: Context?, message: String, duration: Int) {
        if (toast == null){
            toast = Toast.makeText(context,message,duration)
        }else{
            toast!!.setText(message)
        }
    }

    fun show() {
        toast?.show()
    }

}