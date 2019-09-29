package com.kx.kotlin.fragment

import android.support.v7.widget.LinearLayoutManager
import com.kx.kotlin.R
import com.kx.kotlin.adapter.KnowledgeTreeAdapter
import com.kx.kotlin.base.BaseFragment
import com.kx.kotlin.base.BaseObserver
import com.kx.kotlin.bean.KnowledgeTreeBody
import com.kx.kotlin.ext.showToast
import com.kx.kotlin.http.RetrofitHelper
import com.kx.kotlin.util.RxUtil
import kotlinx.android.synthetic.main.fragment_knowledge.*

class KnowledgeTreeFragment : BaseFragment() {

    companion object {
        fun getInstance(): KnowledgeTreeFragment = KnowledgeTreeFragment()
    }

    override fun attachLayoutRes(): Int = R.layout.fragment_knowledge

    override fun initView() {
        recyclerView.run {
            adapter = mKnowledgeTreeAdapter
            layoutManager = mLayoutManager
        }
        refreshLayout.run {
            setOnRefreshListener {
                getKnowledgeTree()
            }
        }
        mKnowledgeTreeAdapter.run {
            setOnItemClickListener { _, _, position ->
                val name = mKnowledgeTreeAdapter.data[position].name
                showToast(name)
            }
        }
        getKnowledgeTree()
    }




    private fun getKnowledgeTree(){
        addDisposable(RetrofitHelper.service.getKnowledgeTree().compose(RxUtil.ioMain())
            .subscribeWith(object : BaseObserver<List<KnowledgeTreeBody>>(){
                override fun onSuccess(result: List<KnowledgeTreeBody>) {
                    mKnowledgeTreeAdapter.setNewData(result)
                    refreshLayout.finishRefresh()
                }

                override fun onError(errorMsg: String) {
                    refreshLayout.finishRefresh(false)
                }
            })
        )
    }

    private val mKnowledgeTreeAdapter: KnowledgeTreeAdapter by lazy {
        KnowledgeTreeAdapter(activity)
    }

    private val mLayoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(activity)
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

}