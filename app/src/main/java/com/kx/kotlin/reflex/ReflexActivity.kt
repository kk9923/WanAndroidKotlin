package com.kx.kotlin.reflex

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kx.kotlin.R
import kotlinx.android.synthetic.main.activity_reflex.*

class ReflexActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reflex)

        intent.run {
            hook.text = getStringExtra("hook")
        }

    }
}
