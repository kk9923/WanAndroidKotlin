package com.kx.kotlin.ext

import android.content.Context
import androidx.fragment.app.Fragment
import com.kx.kotlin.util.ToastUtils

fun Context.showToast(content: String) {
    ToastUtils.showShort(this, content).show()
}

fun androidx.fragment.app.Fragment.showToast(content: String){
    ToastUtils.showShort(this.activity?.applicationContext, content).show()
}