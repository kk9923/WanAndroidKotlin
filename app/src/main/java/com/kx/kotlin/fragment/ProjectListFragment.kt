package com.kx.kotlin.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.kx.kotlin.R
import com.kx.kotlin.adapter.ProjectAdapter
import com.kx.kotlin.base.BaseFragment
import com.kx.kotlin.base.BaseObserver
import com.kx.kotlin.bean.ArticleResponseBody
import com.kx.kotlin.constant.Constant
import com.kx.kotlin.ext.showToast
import com.kx.kotlin.http.RetrofitHelper
import com.kx.kotlin.ui.ArticlesDetailActivity
import com.kx.kotlin.ui.LoginActivity
import com.kx.kotlin.util.RxUtil
import kotlinx.android.synthetic.main.fragment_refresh_layout.*

class ProjectListFragment : BaseFragment() {

    private var cid: Int = 0
    private var pageNum: Int = 1

    override fun attachLayoutRes(): Int = R.layout.fragment_refresh_layout

    companion object {
        fun getInstance(cid: Int): ProjectListFragment {
            val fragment = ProjectListFragment()
            val args = Bundle()
            args.putInt(Constant.CONTENT_CID_KEY, cid)
            fragment.arguments = args
            return fragment
        }
    }

    override fun initView() {
        cid = arguments?.getInt(Constant.CONTENT_CID_KEY) ?: 0
        refreshLayout.run {
            refreshLayout.setEnableOverScrollDrag(false);//禁止越界拖动（1.0.4以上版本）
            refreshLayout.setEnableOverScrollBounce(false);//关闭越界回弹功能
            refreshLayout.setEnableLoadMoreWhenContentNotFull(false);//关闭越界回弹功能
            setOnRefreshListener {
                pageNum = 1
                getData()
            }
            setOnLoadMoreListener {
                getData()
            }
        }
        recyclerView.run {
            layoutManager = linearLayoutManager
            adapter = projectAdapter
            itemAnimator = DefaultItemAnimator()
            addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        }
        projectAdapter.run {
            onItemClickListener = this@ProjectListFragment.onItemClickListener
            onItemChildClickListener = this@ProjectListFragment.onItemChildClickListener
        }
        refreshLayout.autoRefresh()
    }

    private fun getData() {
        addDisposable(
            RetrofitHelper.service.getProjectList(pageNum, cid).compose(RxUtil.ioMain())
                .subscribeWith(object : BaseObserver<ArticleResponseBody>() {
                    override fun onSuccess(result: ArticleResponseBody) {
                        super.onSuccess(result)
                        if (pageNum == 1 ){
                            projectAdapter.setNewData(result.datas)
                            if (result.datas.size <  result.size){
                                refreshLayout.finishRefreshWithNoMoreData()
                            }else{
                                refreshLayout.finishRefresh()
                            }
                        }else{
                            projectAdapter.addData(result.datas)
                            if (result.datas.size <  result.size){
                                refreshLayout.finishLoadMoreWithNoMoreData()
                            }else{
                                refreshLayout.finishLoadMore()
                            }
                        }
                        pageNum ++
                    }
                    override fun onError(errorMsg: String) {
                        showToast(errorMsg)
                    }
                }
                )
        )
    }
    private val onItemClickListener = object : BaseQuickAdapter.OnItemClickListener {
        override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
            val itemData = projectAdapter.data[position]
            ArticlesDetailActivity.start(activity, itemData.id, itemData.title, itemData.link)
        }
    }

    private val onItemChildClickListener =
        BaseQuickAdapter.OnItemChildClickListener { _, view, position ->
            if (projectAdapter.data.size != 0) {
                val data = projectAdapter.data[position]
                when (view.id) {
                    R.id.item_project_list_like_iv -> {
                        if (isLogin) {
                            if (data.collect) {
                                cancelCollectArticle(data.id, position)
                            } else {
                                addCollectArticle(data.id, position)
                            }
                        }else{
                            showToast(resources.getString(R.string.login_tint))
                            Intent(activity, LoginActivity::class.java).run {
                                startActivity(this)
                            }
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
                        projectAdapter.data[position].collect = true
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
                        projectAdapter.data[position].collect = false
                        notifyItemChanged(position)
                    }

                    override fun onError(errorMsg: String) {
                        showToast(errorMsg)
                    }
                })
        )
    }

    private fun notifyItemChanged(position: Int) {
        projectAdapter.notifyItemChanged(position )
    }

    private val projectAdapter: ProjectAdapter by lazy {
        ProjectAdapter(activity)
    }

    private val linearLayoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(activity)
    }

    fun scrollToTop() {
        recyclerView.run {
            if (linearLayoutManager.findFirstVisibleItemPosition() > 40) {
                scrollToPosition(0)
            } else {
                smoothScrollToPosition(0)
            }
        }
    }

}