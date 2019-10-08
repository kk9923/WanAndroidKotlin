package com.kx.kotlin.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.NavigationView
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.GravityCompat
import android.support.v4.view.MenuItemCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import com.kx.kotlin.R
import com.kx.kotlin.WanAndroidApplication.Companion.context
import com.kx.kotlin.base.BaseActivity
import com.kx.kotlin.base.BaseObserver
import com.kx.kotlin.bean.UserInfo
import com.kx.kotlin.event.LoginEvent
import com.kx.kotlin.ext.showToast
import com.kx.kotlin.fragment.HomeFragment
import com.kx.kotlin.fragment.KnowledgeTreeFragment
import com.kx.kotlin.fragment.ProjectFragment
import com.kx.kotlin.fragment.WeChatFragment
import com.kx.kotlin.http.RetrofitHelper
import com.kx.kotlin.theme.ResourceUtils
import com.kx.kotlin.theme.ThemeEvent
import com.kx.kotlin.theme.ThemeManager
import com.kx.kotlin.theme.ThemeUtils
import com.kx.kotlin.util.RxUtil
import com.kx.kotlin.util.RxUtils
import com.kx.kotlin.util.SPUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import kotlinx.android.synthetic.main.toolbar.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.doAsync


class MainActivity : BaseActivity(), View.OnClickListener {

    private val BOTTOM_INDEX: String = "bottom_index"
    private val FRAGMENT_HOME = 0
    private val FRAGMENT_KNOWLEDGE = 1
    private val FRAGMENT_WECHAT = 2
    private val FRAGMENT_NAVIGATION = 3
    private val FRAGMENT_PROJECT = 4

    private var mHomeFragment: HomeFragment? = null
    private var mKnowledgeTreeFragment: KnowledgeTreeFragment? = null
    private var mWeChatFragment: WeChatFragment? = null
    private var mProjectFragment: ProjectFragment? = null

    private var mIndex = FRAGMENT_HOME

    private var nav_night_mode: TextView? = null
    private var nav_night_mode_menu: MenuItem? = null
    private var nav_view_header: LinearLayout? = null
    private var nav_username: TextView? = null
    private var nav_user_id: TextView? = null
    private var nav_user_grade: TextView? = null
    private var nav_user_rank: TextView? = null
    private var nav_score: TextView? = null

    override fun onClick(v: View?) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fullScreen(this)
        setTheme(ThemeManager.getTheme())
        setContentView(R.layout.activity_main)
        toolbar.run {
            setSupportActionBar(this)
            setToolBarEnableScroll(false)
        }
        initDrawerLayout()
        initBottomNavigation()
        initNavView()

        fab.run {
            setOnClickListener(onFABClickListener)
        }

