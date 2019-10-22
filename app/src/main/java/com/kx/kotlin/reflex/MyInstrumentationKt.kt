package com.kx.kotlin.reflex

import android.app.Activity
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.util.Log

/**
 *  Created by admin on 2019/10/22.
 */
class MyInstrumentationKt(val instrumentation: Instrumentation?) : Instrumentation() {

   public  fun execStartActivity(who : Context?, contextThread : IBinder?, token:IBinder?, target: Activity?, intent: Intent?, requestCode: Int? , options: Bundle?): ActivityResult? {

//        Log.d("-----", "啦啦啦我是hookMyInstrumentationKt进来的!")
//        Log.d("-----", "啦啦啦我是hookMyInstrumentationKt进来的!!")
//        var declaredMethod = instrumentation.javaClass.getDeclaredMethod("execStartActivity",
//            Context::class.java,
//            IBinder::class.java,
//            IBinder::class.java,
//            Activity::class.java,
//            Intent::class.java,
//            Int::class.javaPrimitiveType!!,
//            Bundle::class.java)
//        declaredMethod.isAccessible = true
//      //  return Reflex.invokeInstanceMethod(instrumentation, "execStartActivity", classes, objects) as Instrumentation.ActivityResult
//        return  declaredMethod.invoke(instrumentation,who, contextThread, token, target, intent, requestCode, options) as Instrumentation.ActivityResult

       Log.d("-----", "啦啦啦我是hook kt 进来的!")
       val classes = arrayOf<Class<*>>(
           Context::class.java,
           IBinder::class.java,
           IBinder::class.java,
           Activity::class.java,
           Intent::class.java,
           Int::class.javaPrimitiveType!!,
           Bundle::class.java
       )
       val objects = arrayOf(who, contextThread, token, target, intent, requestCode, options)
       Log.d("-----", "啦啦啦我是hook kt 进来的!!")
       return Reflex.invokeInstanceMethod(
           instrumentation,
           "execStartActivity",
           classes,
           objects
       ) as Instrumentation.ActivityResult

    }
}