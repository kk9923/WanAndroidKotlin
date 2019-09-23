package com.kx.kotlin.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.kx.kotlin.R
import kotlinx.android.synthetic.main.toolbar.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        toolbar.run {
            setSupportActionBar(this)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            setNavigationOnClickListener {
                finish()
            }
            supportActionBar?.title = getString(R.string.login)
        }
    }

    fun login(view : View){
        Toast.makeText(this,"登陆",Toast.LENGTH_SHORT).show()
    }

}
