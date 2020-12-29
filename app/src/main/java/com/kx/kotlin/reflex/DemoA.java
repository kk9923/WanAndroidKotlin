package com.kx.kotlin.reflex;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.View;
import com.kx.kotlin.R;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by admin on 2019/10/22.
 */
public class DemoA extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);


        Instrumentation instrumentation = (Instrumentation) Reflex.getFieldObject(Activity.class, this, "mInstrumentation");
        MyInstrumentation instrumentation1 = new MyInstrumentation(instrumentation);
        Reflex.setFieldObject(Activity.class, this, "mInstrumentation", instrumentation1);

        hookActivityManager();

        findViewById(R.id.hook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DemoA.this, ReflexActivity.class);
                intent.putExtra("hook", "hookAMS");
                startActivity(intent);
            }
        });

        hookActivityThread();

    }

    private void hookActivityThread() {
        Class activityThreadClass = null;
        try {
            activityThreadClass = Class.forName("android.app.ActivityThread");
            Field sCurrentActivityThreadField = activityThreadClass.getDeclaredField("sCurrentActivityThread");
            sCurrentActivityThreadField.setAccessible(true);
            Object activityThread = sCurrentActivityThreadField.get(null);

            Field declaredField = activityThreadClass.getDeclaredField("mH");
            declaredField.setAccessible(true);
            Handler handler = (Handler) declaredField.get(activityThread);

            Field mCallbackField = Handler.class.getDeclaredField("mCallback");
            mCallbackField.setAccessible(true);
            mCallbackField.set(handler, new HookHandlerCallback(handler));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("hook handleMessage  " + e.toString());
        }
    }

    private void hookActivityManager() {
        try {
            Class classActivityManager = Class.forName("android.app.ActivityManager");
            Field iActivityManagerSingleton = classActivityManager.getDeclaredField("IActivityManagerSingleton");
            iActivityManagerSingleton.setAccessible(true);
            //  得到ActivityManager中静态字段 IActivityManagerSingleton
            Object gDefault = iActivityManagerSingleton.get(null);

            Class classSingleton = Class.forName("android.util.Singleton");
            Field mInstanceSingleton = classSingleton.getDeclaredField("mInstance");
            mInstanceSingleton.setAccessible(true);
            //  得到IActivityManagerSingleton中 mInstance 字段 --- 此时是 IActivityManager 接口
            Object mInstance = mInstanceSingleton.get(gDefault);

            Class<?> classInterface = Class.forName("android.app.IActivityManager");
            Object proxy = Proxy.newProxyInstance(classInterface.getClassLoader(),
                    new Class<?>[]{classInterface}, new AMNInvocationHandler(mInstance));
            mInstanceSingleton.set(gDefault, proxy);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("hook1失败 " + e.toString());
        }
    }

    private void hookHandler2() {
        try {
            Class classActivityManager = Class.forName("android.app.ActivityManager");
            Method getService = classActivityManager.getDeclaredMethod("getService", new Class[]{});
            Object invoke = getService.invoke(null, null);

            Field iActivityManagerSingleton = classActivityManager.getDeclaredField("IActivityManagerSingleton");
            iActivityManagerSingleton.setAccessible(true);
            Object gDefault = iActivityManagerSingleton.get(null);


            Class<?> classInterface = Class.forName("android.app.IActivityManager");
            Object proxy = Proxy.newProxyInstance(classInterface.getClassLoader(),
                    new Class<?>[]{classInterface}, new AMNInvocationHandler(invoke));

            Reflex.setFieldObject("android.util.Singleton", gDefault, "mInstance", proxy);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("hook2失败 " + e.toString());
        }
    }

    private class HookHandlerCallback implements Handler.Callback {

        Handler handler;

        public HookHandlerCallback(Handler handler) {
            this.handler = handler;
        }

        @Override
        public boolean handleMessage(Message msg) {
            System.out.println("hook handleMessage  " + msg.what);
            switch (msg.what) {
                case 100:
                    handleLaunchActivity(msg);
                    break;
                default:
                    break;

            }
            handler.handleMessage(msg);
            return true;
        }

        private void handleLaunchActivity(Message msg) {
            Object obj = msg.obj;
            try {

                Class activityThread$ActivityClientRecordClass = Class.forName("android.app.ActivityThread$ActivityClientRecord");
                Field intentField = activityThread$ActivityClientRecordClass.getDeclaredField("intent");
                intentField.setAccessible(true);
                Intent intent = (Intent) intentField.get(obj);
                Intent targetIntent = intent.getParcelableExtra("AmsHook");
                intent.setComponent(targetIntent.getComponent());
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("hook handleLaunchActivity  " +e.toString());
            }
        }
    }


    public class AMNInvocationHandler implements InvocationHandler {


        private String actionName = "startActivity";

        private Object target;

        public AMNInvocationHandler(Object target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            if (method.getName().equals(actionName)) {
                Intent intent = null;
                int index = 0;
                for (int i = 0; i < args.length; i++) {
                    if (args[i] instanceof Intent) {
                        intent = (Intent) args[i];
                        index = i;
                        break;
                    }
                }
                Log.d("---", "啦啦啦我是hook AMN  1进来的");
                Log.d("---", intent.getStringExtra("hook"));
                String packageName = intent.getComponent().getPackageName();
                Intent newIntent = (Intent) intent.clone();
                ComponentName componentName = new ComponentName(packageName, Main2Activity.class.getName());
                newIntent.setComponent(componentName);
                newIntent.putExtra("AmsHook", intent);
                args[index] = newIntent;
            }
            return method.invoke(target, args);
        }


    }
}
