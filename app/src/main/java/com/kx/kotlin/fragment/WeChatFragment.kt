package com.kx.kotlin.fragment

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.view.View
import com.kx.kotlin.R
import com.kx.kotlin.adapter.WeChatPagerAdapter
import com.kx.kotlin.base.BaseFragment
import com.kx.kotlin.base.BaseObserver
import com.kx.kotlin.bean.WXChapterBean
import com.kx.kotlin.ext.showToast
import com.kx.kotlin.http.RetrofitHelper
import com.kx.kotlin.util.RxUtil
import kotlinx.android.synthetic.main.fragment_wechat.*

class WeChatFragment : BaseFragment() {

    companion object {
        fun getInstance(): WeChatFragment = WeChatFragment()
    }

    private val datas = mutableListOf<WXChapterBean>()

    override fun attachLayoutRes(): Int = R.layout.fragment_wechat

    override fun initView() {
        viewPager.run {
            addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        }

        tabLayout.run {
            setupWithViewPager(viewPager)
            // TabLayoutHelper.setUpIndicatorWidth(tabLayout)
            addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(viewPager))
            addOnTabSelectedListener(onTabSelectedListener)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getWXChapters()
    }

    private fun getWXChapters() {
        addDisposable(RetrofitHelper.service.getWXChapters().compose(RxUtil.ioMain())
                .subscribeWith(object : BaseObserver<MutableList<WXChapterBean>>() {
                    override fun onSuccess(result: MutableList<WXChapterBean>) {
                        datas.addAll(result)
                        viewPager.run {
                            adapter = viewPagerAdapter
                            offscreenPageLimit = result.size
                        }
                    }

                    override fun onError(errorMsg: String) {
                        showToast(errorMsg)
                    }
                }
            )
        )
    }
    private val viewPagerAdapter: WeChatPagerAdapter by lazy {
        WeChatPagerAdapter(datas, childFragmentManager)
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

     fun scrollToTop() {
        if (viewPagerAdapter.count == 0) {
            return
        }
        val fragment: KnowledgeFragment = viewPagerAdapter.getItem(viewPager.currentItem) as KnowledgeFragment
        fragment.scrollToTop()
    }
}