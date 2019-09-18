package com.kx.kotlin.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.NavigationView
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.kx.kotlin.fragment.HomeFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*


class MainActivity : AppCompatActivity(), View.OnClickListener {

    val context by lazy { this }

    override fun onClick(v: View?) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fullScreen(this)
        setContentView(R.layout.activity_main)
        toolbar.run {
            title = getString(R.string.app_name)
            setSupportActionBar(this)
        }
        setToolBarEnableScroll(false)
        initDrawerLayout()
        initBottomNavigation()
        initNavView()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.container, HomeFragment.getInstance(), "home")
        transaction.commit()
    }

    private fun initNavView() {
        nav_view.run {
            setNavigationItemSelectedListener(onDrawerNavigationItemSelectedListener)
        }
    }

    /**
     * NavigationView 监听
     */
    private val onDrawerNavigationItemSelectedListener =
        NavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_score -> {
//                    if (isLogin) {
//                        Intent(this@MainActivity, ScoreActivity::class.java).run {
//                            startActivity(this)
//                        }
//                    } else {
//                        showToast(resources.getString(R.string.login_tint))
//                        goLogin()
//                    }
                    Toast.makeText(context, getString(R.string.nav_my_score), Toast.LENGTH_SHORT).show()
                }
                R.id.nav_collect -> {
//                    if (isLogin) {
//                        goCommonActivity(Constant.Type.COLLECT_TYPE_KEY)
//                    } else {
//                        showToast(resources.getString(R.string.login_tint))
//                        goLogin()
//                    }
                    Toast.makeText(context, getString(R.string.nav_my_collect), Toast.LENGTH_SHORT).show()
                }
                R.id.nav_todo -> {
//                    if (isLogin) {
//                        Intent(this@MainActivity, TodoActivity::class.java).run {
//                            startActivity(this)
//                        }
//                    } else {
//                        showToast(resources.getString(R.string.login_tint))
//                        goLogin()
//                    }
                    Toast.makeText(context, getString(R.string.nav_todo), Toast.LENGTH_SHORT).show()
                }
                R.id.nav_night_mode -> {
//                    if (SettingUtil.getIsNightMode()) {
//                        SettingUtil.setIsNightMode(false)
//                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//                    } else {
//                        SettingUtil.setIsNightMode(true)
//                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//                    }
//                    window.setWindowAnimations(R.style.WindowAnimationFadeInOut)
//                    recreate()
                    Toast.makeText(context, getString(R.string.nav_night_mode), Toast.LENGTH_SHORT).show()
                }
                R.id.nav_setting -> {
//                    Intent(this@MainActivity, SettingActivity::class.java).run {
//                        // putExtra(Constant.TYPE_KEY, Constant.Type.SETTING_TYPE_KEY)
//                        startActivity(this)
//                    }
                    Toast.makeText(context, getString(R.string.nav_setting), Toast.LENGTH_SHORT).show()
                }
                R.id.nav_about_us -> {
                    // goCommonActivity(Constant.Type.ABOUT_US_TYPE_KEY)
                    Toast.makeText(context, getString(R.string.nav_about_us), Toast.LENGTH_SHORT).show()
                }
                R.id.nav_logout -> {
                    // logout()
                    Toast.makeText(context, getString(R.string.nav_logout), Toast.LENGTH_SHORT).show()
                }
            }
            // drawer_layout.closeDrawer(GravityCompat.START)
            true
        }

    private fun initBottomNavigation() {
        bottom_navigation.run {
            // 以前使用 BottomNavigationViewHelper.disableShiftMode(this) 方法来设置底部图标和字体都显示并去掉点击动画
            // 升级到 28.0.0 之后，官方重构了 BottomNavigationView ，目前可以使用 labelVisibilityMode = 1 来替代
            // BottomNavigationViewHelper.disableShiftMode(this)
            labelVisibilityMode = 1
            setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
            setOnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.action_home -> {
                        toolbar.title = getString(R.string.app_name)
                        setToolBarEnableScroll(false)
                        true
                    }
                    R.id.action_knowledge_system -> {
                        toolbar.title = getString(R.string.knowledge_system)
                        setToolBarEnableScroll(false)
                        true
                    }
                    R.id.action_wechat -> {
                        toolbar.title = getString(R.string.wechat)
                        setToolBarEnableScroll(true)
                        true
                    }
                    R.id.action_navigation -> {
                        toolbar.title = getString(R.string.navigation)
                        setToolBarEnableScroll(false)
                        true
                    }
                    R.id.action_project -> {
                        toolbar.title = getString(R.string.project)
                        setToolBarEnableScroll(true)
                        true
                    }
                    else -> {
                        false
                    }
                }
            }
        }
    }

    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            return@OnNavigationItemSelectedListener when (item.itemId) {
                R.id.action_home -> {
                    toolbar.title = getString(R.string.app_name)
                    setToolBarEnableScroll(false)
                    true
                }
                R.id.action_knowledge_system -> {
                    toolbar.title = getString(R.string.knowledge_system)
                    setToolBarEnableScroll(false)
                    true
                }
                R.id.action_wechat -> {
                    toolbar.title = getString(R.string.wechat)
                    setToolBarEnableScroll(true)
                    true
                }
                R.id.action_navigation -> {
                    toolbar.title = getString(R.string.navigation)
                    setToolBarEnableScroll(false)
                    true
                }
                R.id.action_project -> {
                    toolbar.title = getString(R.string.project)
                    setToolBarEnableScroll(true)
                    true
                }
                else -> {
                    false
                }

            }
        }

    private fun initDrawerLayout() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            var params = window.attributes
//            params.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//            drawer_layout.fitsSystemWindows = true
//            drawer_layout.clipToPadding = false
//        }
        drawer_layout.run {
            val toggle = ActionBarDrawerToggle(
                this@MainActivity,
                this,
                toolbar
                , R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
            )
            addDrawerListener(toggle)
            toggle.syncState()
        }
    }

    private fun fullScreen(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
                val window = activity.window
                val decorView = window.decorView
                //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
                val option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                decorView.systemUiVisibility = option
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window.statusBarColor = Color.TRANSPARENT
                //导航栏颜色也可以正常设置
                //                window.setNavigationBarColor(Color.TRANSPARENT);
            } else {
                val window = activity.window
                val attributes = window.attributes
                val flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//                val flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
                attributes.flags = attributes.flags or flagTranslucentStatus
                //                attributes.flags |= flagTranslucentNavigation;
                window.attributes = attributes
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_search -> {
                Intent(this, SearchActivity::class.java).run {
                    startActivity(this)
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * 设置TootBar的滚动模式
     */
    private fun setToolBarEnableScroll(enable: Boolean) {
        val layoutParams = toolbar.layoutParams
        if (layoutParams is AppBarLayout.LayoutParams)
            if (enable) {
                layoutParams.scrollFlags = 5
            } else {
                layoutParams.scrollFlags = 0
            }
    }
}

