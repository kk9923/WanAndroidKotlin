package com.kx.kotlin.ext

import android.content.Context
import android.support.v4.app.Fragment
import com.kx.kotlin.util.ToastUtils

fun Context.showToast(content: String) {
    ToastUtils.showShort(this, content)
}

fun Fragment.showToast(content: String){
    ToastUtils.showShort(this.activity?.applicationContext, content)
}