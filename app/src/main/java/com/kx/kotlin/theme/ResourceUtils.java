package com.kx.kotlin.theme;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.core.content.res.ResourcesCompat;
import android.util.TypedValue;

public class ResourceUtils {
    public static int resolveData(Context context , int i) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(i, typedValue, true);
        return typedValue.data;
    }

    public static int resolveResId(Context context, int i) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(i, typedValue, true);
        return typedValue.resourceId;
    }

    public static CharSequence resolveString(Context context, int i) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(i, typedValue, true);
        return typedValue.string;
    }

    public static int getColorInt(Context context, int i) {
        return ResourcesCompat.getColor(context.getResources(), i, context.getTheme());
    }

    public static Drawable getDrawable(Context context, int i) {
        return ResourcesCompat.getDrawable(context.getResources(), i, context.getTheme());
    }

}
