package com.kx.kotlin.fragment

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.kx.kotlin.R
import com.kx.kotlin.adapter.KnowledgeAdapter
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

class KnowledgeFragment : BaseFragment() {

    private var cid: Int = 0
    private var pageNum: Int = 0

    companion object {
        fun getInstance(cid: Int): KnowledgeFragment {
            val fragment = KnowledgeFragment()
            val args = Bundle()
            args.putInt(Constant.CONTENT_CID_KEY, cid)
            fragment.arguments = args
            return fragment
        }
    }


    override fun attachLayoutRes(): Int = R.layout.fragment_refresh_layout


    override fun initView() {
        cid = arguments?.getInt(Constant.CONTENT_CID_KEY) ?: 0
        refreshLayout.run {
            refreshLayout.setEnableOverScrollDrag(false);//禁止越界拖动（1.0.4以上版本）
            refreshLayout.setEnableOverScrollBounce(false);//关闭越界回弹功能
            refreshLayout.setEnableLoadMoreWhenContentNotFull(false);//关闭越界回弹功能
            setOnRefreshListener {
                pageNum = 0
                getData()
            }
            setOnLoadMoreListener {
                getData()
            }
        }
        recyclerView.run {
            layoutManager = linearLayoutManager
            adapter = knowledgeAdapter
            itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
            addItemDecoration(
                androidx.recyclerview.widget.DividerItemDecoration(
                    activity,
                    androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
                )
            )
        }
        knowledgeAdapter.run {
               onItemClickListener = this@KnowledgeFragment.onItemClickListener
               onItemChildClickListener = this@KnowledgeFragment.onItemChildClickListener
        }
        refreshLayout.autoRefresh()
    }

    private fun getData() {
        addDisposable(RetrofitHelper.service.getKnowledgeList(pageNum, cid).compose(RxUtil.ioMain())
                .subscribeWith(object : BaseObserver<ArticleResponseBody>() {
                    override fun onSuccess(result: ArticleResponseBody) {
                        super.onSuccess(result)
                        if (pageNum == 0 ){
                            knowledgeAdapter.setNewData(result.datas)
                            if (result.datas.size <  result.size){
                                refreshLayout.finishRefreshWithNoMoreData()
                            }else{
                                refreshLayout.finishRefresh()
                            }
                        }else{
                            knowledgeAdapter.addData(result.datas)
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
            val itemData = knowledgeAdapter.data[position]
            ArticlesDetailActivity.start(activity, itemData.id, itemData.title, itemData.link)
        }
    }

    private val onItemChildClickListener =
        BaseQuickAdapter.OnItemChildClickListener { _, view, position ->
            if (knowledgeAdapter.data.size != 0) {
                val data = knowledgeAdapter.data[position]
                when (view.id) {
                    R.id.iv_like -> {
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
                        knowledgeAdapter.data[position].collect = true
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
                        knowledgeAdapter.data[position].collect = false
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
        knowledgeAdapter.notifyItemChanged(position )
    }

    private val linearLayoutManager: androidx.recyclerview.widget.LinearLayoutManager by lazy {
        androidx.recyclerview.widget.LinearLayoutManager(activity)
    }

    private val knowledgeAdapter: KnowledgeAdapter by lazy {
        KnowledgeAdapter(activity)
    }


    fun scrollToTop() {
        recyclerView.run {
            if (linearLayoutManager.findFirstVisibleItemPosition() > 20) {
                scrollToPosition(0)
            } else {
                smoothScrollToPosition(0)
            }
        }
    }
}