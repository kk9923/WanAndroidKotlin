package com.kx.kotlin.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.kx.kotlin.R
import com.kx.kotlin.base.BaseActivity
import com.kx.kotlin.base.BaseObserver
import com.kx.kotlin.bean.LoginData
import com.kx.kotlin.event.LoginEvent
import com.kx.kotlin.ext.showToast
import com.kx.kotlin.http.RetrofitHelper
import com.kx.kotlin.util.RxUtil
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
                    .compose(RxUtil.ioMain())
                    .subscribeWith(object : BaseObserver<LoginData>() {
                        override fun onError(errorMsg: String) {
                            showToast(errorMsg)
                        }
                        override fun onSuccess(result: LoginData) {
                            username = result.username
                            password = result.password
                            token = result.token
                            isLogin = true
                            showToast(getString(R.string.login_success))
                            EventBus.getDefault().post(LoginEvent(true))
                            finish()
                        }
                    })
            )
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
