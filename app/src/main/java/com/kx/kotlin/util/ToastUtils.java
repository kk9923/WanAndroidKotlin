package com.kx.kotlin.util;



import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import androidx.core.app.NotificationManagerCompat;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.Toast;
import com.kx.kotlin.R;

import java.util.ArrayList;

public class ToastUtils {
    private static final String TAG = "ToastUtils";
    public static final int LENGTH_SHORT = 0;
    public static final int LENGTH_LONG = 1;
    static final long TOAST_CHECK_TIME = 100L;
    static final int MSG_ADD_TOAST = 1000;
    static final int MSG_PROCESS_TOAST = 1001;
    static final ActivityLifecycleCallbacks LIFECYCLE_CALLBACK = new ActivityLifecycleCallbacks() {
        public void onActivityCreated(Activity var1, Bundle var2) {
        }

        public void onActivityStarted(Activity var1) {
        }

        public void onActivityResumed(Activity var1) {
            ToastUtils.sLastResumedActivity = var1;
        }

        public void onActivityPaused(Activity var1) {
            if (ToastUtils.sLastResumedActivity == var1) {
                ToastUtils.sLastResumedActivity = null;
            }

        }

        public void onActivityStopped(Activity var1) {
        }

        public void onActivitySaveInstanceState(Activity var1, Bundle var2) {
        }

        public void onActivityDestroyed(Activity var1) {
            if (ToastUtils.sLastResumedActivity == var1) {
                ToastUtils.sLastResumedActivity = null;
            }

        }
    };
    static final Handler TOAST_HANDLER = new Handler(Looper.getMainLooper(), new Callback() {
        public boolean handleMessage(Message var1) {
            ToastUtils.ToastRequest var2;
            switch(var1.what) {
                case 1000:
                    var2 = (ToastUtils.ToastRequest)var1.obj;
                 //   TAFLogger.log("ToastUtils", "Add Toast: %s", new Object[]{var2.text});
                    ToastUtils.sRequestQueue.add(var2);
                 //   TAFLogger.log("ToastUtils", "Add Toast Count: %d", new Object[]{ToastUtils.sRequestQueue.size()});
                    if (ToastUtils.sRequestQueue.size() == 1) {
                        ToastUtils.TOAST_HANDLER.sendEmptyMessage(1001);
                    }
                    break;
                case 1001:
                    var2 = (ToastUtils.ToastRequest)ToastUtils.sRequestQueue.get(0);
                    boolean var3 = true;
                 //   TAFLogger.log("ToastUtils", "Process Toast: %s %d", new Object[]{var2.text, var2.retryCount});
                    if (ToastUtils.sLastResumedActivity != null) {
                        if (ToastUtils.sShowingToast.get(ToastUtils.sLastResumedActivity.hashCode()) == null) {
                            View var4 = ToastUtils.showToast(ToastUtils.sLastResumedActivity, var2.text, var2.duration);
                            if (var4 != null) {
                                ToastUtils.sShowingToast.put(ToastUtils.sLastResumedActivity.hashCode(), var4);
                                ToastUtils.sRequestQueue.remove(0);
                            } else {
                                var3 = false;
                            }
                        }
                    } else {
                        var3 = false;
                    }

                 //   TAFLogger.log("ToastUtils", "Process Toast: %s", new Object[]{var3 ? "true" : "false"});
                    if (!var3) {
                        ++var2.retryCount;
                    }

                    if (var2.retryCount > 3) {
                        ToastUtils.sRequestQueue.clear();
                    }

                //    TAFLogger.log("ToastUtils", "Process Toast Count: %d", new Object[]{ToastUtils.sRequestQueue.size()});
                    if (!ToastUtils.sRequestQueue.isEmpty()) {
                        ToastUtils.TOAST_HANDLER.sendEmptyMessageDelayed(1001, 100L);
                    }
            }

            return true;
        }
    });
    static Activity sLastResumedActivity = null;
    static ArrayList<ToastUtils.ToastRequest> sRequestQueue = new ArrayList(4);
    static SparseArray<View> sShowingToast = new SparseArray(2);
    final Context context;
    final CharSequence text;
    final int duration;

    private ToastUtils(Context var1, CharSequence var2, int var3) {
        this.context = var1;
        this.text = var2;
        this.duration = var3;
    }

    public static void init(Application var0) {
        var0.registerActivityLifecycleCallbacks(LIFECYCLE_CALLBACK);
    }

    public static ToastUtils makeText(Context var0, int var1, int var2) {
        return makeText(var0, var0.getString(var1), var2);
    }

    public static ToastUtils makeText(Context var0, CharSequence var1, int var2) {
        return new ToastUtils(var0, var1, var2);
    }

    public static ToastUtils showShort(Context context, CharSequence toast) {
        return new ToastUtils(context, toast, LENGTH_SHORT);
    }

    public void show() {
        if (NotificationManagerCompat.from(this.context).areNotificationsEnabled()) {
            Toast.makeText(this.context, this.text, this.duration).show();
        } else {
            ToastUtils.ToastRequest var1 = new ToastUtils.ToastRequest();
            var1.text = this.text;
            var1.duration = (long)(this.duration == 0 ? 2000 : 3500);
            TOAST_HANDLER.obtainMessage(1000, var1).sendToTarget();
        }

    }

    static View showToast(Activity var0, CharSequence var1, long var2) {
        Window var4 = var0 != null && !var0.isFinishing() ? var0.getWindow() : null;
        if (var4 != null && var4.peekDecorView() != null) {
            View var5 = var4.peekDecorView();
            if (var5 instanceof FrameLayout) {
                final FrameLayout var6 = (FrameLayout)var5;
                Toast var7 = Toast.makeText(var4.getContext(), var1, Toast.LENGTH_SHORT);
                final View var8 = var7.getView();
                if (var8 != null) {
                    final Runnable var9 = new Runnable() {
                        public void run() {
                         //   TAFLogger.log("ToastUtils", "Remove Toast: %d", new Object[]{var8.hashCode()});
                            var8.startAnimation(AnimationUtils.loadAnimation(var8.getContext(), R.anim.up_base_ui_toast_exit));
                            var6.removeView(var8);
                        }
                    };
                    var8.setClickable(false);
                    var8.setFocusable(false);
                    var8.addOnAttachStateChangeListener(new OnAttachStateChangeListener() {
                        public void onViewAttachedToWindow(View var1) {
                        }

                        public void onViewDetachedFromWindow(View var1) {
                        //    TAFLogger.log("ToastUtils", "Remove Activity Toast: %d", new Object[]{var1.hashCode()});
                            var1.removeCallbacks(var9);
                            int var2 = ToastUtils.sShowingToast.indexOfValue(var1);
                            if (var2 >= 0) {
                                ToastUtils.sShowingToast.removeAt(var2);
                            }

                        }
                    });
                    LayoutParams var10 = new LayoutParams(-2, -2);
                    var10.gravity = 81;
                    var10.bottomMargin = var8.getResources().getDimensionPixelSize(R.dimen.dp_100);
                    var6.addView(var8, var10);
                    var8.startAnimation(AnimationUtils.loadAnimation(var8.getContext(), R.anim.up_base_ui_toast_enter));
                 //   TAFLogger.log("ToastUtils", "Show Toast: %d", new Object[]{var8.hashCode()});
                    var8.postDelayed(var9, var2);
                    return var8;
                }
            }
        }

        return null;
    }

    static class ToastRequest {
        CharSequence text;
        long duration;
        int retryCount;

        ToastRequest() {
        }
    }
}
