package com.kx.kotlin.util

import android.content.Context
import android.widget.Toast

class ToastUtils {

    companion object{

        private var toast: Toast? = null
        fun showShort(context: Context?, message: CharSequence) {
            if (context == null) {
                return
            }
            if (toast == null) {
                toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
            }
            toast?.setText(message)
            toast?.show()
        }

        fun showLong(context: Context?, message: CharSequence) {
            if (context == null) {
                return
            }
            if (toast == null) {
                toast = Toast.makeText(context, message, Toast.LENGTH_LONG)
            }
            toast?.setText(message)
            toast?.show()
        }
    }
}