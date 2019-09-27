package com.kx.kotlin.util

import android.content.res.Resources

class DensityUtil {

    companion object {

        private fun getDensity():Float{
            return Resources.getSystem().displayMetrics.density
        }

        /**
         * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
         * @param dpValue 虚拟像素
         * @return 像素
         */
        fun dp2px(dpValue: Int): Int {
            return (0.5f + dpValue * getDensity()).toInt()
        }

        /**
         * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
         * @param pxValue 像素
         * @return 虚拟像素
         */
        fun px2dp(pxValue: Int): Float {
            return pxValue / getDensity()
        }
    }
}