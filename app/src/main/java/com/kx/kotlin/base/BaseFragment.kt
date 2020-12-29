package com.kx.kotlin.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kx.kotlin.constant.Constant
import com.kx.kotlin.util.SPUtils
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract  class BaseFragment : androidx.fragment.app.Fragment() {

    /**
     * check login
     */
    protected var isLogin: Boolean by SPUtils(Constant.LOGIN_KEY, false)

    var mCompositeDisposable: CompositeDisposable? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(attachLayoutRes(), null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    abstract fun attachLayoutRes(): Int

    abstract fun initView()

    fun addDisposable(disposable: Disposable?) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = CompositeDisposable()
        }
        disposable?.let { mCompositeDisposable?.add(it) }
    }


    override fun onDestroy() {
        super.onDestroy()
        clearDispose()
    }


    private fun clearDispose() {
        mCompositeDisposable?.clear()  // 保证Activity结束时取消
        mCompositeDisposable = null
    }

}