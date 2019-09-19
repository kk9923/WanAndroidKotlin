package com.kx.kotlin.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kx.kotlin.R
import com.kx.kotlin.adapter.HomeListAdapter
import com.kx.kotlin.bean.ArticleResponseBody
import com.kx.kotlin.bean.Banner
import com.kx.kotlin.bean.HttpResult
import com.kx.kotlin.http.RetrofitHelper
import com.kx.kotlin.theme.ThemeEvent
import com.kx.kotlin.widget.GlideImageLoader
import com.kx.kotlin.widget.SpaceItemDecoration
import com.youth.banner.BannerConfig
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_home.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class HomeFragment : Fragment() {

    var homeListAdapter :HomeListAdapter? = null
    private var bannerView: com.youth.banner.Banner? = null

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
        homeListAdapter = HomeListAdapter(activity)
        recyclerView.run {
            adapter = homeListAdapter
            itemAnimator = DefaultItemAnimator()
            recyclerViewItemDecoration?.let { addItemDecoration(it) }
        }
        val headerView  = LayoutInflater.from(activity).inflate(R.layout.home_banner,null)
        bannerView = headerView.findViewById(R.id.banner)
        homeListAdapter?.addHeaderView(headerView)
        RetrofitHelper.service.getBanners().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Consumer<HttpResult<List<Banner>>> {
                override fun accept(result: HttpResult<List<Banner>>) {
                    val images = arrayListOf<String>()
                    val titles = arrayListOf<String>()
                    Observable.fromIterable(result.data)
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
                }
                }, object : Consumer<Throwable> {
                    override fun accept(t: Throwable?) {
                    }
                }
            )
        RetrofitHelper.service.getArticles(0).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Consumer<HttpResult<ArticleResponseBody>> {
                override fun accept(result: HttpResult<ArticleResponseBody>) {
                    val datas = result.data.datas
                    homeListAdapter?.setNewData(datas)
                }
                }, object : Consumer<Throwable> {
                    override fun accept(t: Throwable?) {
                    }
                }
            )
    }

    private val recyclerViewItemDecoration by lazy {
        activity?.let {
            SpaceItemDecoration(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAppThemeChange(themeEvent: ThemeEvent) {
        homeListAdapter?.notifyDataSetChanged()
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
