//package com.kx.kotlin.ui
//
//import android.animation.ValueAnimator
//import android.content.pm.PackageManager
//import android.content.res.ColorStateList
//import android.os.Bundle
//import com.google.android.material.appbar.AppBarLayout
//import androidx.core.content.ContextCompat
//import android.text.Html
//import android.text.method.LinkMovementMethod
//import android.view.View
//import com.kx.kotlin.R
//import com.kx.kotlin.base.BaseActivity
//import com.kx.kotlin.util.DensityUtil
//import com.kx.kotlin.util.StatusBarUtil
//import com.kx.kotlin.widget.interpolator.ElasticOutInterpolator
//import com.scwang.smart.refresh.layout.api.RefreshHeader
//import com.scwang.smart.refresh.layout.api.RefreshLayout
//import kotlinx.android.synthetic.main.activity_about_us.*
//import kotlinx.android.synthetic.main.activity_about_us.about_us_content
//import kotlinx.android.synthetic.main.content_about.*
//
//open class AboutUsActivity : BaseActivity() {
//
//    private var mThemeListener: View.OnClickListener? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_about_us)
//        initToolbar()
//        initEventAndData()
//    }
//
//    private fun initToolbar() {
//        toolbar.run {
//            setSupportActionBar(this)
//            StatusBarUtil.immersive(this@AboutUsActivity)
//            StatusBarUtil.setPaddingSmart(this@AboutUsActivity, this)
//        }
//    }
//
//    private fun initEventAndData() {
//        showAboutContent()
//        setSmartRefreshLayout()
//
//        //进入界面时自动刷新
//        about_us_refresh_layout.autoRefresh()
//
//        //点击悬浮按钮时自动刷新
//        about_us_fab.setOnClickListener {  about_us_refresh_layout.autoRefresh() }
//
//        //监听 AppBarLayout 的关闭和开启 给 FlyView（纸飞机） 和 ActionButton 设置关闭隐藏动画
//        about_us_app_bar.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
//             var misAppbarExpand = true
//
//            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
//                val scrollRange = appBarLayout.totalScrollRange
//                val fraction = 1f * (scrollRange + verticalOffset) / scrollRange
//                val minFraction = 0.1
//                val maxFraction = 0.8
//                if (about_us_content == null || about_us_fab == null || about_us_fly_view == null) {
//                    return
//                }
//                if (fraction < minFraction && misAppbarExpand) {
//                    misAppbarExpand = false
//                    about_us_fab.animate().scaleX(0f).scaleY(0f)
//                    about_us_fly_view.animate().scaleX(0f).scaleY(0f)
//                    val animator = ValueAnimator.ofInt(about_us_content.paddingTop, 0)
//                    animator.duration = 300
//                    animator.addUpdateListener { animation ->
//                        if (about_us_content != null) {
//                            about_us_content.setPadding(0, animation.animatedValue as Int, 0, 0)
//                        }
//                    }
//                    animator.start()
//                }
//                if (fraction > maxFraction && !misAppbarExpand) {
//                    misAppbarExpand = true
//                    about_us_fab.animate().scaleX(1f).scaleY(1f)
//                    about_us_fly_view.animate().scaleX(1f).scaleY(1f)
//                    val animator = ValueAnimator.ofInt(about_us_content.paddingTop, DensityUtil.dp2px(25))
//                    animator.duration = 300
//                    animator.addUpdateListener { animation ->
//                        if (about_us_content != null) {
//                            about_us_content.setPadding(0, animation.animatedValue as Int, 0, 0)
//                        }
//                    }
//                    animator.start()
//                }
//            }
//        })
//    }
//
//    private fun setSmartRefreshLayout() {
//        //绑定场景和纸飞机
//        about_us_fly_refresh.setUp(about_us_mountain, about_us_fly_view)
//        about_us_refresh_layout.setReboundInterpolator(ElasticOutInterpolator())
//        about_us_refresh_layout.setReboundDuration(800)
//        about_us_refresh_layout.setOnRefreshListener { refreshLayout ->
//            updateTheme()
//            refreshLayout.finishRefresh(1000)
//        }
//
//        //设置让Toolbar和AppBarLayout的滚动同步
//        about_us_refresh_layout.setOnMultiPurposeListener(object : SimpleMultiPurposeListener() {
//            override fun onRefresh(refreshLayout: RefreshLayout) {
//                super.onRefresh(refreshLayout)
//                refreshLayout.finishRefresh(2000)
//            }
//
//            override fun onLoadMore(refreshLayout: RefreshLayout) {
//                super.onLoadMore(refreshLayout)
//                refreshLayout.finishLoadMore(3000)
//            }
//
//            override fun onHeaderMoving(
//                header: RefreshHeader?,
//                isDragging: Boolean,
//                percent: Float,
//                offset: Int,
//                headerHeight: Int,
//                maxDragHeight: Int
//            ) {
//                super.onHeaderMoving(header, isDragging, percent, offset, headerHeight, maxDragHeight)
//                if (about_us_app_bar == null || toolbar == null) {
//                    return
//                }
//                about_us_app_bar.translationY = offset.toFloat()
//                toolbar.translationY = (-offset).toFloat()
//            }
//        })
//    }
//
//    private fun showAboutContent() {
//        aboutContent.text = Html.fromHtml(getString(R.string.about_content))
//        aboutContent.movementMethod = LinkMovementMethod.getInstance()
//        try {
//            val versionStr =" V" + packageManager.getPackageInfo(packageName, 0).versionName
//            aboutVersion.text = versionStr
//        } catch (e: PackageManager.NameNotFoundException) {
//            e.printStackTrace()
//        }
//
//    }
//
//    private fun updateTheme() {
//        if (mThemeListener == null) {
//            mThemeListener = object : View.OnClickListener {
//                 var index = 0
//                 var ids = intArrayOf(
//                    R.color.colorPrimary,
//                    android.R.color.holo_green_light,
//                    android.R.color.holo_red_light,
//                    android.R.color.holo_orange_light,
//                    android.R.color.holo_blue_bright
//                )
//
//                override fun onClick(v: View?) {
//                    val color = ContextCompat.getColor(application, ids[index % ids.size])
//                    about_us_refresh_layout.setPrimaryColors(color)
//                    about_us_fab.setBackgroundColor(color)
//                    about_us_fab.backgroundTintList = ColorStateList.valueOf(color)
//                    about_us_toolbar_layout.setContentScrimColor(color)
//                    index++
//                }
//            }
//        }
//        mThemeListener!!.onClick(null)
//    }
//}
