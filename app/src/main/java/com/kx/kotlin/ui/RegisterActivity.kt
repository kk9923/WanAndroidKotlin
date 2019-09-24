package com.kx.kotlin.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.kx.kotlin.R
import com.kx.kotlin.base.BaseActivity
import com.kx.kotlin.event.LoginEvent
import com.kx.kotlin.ext.showToast
import com.kx.kotlin.http.RetrofitHelper
import com.kx.kotlin.util.RxUtils
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.toolbar.*
import org.greenrobot.eventbus.EventBus

class RegisterActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        toolbar.run {
            title = resources.getString(R.string.register)
            setSupportActionBar(this)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            setNavigationOnClickListener {
                finish()
            }
        }
        register.setOnClickListener(onClickListener)
        login.setOnClickListener(onClickListener)
    }

    private val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.register -> {
                register()
            }
            R.id.login -> {
                Intent(this@RegisterActivity, LoginActivity::class.java).apply {
                    startActivity(this)
                }
                finish()
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
        }
    }


    fun register() {
        if (validate()) {
            val username: String = et_username.text.toString()
            val password: String = et_password.text.toString()
            val repassword: String = et_repassword.text.toString()
            addDisposable(
                RetrofitHelper.service.register(username, password, repassword)
                    .compose(RxUtils.ioMain())
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
        val repassword: String = et_repassword.text.toString()
        if (username.isEmpty()) {
            showToast(getString(R.string.username_not_empty))
            valid = false
        }
        if (password.isEmpty()) {
            showToast(getString(R.string.password_not_empty))
            valid = false
        }
        if (repassword.isEmpty()) {
            showToast(getString(R.string.confirm_password_not_empty))
            valid = false
        }
        if (password != repassword) {
            showToast(getString(R.string.password_cannot_match))
            valid = false
        }
        return valid
    }

}