        if (savedInstanceState != null) {
            mIndex = savedInstanceState.getInt(BOTTOM_INDEX)
        }
        showFragment(mIndex)

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        if (isLogin){
            getUserInfo()
        }
    }

    private fun initNavView() {
        nav_view.run {
            nav_night_mode_menu = menu.findItem(R.id.nav_night_mode)
            nav_username = getHeaderView(0).findViewById(R.id.tv_username)
            nav_user_id = getHeaderView(0).findViewById(R.id.tv_user_id)
            nav_user_grade = getHeaderView(0).findViewById(R.id.tv_user_grade)
            nav_user_rank = getHeaderView(0).findViewById(R.id.tv_user_rank)
            nav_night_mode = MenuItemCompat.getActionView(nav_view.menu.findItem(R.id.nav_night_mode)) as TextView
            nav_score = MenuItemCompat.getActionView(nav_view.menu.findItem(R.id.nav_score)) as TextView
            nav_score?.gravity = Gravity.CENTER_VERTICAL
            nav_view_header = getHeaderView(0).findViewById(R.id.nav_view_header)
            nav_view_header.run {
                if(!isLogin){
                    setOnClickListener {
                        goLogin()
                    }
                }
            }
            setNavigationItemSelectedListener(onDrawerNavigationItemSelectedListener)
            menu.findItem(R.id.nav_logout).isVisible = isLogin
        }
        nav_username?.run {
            text = if (!isLogin) getString(R.string.go_login) else username
        }
    }
    /**
     * 展示Fragment
     * @param index
     */
    private fun showFragment(index: Int) {
        val transaction = supportFragmentManager.beginTransaction()
        hideFragments(transaction)
        mIndex = index
        when (index) {
            FRAGMENT_HOME // 首页
            -> {
                if (mHomeFragment == null) {
                    mHomeFragment = HomeFragment.getInstance()
                    transaction.add(R.id.container, mHomeFragment!!, "home")
                } else {
                    transaction.show(mHomeFragment!!)
                }
            }
            FRAGMENT_KNOWLEDGE // 知识体系
            -> {
                if (mKnowledgeTreeFragment == null) {
                    mKnowledgeTreeFragment = KnowledgeTreeFragment.getInstance()
                    transaction.add(R.id.container, mKnowledgeTreeFragment!!, "knowledge")
                } else {
                    transaction.show(mKnowledgeTreeFragment!!)
                }
            }
            FRAGMENT_WECHAT // 公众号
            -> {
                if (mWeChatFragment == null) {
                    mWeChatFragment = WeChatFragment.getInstance()
                    transaction.add(R.id.container, mWeChatFragment!!, "wechat")
                } else {
                    transaction.show(mWeChatFragment!!)
                }
            }
            FRAGMENT_NAVIGATION // 导航
            -> {
//                toolbar.title = getString(R.string.navigation)
//                if (mNavigationFragment == null) {
//                    mNavigationFragment = NavigationFragment.getInstance()
//                    transaction.add(R.id.container, mNavigationFragment!!, "navigation")
//                } else {
//                    transaction.show(mNavigationFragment!!)
//                }
            }
            FRAGMENT_PROJECT // 项目
            -> {
                if (mProjectFragment == null) {
                    mProjectFragment = ProjectFragment.getInstance()
                    transaction.add(R.id.container, mProjectFragment!!, "project")
                } else {
                    transaction.show(mProjectFragment!!)
                }
            }
        }
        transaction.commit()
    }

    /**
     * 隐藏所有的Fragment
     */
    private fun hideFragments(transaction: FragmentTransaction) {
        mHomeFragment?.let { transaction.hide(it) }
        mKnowledgeTreeFragment?.let { transaction.hide(it) }
        mWeChatFragment?.let { transaction.hide(it) }
    //    mNavigationFragment?.let { transaction.hide(it) }
        mProjectFragment?.let { transaction.hide(it) }
    }

    /**
     * NavigationView 监听
     */
    private val onDrawerNavigationItemSelectedListener =
        NavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_score -> {
                    if (isLogin) {
                        Intent(this@MainActivity, ScoreActivity::class.java).run {
                            startActivity(this)
                        }
                    } else {
                        goLogin()
                    }
                }
                R.id.nav_collect -> {
                    if (isLogin) {
                        Intent(this@MainActivity, MyCollectActivity::class.java).run {
                            startActivity(this)
                        }
                    } else {
                        goLogin()
                    }
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
                    showToast(getString(R.string.nav_todo))
                }
                R.id.nav_night_mode -> {
                    ThemeManager.changeTheme(context)
                    ThemeUtils.startThemeChangeRevealAnimation(mActivity, nav_night_mode)
                }
                R.id.nav_setting -> {
//                    Intent(this@MainActivity, SettingActivity::class.java).run {
//                        // putExtra(Constant.TYPE_KEY, Constant.Type.SETTING_TYPE_KEY)
//                        startActivity(this)
//                    }
                    showToast(getString(R.string.nav_setting))
                }
                R.id.nav_about_us -> {
                    Intent(this@MainActivity, AboutUsActivity::class.java).run {
                        startActivity(this)
                    }
                    // goCommonActivity(Constant.Type.ABOUT_US_TYPE_KEY)
                }
                R.id.nav_logout -> {
                     logout()
                }
            }
            // drawer_layout.closeDrawer(GravityCompat.START)
            true
        }

    private fun initBottomNavigation() {
        bottom_navigation.run {
            labelVisibilityMode = 1
//            setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
            setOnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.action_home -> {
                        toolbar.title = getString(R.string.app_name)
                        setToolBarEnableScroll(false)
                        showFragment(FRAGMENT_HOME)
                        true
                    }
                    R.id.action_knowledge_system -> {
                        toolbar.title = getString(R.string.knowledge_system)
                        setToolBarEnableScroll(false)
                        showFragment(FRAGMENT_KNOWLEDGE)
                        true
                    }
                    R.id.action_wechat -> {
                        toolbar.title = getString(R.string.wechat)
                        setToolBarEnableScroll(true)
                        showFragment(FRAGMENT_WECHAT)
                        true
                    }
                    R.id.action_navigation -> {
                        toolbar.title = getString(R.string.navigation)
                        setToolBarEnableScroll(false)
                        showFragment(FRAGMENT_NAVIGATION)
                        true
                    }
                    R.id.action_project -> {
                        toolbar.title = getString(R.string.project)
                        setToolBarEnableScroll(true)
                        showFragment(FRAGMENT_PROJECT)
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
                    showFragment(FRAGMENT_KNOWLEDGE)
                    true
                }
                R.id.action_knowledge_system -> {
                    toolbar.title = getString(R.string.knowledge_system)
                    setToolBarEnableScroll(false)
                    showFragment(FRAGMENT_KNOWLEDGE)
                    true
                }
                R.id.action_wechat -> {
                    toolbar.title = getString(R.string.wechat)
                    setToolBarEnableScroll(true)
                    showFragment(FRAGMENT_WECHAT)
                    true
                }
                R.id.action_navigation -> {
                    toolbar.title = getString(R.string.navigation)
                    setToolBarEnableScroll(false)
                    showFragment(FRAGMENT_NAVIGATION)
                    true
                }
                R.id.action_project -> {
                    toolbar.title = getString(R.string.project)
                    setToolBarEnableScroll(true)
                    showFragment(FRAGMENT_PROJECT)
                    true
                }
                else -> {
                    false
                }
            }
        }

    private fun initDrawerLayout() {
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

    /**
     * Logout
     */
    private fun logout() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.confirm_logout))
        builder.setPositiveButton("确定") { _, _ ->
            RetrofitHelper.service.logout().compose(RxUtils.ioMain())
                .subscribe({
                    doAsync {
                        // CookieManager().clearAllCookies()
                        SPUtils.clearPreference()
                        runOnUiThread {
                            showToast(resources.getString(R.string.logout_success))
                            username = tv_username.text.toString().trim()
                            isLogin = false
                            EventBus.getDefault().post(LoginEvent(false))
                        }
                    }
                },{

                })
        }
        builder.setNegativeButton("取消", null)
        builder.show()
    }

    /**
     * FAB 监听
     */
    private val onFABClickListener = View.OnClickListener {
        when (mIndex) {
            FRAGMENT_HOME -> {
                mHomeFragment?.scrollToTop()
            }
            FRAGMENT_KNOWLEDGE -> {
                mKnowledgeTreeFragment?.scrollToTop()
            }
            FRAGMENT_NAVIGATION -> {
              //  mNavigationFragment?.scrollToTop()
            }
            FRAGMENT_PROJECT -> {
                mProjectFragment?.scrollToTop()
            }
            FRAGMENT_WECHAT -> {
                mWeChatFragment?.scrollToTop()
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAppThemeChange(themeEvent: ThemeEvent) {
        bottom_navigation.setBackgroundColor(ResourceUtils.resolveData(context, R.attr.common_bg))
        nav_view.setBackgroundColor(ResourceUtils.resolveData(context, R.attr.common_bg))
        nav_view_header?.setBackgroundColor(ResourceUtils.resolveData(context, R.attr.nav_header_bg))

        val isDay = ThemeManager.isDay()
        nav_night_mode_menu?.title = if (isDay) getString(R.string.nav_night_mode) else getString(R.string.nav_day_mode)

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun loginEvent(event: LoginEvent) {
        if (event.isLogin) {
            tv_username.text = username
            nav_view.menu.findItem(R.id.nav_logout).isVisible = true
            //  mHomeFragment?.lazyLoad()
            //  mPresenter?.getUserInfo()
            getUserInfo()
        } else {
            tv_username.text = getString(R.string.go_login)
            nav_view.menu.findItem(R.id.nav_logout).isVisible = false
            //  mHomeFragment?.lazyLoad()
            // 重置用户信息
               tv_user_id?.text = getString(R.string.nav_line_4)
               nav_user_grade?.text = getString(R.string.nav_line_2)
               nav_user_rank?.text = getString(R.string.nav_line_2)
               nav_score?.text = ""
        }
    }

    private fun getUserInfo() {
        addDisposable(RetrofitHelper.service.getUserInfo().compose(RxUtil.ioMain())
            .subscribeWith(object :BaseObserver<UserInfo>(){
                override fun onSuccess(result: UserInfo) {
                    tv_user_id?.text = result.userId.toString()
                    nav_user_grade?.text = (result.coinCount / 100 + 1).toString()
                    nav_user_rank?.text = result.rank.toString()
                    nav_score?.text = result.coinCount.toString()
                }
                override fun onError(errorMsg: String) {
                }
            })
        )
    }

    private fun goLogin() {
        showToast(resources.getString(R.string.login_tint))
        Intent(this@MainActivity, LoginActivity::class.java).run {
            startActivity(this)
        }
    }

    private var lastPressTime : Long = 0

    override fun onBackPressed() {
        val drawerOpen = drawer_layout.isDrawerOpen(GravityCompat.START)
        if (drawerOpen){
            drawer_layout.closeDrawer(GravityCompat.START)
            return
        }
        if (System.currentTimeMillis() - lastPressTime  > 2000){
            lastPressTime = System.currentTimeMillis()
            showToast(getString(R.string.exit_tip))
            return
        }
        super.onBackPressed()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt(BOTTOM_INDEX, mIndex)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }
}

