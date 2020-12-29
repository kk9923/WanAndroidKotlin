package com.kx.kotlin.base

import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import com.kx.kotlin.constant.Constant
import com.kx.kotlin.util.SPUtils
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class BaseActivity : AppCompatActivity() {

    protected val mActivity by lazy { this }

    protected var isLogin: Boolean by SPUtils(Constant.LOGIN_KEY, false)
    /**
     * local username
     */
    protected var username: String by SPUtils(Constant.USERNAME_KEY, "")

    /**
     * local password
     */
    protected var password: String by SPUtils(Constant.PASSWORD_KEY, "")

    /**
     * token
     */
    protected var token: String by SPUtils(Constant.TOKEN_KEY, "")

    var mCompositeDisposable: CompositeDisposable? = null


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

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}