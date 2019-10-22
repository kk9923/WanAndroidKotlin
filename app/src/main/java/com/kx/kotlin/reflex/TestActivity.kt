package com.kx.kotlin.reflex

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.kx.kotlin.R
import kotlinx.android.synthetic.main.activity_test.*
import kotlinx.coroutines.*
import java.lang.Exception
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import android.app.Activity
import android.app.Instrumentation
import com.kx.kotlin.ui.MainActivity


class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        GlobalScope.launch(Dispatchers.Main) {
            println("当前线程1 ：   ${Thread.currentThread().name}")
            //   delay(5000)
            println("当前线程2 ：   ${Thread.currentThread().name}")
            var bitmapDrawable: Drawable? = null
            withContext(Dispatchers.IO) {
                println("当前线程 ：   ${Thread.currentThread().name}")
                val httpURLConnection: HttpsURLConnection =
                    URL("https://pic1.zhimg.com/50/v2-41e15cd04c4f8633d106e927680dc0ae_hd.jpg").openConnection() as HttpsURLConnection
                if (httpURLConnection.responseCode == 200) {
                    bitmapDrawable = BitmapDrawable.createFromStream(httpURLConnection.inputStream, "sdf.png")
                }
            }
            imageView.background = bitmapDrawable
            hook.run {
                setOnClickListener {
                    var declaredField = Activity::class.java.getDeclaredField("mInstrumentation")
                    declaredField.isAccessible = true
                    val instrumentation = declaredField.get(this@TestActivity)
                    if (instrumentation is MyInstrumentationKt){
                        println("setOnClickListener  MyInstrumentationKt")
                    }else {
                        println("setOnClickListener  instrumentation")
                    }
                    println("setOnClickListener  hook")
                    Intent(this@TestActivity, DemoA::class.java).run {
                        startActivity(this)
                    }

                }
            }
            hookStartActivity()


         //   hookOnClickListener()
        }
    }

    private fun hookStartActivity() {
        try {
            var declaredField = Activity::class.java.getDeclaredField("mInstrumentation")
            declaredField.isAccessible = true
            val instrumentation = declaredField.get(this) as Instrumentation

            val instrumentationKt = MyInstrumentationKt(instrumentation )
            declaredField.set(this,instrumentationKt)


        }catch (e :Exception){
            println("hookStartActivity  $e")
        }
    }

    private fun hookOnClickListener() {
        try {
            val mListenerInfoField = View::class.java.getDeclaredField("mListenerInfo")
            mListenerInfoField.isAccessible = true
            // 获得  hook  button 的 ListenerInfo 字段对象
            val mListenerInfo = mListenerInfoField.get(hook)
            val listenerInfoClass = Class.forName("android.view.View\$ListenerInfo")
            val mOnClickListenerField = listenerInfoClass.getField("mOnClickListener")

            val preOnClickListener = mOnClickListenerField.get(mListenerInfo) as View.OnClickListener?

            val newProxyInstance = Proxy.newProxyInstance(
                listenerInfoClass.classLoader,
                arrayOf<Class<*>>(View.OnClickListener::class.java),
                object : InvocationHandler {
                    override fun invoke(proxy: Any?, method: Method, args: Array<out Any>?): Any? {
                        println("hook")
                        val result = method.invoke(preOnClickListener, hook)
                        return result
                    }
                })
            //  mOnClickListenerField.set(mListenerInfo,ProxyHookClickListener(preOnClickListener ))
            mOnClickListenerField.set(mListenerInfo, newProxyInstance)
        } catch (e: Exception) {
            println(e.toString())
        }
    }

    suspend fun suspendingPrint() {
        println("Thread: ${Thread.currentThread().name}")
    }

    class ProxyHookClickListener(var onHookClickListener: View.OnClickListener?) : View.OnClickListener {

        override fun onClick(v: View?) {
            println(
                "hook1232312312323" +
                        ""
            )
            onHookClickListener?.onClick(v)
        }
    }
}

