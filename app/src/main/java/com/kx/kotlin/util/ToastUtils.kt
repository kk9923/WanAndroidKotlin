package com.kx.kotlin.util

import android.content.Context
import android.widget.Toast

class ToastUtils {

    private var toast: Toast

    constructor(context: Context?, message: String) : this(context, message, Toast.LENGTH_SHORT)

    constructor(context: Context?, message: String, duration: Int) {
        toast = Toast(context)
        toast.setText(message)
        toast.duration = duration
    }

    fun show() {
        toast.show()
    }

}