package com.kx.kotlin.fragment

import android.content.SharedPreferences
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.chad.library.adapter.base.BaseQuickAdapter
import com.kx.kotlin.R
import com.kx.kotlin.R.id.recyclerView
import com.kx.kotlin.R.id.refreshLayout
import com.kx.kotlin.adapter.HomeListAdapter
import com.kx.kotlin.base.BaseFragment
import com.kx.kotlin.base.BaseObserver
import com.kx.kotlin.base.BaseViewModelFragment
import com.kx.kotlin.bean.ArticleResponseBody
import com.kx.kotlin.bean.Banner
import com.kx.kotlin.bean.HomeData
import com.kx.kotlin.bean.HttpResult
import com.kx.kotlin.ext.showToast
import com.kx.kotlin.http.RetrofitHelper
import com.kx.kotlin.theme.ThemeEvent
import com.kx.kotlin.ui.ArticlesDetailActivity
import com.kx.kotlin.util.RxUtil
import com.kx.kotlin.util.RxUtils
import com.kx.kotlin.viewmmodel.HomeFragmentViewModel
import com.kx.kotlin.widget.GlideImageLoader
import com.youth.banner.BannerConfig
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.coroutines.CoroutineContext


class HomeFragment : BaseViewModelFragment<HomeFragmentViewModel>() {


    private var bannerView: com.youth.banner.Banner? = null

    private var pageNum: Int = 0

    private var bannerUrls: MutableList<Banner> = mutableListOf()

    companion object {
        fun getInstance(): HomeFragment = HomeFragment()
    }

    fun SharedPreferences.put(put : SharedPreferences.Editor.() -> Unit){
        val editor = edit()
        editor.put()
        editor.apply()
    }

    override fun attachLayoutRes(): Int = R.layout.fragment_home

    override fun initView() {
        recyclerView.run {
            adapter = homeListAdapter
            layoutManager = mLayoutManager
            itemAnimator = null
            recyclerViewItemDecoration?.let { addItemDecoration(it) }
        }
        val headerView = LayoutInflater.from(activity).inflate(R.layout.home_banner, null)
        bannerView = headerView.findViewById(R.id.banner)
        bannerView.run {
            this!!.setImageLoader(GlideImageLoader())
                .setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE)
                .setDelayTime(3000)
                .setIndicatorGravity(BannerConfig.RIGHT)
                .setOnBannerListener {
                    val itemData = bannerUrls[it]
                    ArticlesDetailActivity.start(
                        activity,
                        itemData.id,
                        itemData.title,
                        itemData.url
                    )
                }
        }

