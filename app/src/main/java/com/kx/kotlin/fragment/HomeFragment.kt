package com.kx.kotlin.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.DefaultItemAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseQuickAdapter.OnItemClickListener
import com.kx.kotlin.R
import com.kx.kotlin.adapter.HomeListAdapter
import com.kx.kotlin.bean.ArticleResponseBody
import com.kx.kotlin.bean.Banner
import com.kx.kotlin.bean.HomeData
import com.kx.kotlin.bean.HttpResult
import com.kx.kotlin.constant.Constant
import com.kx.kotlin.http.RetrofitHelper
import com.kx.kotlin.theme.ThemeEvent
import com.kx.kotlin.ui.ArticlesDetailActivity
import com.kx.kotlin.util.RxUtils
import com.kx.kotlin.widget.GlideImageLoader
import com.kx.kotlin.widget.SpaceItemDecoration
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.youth.banner.BannerConfig
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import kotlinx.android.synthetic.main.fragment_home.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class HomeFragment : BaseFragment() {


    private var bannerView: com.youth.banner.Banner? = null

    private var pageNum: Int = 0

    companion object {
        fun getInstance(): HomeFragment = HomeFragment()
    }

    var rootView: View? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_home, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    @SuppressLint("CheckResult")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recyclerView.run {
            adapter = homeListAdapter
            itemAnimator = DefaultItemAnimator()
            //recyclerViewItemDecoration?.let { addItemDecoration(it) }
        }
        val headerView = LayoutInflater.from(activity).inflate(R.layout.home_banner, null)
        bannerView = headerView.findViewById(R.id.banner)

        homeListAdapter.run {
            addHeaderView(headerView)
            onItemClickListener = this@HomeFragment.onItemClickListener
            setOnLoadMoreListener(object : BaseQuickAdapter.RequestLoadMoreListener {
                override fun onLoadMoreRequested() {
                    addDisposable(getArticles(pageNum)
                            .subscribe({
                                refreshLayout.getLayout().postDelayed(object : Runnable {
                                    override fun run() {
                                        val datas = it.data.datas
                                        homeListAdapter.addData(datas)
                                        pageNum++
                                        loadMoreComplete()
                                    }
                                }, 1000)
                            }, { it ->
                                run {
                                    Toast.makeText(activity, "$it.message", Toast.LENGTH_SHORT).show()
                                }
                            })


                    )
                }

            }, recyclerView)
        }
        refreshLayout.run {
            setEnableLoadMore(false)
            setOnRefreshListener(object : OnRefreshListener {
                override fun onRefresh(refreshLayout: RefreshLayout) {
                    finishRefresh(true)
                }
            })
//            setOnLoadMoreListener(object : OnLoadMoreListener {
//                override fun onLoadMore(refreshLayout: RefreshLayout) {
//                    addDisposable(getArticles(pageNum)
//                            .subscribe({
//                                refreshLayout.getLayout().postDelayed(object :Runnable{
//                                    override fun run() {
//                                        finishLoadMore()
//                                        val datas = it.data.datas
//                                        homeListAdapter.addData(datas)
//                                        pageNum++
//
//                                    }
//                                }, 1000)
//                            }, { it ->
//                                run {
//                                    Toast.makeText(activity, "$it.message", Toast.LENGTH_SHORT).show()
//                                }
//                            }
//                            )
//                    )
//                }
//            })
        }
        val banner = getBanner()
        val articles = getArticles(pageNum)

        addDisposable(Observable.zip(banner, articles,
                BiFunction<HttpResult<List<Banner>>, HttpResult<ArticleResponseBody>, HomeData> { t1, t2 ->
                    val homeData = HomeData(t1.data, t2.data.datas)
                    homeData
                })
                .subscribe({ homeData ->
                    val banners = homeData.banners
                    val images = arrayListOf<String>()
                    val titles = arrayListOf<String>()
                    Observable.fromIterable(banners)
                            .subscribe { item ->
                                images.add(item.imagePath)
                                titles.add(item.title)
                            }
                    bannerView?.setImageLoader(GlideImageLoader())
                    bannerView?.setImages(images)
                    bannerView?.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE)
                    bannerView?.setBannerTitles(titles)
                    bannerView?.setDelayTime(3000)
                    bannerView?.setIndicatorGravity(BannerConfig.RIGHT)
                    bannerView?.start()
                    homeListAdapter.setNewData(homeData.articles)
                    pageNum++
                }, { it ->
                    run {
                        Toast.makeText(activity, "$it.message", Toast.LENGTH_SHORT).show()
                    }
                }
                ))
    }


    private fun getBanner(): Observable<HttpResult<List<Banner>>> {
        return RetrofitHelper.service.getBanners().compose(RxUtils.rxSchedulerHelper())
    }

    private fun getArticles(pageNum: Int): Observable<HttpResult<ArticleResponseBody>> {
        return RetrofitHelper.service.getArticles(pageNum).compose(RxUtils.rxSchedulerHelper())
    }

    private val recyclerViewItemDecoration by lazy {
        activity?.let {
            SpaceItemDecoration(it)
        }
    }
    private val homeListAdapter: HomeListAdapter by lazy {
        HomeListAdapter(activity)
    }

    private val onItemClickListener = object : OnItemClickListener {
        override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
            val itemData = homeListAdapter.data[position]
            val intent = Intent(activity, ArticlesDetailActivity::class.java)
            intent.run {
                putExtra(Constant.CONTENT_URL_KEY, itemData.link)
                putExtra(Constant.CONTENT_TITLE_KEY, itemData.title)
                putExtra(Constant.CONTENT_ID_KEY, itemData.id)
                startActivity(this)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
        clearDispose()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAppThemeChange(themeEvent: ThemeEvent) {
        homeListAdapter.notifyDataSetChanged()
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
