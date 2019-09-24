package com.kx.kotlin.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.kx.kotlin.R
import com.kx.kotlin.base.BaseActivity
import com.kx.kotlin.bean.BaseResponse
import com.kx.kotlin.event.LoginEvent
import com.kx.kotlin.ext.showToast
import com.kx.kotlin.http.ApiException
import com.kx.kotlin.http.ErrorStatus
import com.kx.kotlin.http.ExceptionHandle
import com.kx.kotlin.http.RetrofitHelper
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.toolbar.*
import org.greenrobot.eventbus.EventBus

class LoginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        toolbar.run {
            title = getString(R.string.login)
            setSupportActionBar(this)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            setNavigationOnClickListener {
                finish()
            }
            //supportActionBar?.title = getString(R.string.login)
        }
        register.setOnClickListener {
            Intent(this@LoginActivity, RegisterActivity::class.java).run {
                startActivity(this)
                finish()
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
        }
    }

    fun login(view: View) {
        if (validate()) {
            addDisposable(
                RetrofitHelper.service.login(et_username.text.toString(), et_password.text.toString())
                   .compose(ioMain())
                    .subscribe({
                        username = it.username
                        password = it.password
                        token = it.token
                        isLogin = true
                        EventBus.getDefault().post(LoginEvent(true))
                        finish()
                    }, { it ->
                        showToast("${it.message}")
                    })
            )
        }
    }

   private fun <T> ioMain():ObservableTransformer<BaseResponse<T>,T>{
        return ObservableTransformer { upstream ->
            upstream.subscribeOn(Schedulers.io())
                .onErrorResumeNext(Function { throwable ->
                    Observable.error(ApiException(ExceptionHandle.handleException(throwable)))
                })
                .flatMap { baseResponse ->
                    if (baseResponse .errorCode == ErrorStatus.SUCCESS){
                        Observable.just(baseResponse.data!!)
                    }else{
                        Observable.error(ApiException(baseResponse.errorMsg))
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())

        }
    }


    private fun validate(): Boolean {
        var valid = true
        val username: String = et_username.text.toString()
        val password: String = et_password.text.toString()

        if (username.isEmpty()) {
            et_username.error = getString(R.string.username_not_empty)
            valid = false
        }
        if (password.isEmpty()) {
            et_password.error = getString(R.string.password_not_empty)
            valid = false
        }
        return valid

    }

}
