package com.kx.kotlin.base

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.reflect.ParameterizedType

abstract class BaseViewModelFragment<VM : ViewModel> : BaseFragment() {

    private var mViewModel: VM? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViewModel()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initViewModel() {
        val parameterizedType = javaClass.genericSuperclass as ParameterizedType
        mViewModel = ViewModelProvider(this)[parameterizedType.actualTypeArguments[0] as Class<VM>]
    }

    protected fun getViewModel(): VM {
        if (mViewModel == null) {
            initViewModel()
        }
        return mViewModel!!
    }

}