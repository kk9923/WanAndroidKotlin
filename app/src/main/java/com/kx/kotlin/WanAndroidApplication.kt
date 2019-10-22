package com.kx.kotlin

import android.app.Application
import android.content.Context
import android.view.View
import com.kx.kotlin.R.id.hook
import com.kx.kotlin.theme.ThemeManager
import com.kx.kotlin.util.ToastUtils
import com.tencent.bugly.crashreport.CrashReport
import kotlinx.android.synthetic.main.activity_test.*
import java.lang.Exception
import kotlin.properties.Delegates

/**
 *  Created by admin on 2019/9/18.
 */
class WanAndroidApplication : Application() {

    companion object {
        private val TAG = "App"

        var context: Context by Delegates.notNull()
            private set

        lateinit var instance: Application

    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        context = applicationContext
        ThemeManager.init(context)
        CrashReport.initCrashReport(applicationContext, "d0f1d62e02", true)
        ToastUtils.init(instance)

//        try {
//            val method = View::class.java.getDeclaredMethod("getListenerInfo")
//            method.isAccessible = true
//            // 得到View 的 ListenerInfo对象
//            var listenerInfo = method.invoke(hook)
//            val listenerInfoClz = Class.forName("android.view.View\$ListenerInfo")
//            var mOnClickListenerField = listenerInfoClz.getField("mOnClickListener")
//            var orignOnClickListener = mOnClickListenerField.get(listenerInfo) as View.OnClickListener
//
//            val proxty = TestActivity.ProxyHookClickListener(orignOnClickListener)
//            mOnClickListenerField.set(listenerInfo, proxty)
//        }catch (ex : Exception){
//            println("异常拉 ${ex.toString()}")
//        }
    }
}