        homeListAdapter.run {
            addHeaderView(headerView)
            onItemClickListener = this@HomeFragment.onItemClickListener
            onItemChildClickListener = this@HomeFragment.onItemChildClickListener
        }
        refreshLayout.run {
            setOnRefreshListener {
                onRefresh()
            }
            setOnLoadMoreListener {
                loadMore()
            }
        }
        refreshLayout.autoRefresh()
        getViewModel().getBanners()
        getViewModel().liveData.observe(this,
            Observer<List<Banner>> {
                it?.forEach {banner ->
                    println("forEach -> "+banner.imagePath)
                }
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    private fun onRefresh() {
        var viewModel = getViewModel()
        pageNum = 0
        val banner = getBanner()
        val articles = getArticles(pageNum)
        addDisposable(Observable.zip(banner, articles,
            BiFunction<HttpResult<List<Banner>>, HttpResult<ArticleResponseBody>, HomeData> { t1, t2 ->
                val homeData = HomeData(t1.data, t2.data.datas)
                homeData
            })
            .subscribe({ homeData ->
                val banners = homeData.banners
                bannerUrls.clear()
                val images = arrayListOf<String>()
                val titles = arrayListOf<String>()
                Observable.fromIterable(banners)
                    .subscribe { item ->
                        images.add(item.imagePath)
                        titles.add(item.title)
                        bannerUrls.add(item)
                    }
                bannerView?.setImages(images)
                bannerView?.setBannerTitles(titles)
                bannerView?.start()
                homeListAdapter.setNewData(homeData.articles)
                pageNum++
                refreshLayout.finishRefresh(true)
            }, { it ->
                run {
                    showToast("$it.message")
                }
            }
            ))
    }

    private fun loadMore() {
        //  加载更多的时候禁调滑动
        //  直接设置接口数据时,当界面刷新完毕后,会在屏幕底部显示加载完成的文字
        recyclerView.stopScroll()
        addDisposable(getArticles(pageNum)
            .subscribe({
                val datas = it.data.datas
                homeListAdapter.addData(datas)
                pageNum++
                refreshLayout.finishLoadMore()
            }, { it ->
                run {
                    showToast("$it.message")
                }
            }
            )
        )
    }


    private fun getBanner(): Observable<HttpResult<List<Banner>>> {
        return RetrofitHelper.service.getBanners().compose(RxUtils.ioMain())
    }

    private fun getArticles(pageNum: Int): Observable<HttpResult<ArticleResponseBody>> {
        return RetrofitHelper.service.getArticles(pageNum).compose(RxUtils.ioMain())
    }

    private val recyclerViewItemDecoration by lazy {
        activity?.let {
            //    SpaceItemDecoration(it)
            androidx.recyclerview.widget.DividerItemDecoration(
                activity,
                androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
            )
        }
    }
    private val homeListAdapter: HomeListAdapter by lazy {
        HomeListAdapter(activity)
    }

    private val onItemClickListener =
        BaseQuickAdapter.OnItemClickListener { _, _, position ->
            val itemData = homeListAdapter.data[position]
            ArticlesDetailActivity.start(activity, itemData.id, itemData.title, itemData.link)
        }

    private val onItemChildClickListener =
        BaseQuickAdapter.OnItemChildClickListener { _, view, position ->
            if (homeListAdapter.data.size != 0) {
                val data = homeListAdapter.data[position]
                when (view.id) {
                    R.id.iv_like -> {
                        if (data.collect) {
                            cancelCollectArticle(data.id, position)
                        } else {
                            addCollectArticle(data.id, position)
                        }
                    }
                }
            }
        }

    private fun addCollectArticle(id: Int, position: Int) {
        addDisposable(
            RetrofitHelper.service.addCollectArticle(id)
                .compose(RxUtil.ioMain())
                .subscribeWith(object : BaseObserver<Any>() {
                    override fun onSuccess() {
                        showToast(getString(R.string.collect_success))
                        homeListAdapter.data[position].collect = true
                        notifyItemChanged(position)
                    }

                    override fun onError(errorMsg: String) {
                        showToast(errorMsg)
                    }
                })
        )
    }

    private fun cancelCollectArticle(id: Int, position: Int) {
        addDisposable(
            RetrofitHelper.service.cancelCollectArticle(id)
                .compose(RxUtil.ioMain())
                .subscribeWith(object : BaseObserver<Any>() {
                    override fun onSuccess() {
                        showToast(getString(R.string.cancel_collect_success))
                        homeListAdapter.data[position].collect = false
                        notifyItemChanged(position)
                    }

                    override fun onError(errorMsg: String) {
                        showToast(errorMsg)
                    }
                })
        )
    }

    /**
     * 因为添加了一个banner头,所以 notifyItemChanged 时需要加上header的个数
     */
    private fun notifyItemChanged(position: Int) {
        homeListAdapter.notifyItemChanged(position + homeListAdapter.headerLayoutCount)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAppThemeChange(themeEvent: ThemeEvent) {
        homeListAdapter.notifyDataSetChanged()
    }

    private val mLayoutManager: androidx.recyclerview.widget.LinearLayoutManager by lazy {
        androidx.recyclerview.widget.LinearLayoutManager(activity)
    }

    fun scrollToTop() {
        recyclerView.run {
            if (mLayoutManager.findFirstVisibleItemPosition() > 40) {
                scrollToPosition(0)
            } else {
                smoothScrollToPosition(0)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        bannerView?.startAutoPlay()
    }

    override fun onStop() {
        super.onStop()
        bannerView?.stopAutoPlay()
    }
}
