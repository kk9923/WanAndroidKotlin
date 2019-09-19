package com.kx.kotlin

import android.app.Application
import com.kx.kotlin.theme.ThemeManager

/**
 *  Created by admin on 2019/9/18.
 */
class WanAndroidApplication : Application() {

    val mContext  by lazy {
        this.applicationContext
    }
    override fun onCreate() {
        super.onCreate()
        ThemeManager.init(mContext)
    }

}