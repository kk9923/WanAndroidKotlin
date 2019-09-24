package com.kx.kotlin.ext

import android.content.Context
import android.support.v4.app.Fragment
import com.kx.kotlin.util.ToastUtils

fun Context.showToast(content: String) {
    ToastUtils(this, content).show()
}

fun Fragment.showToast(content: String){
    ToastUtils(this.activity?.applicationContext, content).show()
}