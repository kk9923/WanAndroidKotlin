package com.kx.kotlin.ui

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.view.View
import com.kx.kotlin.R
import com.kx.kotlin.adapter.KnowledgePagerAdapter
import com.kx.kotlin.base.BaseActivity
import com.kx.kotlin.bean.Knowledge
import com.kx.kotlin.bean.KnowledgeTreeBody
import com.kx.kotlin.constant.Constant
import com.kx.kotlin.fragment.KnowledgeFragment
import kotlinx.android.synthetic.main.activity_knowledge.*

class KnowledgeActivity : BaseActivity() {

    private lateinit var toolbarTitle: String

    private var knowledges = mutableListOf<Knowledge>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_knowledge)

        intent.run {
            val knowledgeTreeBody = getSerializableExtra(Constant.CONTENT_DATA_KEY)
            knowledgeTreeBody.let {
                val data = it as KnowledgeTreeBody
                toolbarTitle = data.name
                data.children.let { children ->
                    knowledges.addAll(children)
                }
            }
        }
        initView()
    }
     fun initView() {
        toolbar.run {
            title = toolbarTitle
            setSupportActionBar(this)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            //StatusBarUtil2.setPaddingSmart(this@KnowledgeActivity, toolbar)
        }
        viewPager.run {
            adapter = viewPagerAdapter
            addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
            offscreenPageLimit = knowledges.size
        }
        tabLayout.run {
            setupWithViewPager(viewPager)
            // TabLayoutHelper.setUpIndicatorWidth(tabLayout)
            addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(viewPager))
            addOnTabSelectedListener(onTabSelectedListener)
        }
        floating_action_btn.run {
            setOnClickListener(onFABClickListener)
        }
    }

    private val onTabSelectedListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabReselected(tab: TabLayout.Tab?) {
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {
        }

        override fun onTabSelected(tab: TabLayout.Tab?) {
            // 默认切换的时候，会有一个过渡动画，设为false后，取消动画，直接显示
            tab?.let {
                viewPager.setCurrentItem(it.position, false)
            }
        }
    }

    private val onFABClickListener = View.OnClickListener {
        if (viewPagerAdapter.count == 0) {
            return@OnClickListener
        }
        val fragment: KnowledgeFragment = viewPagerAdapter.getItem(viewPager.currentItem) as KnowledgeFragment
        fragment.scrollToTop()
    }

    private val viewPagerAdapter: KnowledgePagerAdapter by lazy {
        KnowledgePagerAdapter(knowledges, supportFragmentManager)
    }
}
