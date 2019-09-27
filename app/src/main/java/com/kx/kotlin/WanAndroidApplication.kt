package com.kx.kotlin

import android.app.Application
import android.content.Context
import com.kx.kotlin.theme.ThemeManager
import com.tencent.bugly.crashreport.CrashReport
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
    }
}