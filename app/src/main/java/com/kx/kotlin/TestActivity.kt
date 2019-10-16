package com.kx.kotlin

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.kx.kotlin.WanAndroidApplication.Companion.context
import kotlinx.android.synthetic.main.activity_test.*
import kotlinx.coroutines.*
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        GlobalScope.launch(Dispatchers.Main) {
            println("当前线程1 ：   ${Thread.currentThread().name}")
            delay(5000)
            println("当前线程2 ：   ${Thread.currentThread().name}")
            var bitmapDrawable: Drawable? = null
            withContext(Dispatchers.IO){
                println("当前线程 ：   ${Thread.currentThread().name}")
                val httpURLConnection: HttpsURLConnection =
                    URL("https://pic1.zhimg.com/50/v2-41e15cd04c4f8633d106e927680dc0ae_hd.jpg").openConnection() as HttpsURLConnection
                if (httpURLConnection.responseCode == 200) {
                    bitmapDrawable = BitmapDrawable.createFromStream(httpURLConnection.inputStream, "sdf.png")
                }
            }
            imageView.background = bitmapDrawable
        }
    }
    suspend fun suspendingPrint() {
        println("Thread: ${Thread.currentThread().name}")
    }
}

