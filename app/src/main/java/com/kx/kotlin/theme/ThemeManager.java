package com.kx.kotlin.theme;

import android.content.Context;
import android.content.SharedPreferences;

import com.kx.kotlin.R;


public class ThemeManager {

    public static Context mContext;

    public static void init(Context context) {
        mContext = context.getApplicationContext();
    }

    public enum THEME {
        DAY,
        NIGHT
    }

    public static THEME changeTheme(Context context) {
        if (context == null) {
            return THEME.DAY;
        }
        THEME theme;
        SharedPreferences spf = context.getSharedPreferences("settings", 0);
        if (THEME.valueOf(spf.getString("theme", THEME.DAY.name())) == THEME.DAY) {
            theme = THEME.NIGHT;
            context.setTheme(R.style.AppTheme_Night);
        } else {
            theme = THEME.DAY;
            context.setTheme(R.style.AppTheme_Day);
        }
        spf.edit().putString("theme", theme.name()).apply();
        return theme;
    }

    public static THEME valueOf() {
        THEME valueOf;
        synchronized (ThemeManager.class) {
            valueOf = THEME.valueOf(mContext.getSharedPreferences("settings", 0).getString("theme", THEME.DAY.name()));
        }
        return valueOf;
    }

    public static boolean isDay() {
        return valueOf() == THEME.DAY;
    }

    public static boolean isNight() {
        return valueOf() == THEME.NIGHT;
    }

    public static int getTheme() {
        return isDay() ? R.style.AppTheme_Day : R.style.AppTheme_Night;
    }
}
