package com.kx.kotlin.ui

import android.os.Bundle
import android.view.View
import com.kx.kotlin.R
import com.kx.kotlin.base.BaseActivity
import com.kx.kotlin.event.LoginEvent
import com.kx.kotlin.ext.showToast
import com.kx.kotlin.http.RetrofitHelper
import com.kx.kotlin.util.RxUtils
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
    }

    fun login(view: View) {
        if (validate()) {
            val username: String = et_username.text.toString()
            val password: String = et_password.text.toString()
            addDisposable(
                RetrofitHelper.service.login(username, password)
                    .compose(RxUtils.rxSchedulerHelper())
                    .subscribe({
                        var data = it.data
                        EventBus.getDefault().post(LoginEvent(true))
                        finish()
                    }, { it ->
                        run {
                            showToast("$it.message")
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
