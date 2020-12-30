package com.kx.kotlin.base

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.reflect.ParameterizedType

abstract class BaseViewModelActivity<VM : ViewModel> : BaseActivity() {

    private var mViewModel: VM? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    private fun initViewModel() {
        val parameterizedType = javaClass.genericSuperclass as ParameterizedType
        mViewModel = ViewModelProvider(this)[parameterizedType.actualTypeArguments[1] as Class<VM>]
    }

    protected fun getViewModel(): VM {
        if (mViewModel == null) {
            initViewModel()
        }
        return mViewModel!!
    }
}