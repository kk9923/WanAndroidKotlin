package com.kx.kotlin.theme;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import org.greenrobot.eventbus.EventBus;


public class ThemeUtils {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void startThemeChangeRevealAnimation(final Activity activity, View view) {
        if (activity != null) {
            final ViewGroup viewGroup = (ViewGroup) activity.getWindow().getDecorView();
            Bitmap takeSnapshot = takeSnapshot(viewGroup);
            final ImageView imageView = new ImageView(activity);
            imageView.setImageDrawable(new BitmapDrawable(activity.getResources(), takeSnapshot));
            viewGroup.addView(imageView, 0, new ViewGroup.LayoutParams(-1, -1));
            final View view2 = new View(activity);
            view2.setClickable(true);
            viewGroup.addView(view2, new ViewGroup.LayoutParams(-1, -1));
            int[] iArr = new int[2];
            view.getLocationInWindow(iArr);
            int width = iArr[0] + (view.getWidth() / 2);
            int height = iArr[1] + (view.getHeight() / 2);
            Animator createCircularReveal = ViewAnimationUtils.createCircularReveal(
                    viewGroup.getChildAt(1), width, height,
                    (float) ((Math.max(view.getWidth(), view.getHeight()) / 2) + dp2px(16, activity)),
                    (float) ((int) Math.sqrt(Math.pow((double) Math.max(width, viewGroup.getWidth() - width), 2.0d) +
                            Math.pow((double) Math.max(height, viewGroup.getHeight() - height), 2.0d))));
            createCircularReveal.setDuration(600);
            createCircularReveal.addListener(new AnimatorListenerAdapter() {
                public void onAnimationStart(Animator animator) {
                    postThemeChangeEvent();
                }

                public void onAnimationEnd(Animator animator) {
                    viewGroup.removeView(imageView);
                    viewGroup.removeView(view2);
//                    if (activity instanceof BaseActivity) {
//                        ((BaseActivity) activity).checkStatusBarDarkMode();
//                    }
                }
            });
            createCircularReveal.start();
        }
    }

    public static Bitmap takeSnapshot(View view) {
        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(0);
        Bitmap drawingCache = view.getDrawingCache();
        if (drawingCache == null) {
            return null;
        }
        drawingCache = Bitmap.createBitmap(drawingCache);
        view.setDrawingCacheEnabled(false);
        view.destroyDrawingCache();
        return drawingCache;
    }

    private static int dp2px(int dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    public static void postThemeChangeEvent() {
        EventBus.getDefault().post(new ThemeEvent(ThemeManager.valueOf()));
    }

}