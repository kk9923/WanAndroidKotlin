package com.kx.kotlin.ui

import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import com.chad.library.adapter.base.BaseQuickAdapter
import com.kx.kotlin.R
import com.kx.kotlin.adapter.MyCollectAdapter
import com.kx.kotlin.base.BaseActivity
import com.kx.kotlin.base.BaseObserver
import com.kx.kotlin.bean.MyCollectResponseBody
import com.kx.kotlin.ext.showToast
import com.kx.kotlin.http.RetrofitHelper
import com.kx.kotlin.util.RxUtil
import kotlinx.android.synthetic.main.activity_my_collect.*
import kotlinx.android.synthetic.main.toolbar.*

class MyCollectActivity : BaseActivity() {

    private var pageNum :Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_collect)

        toolbar.run{
            title = getString(R.string.nav_my_collect)
            setSupportActionBar(this)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        recyclerView.run {
            adapter = mMyCollectAdapter
            addItemDecoration(recyclerViewItemDecoration)
        }
        mMyCollectAdapter.run {
            onItemClickListener = this@MyCollectActivity.onItemClickListener
//            setOnLoadMoreListener({
//                getCollectList()
//            },recyclerView)
            onItemChildClickListener = this@MyCollectActivity.onItemChildClickListener
        }
        refreshLayout.run {
            refreshLayout.setEnableOverScrollDrag(false);//禁止越界拖动（1.0.4以上版本）
            refreshLayout.setEnableOverScrollBounce(false);//关闭越界回弹功能
            refreshLayout.setEnableLoadMoreWhenContentNotFull(false);//关闭越界回弹功能
            setOnRefreshListener {
                pageNum = 0
                getCollectList()
            }
            setOnLoadMoreListener {
              //  recyclerView.stopScroll()
                getCollectList()
            }
        }
        refreshLayout.autoRefresh()
    }

    private fun getCollectList() {
        addDisposable(
            RetrofitHelper.service.getCollectList(pageNum).compose(RxUtil.ioMain())
                .subscribeWith(object : BaseObserver<MyCollectResponseBody>(){
                    override fun onSuccess(result: MyCollectResponseBody) {
                        if (pageNum == 0){
                            mMyCollectAdapter.setNewData(result.datas)
                            if (result.datas.size < result.size){
                                refreshLayout.finishRefreshWithNoMoreData()
                            }else{
                                refreshLayout.finishRefresh(true)
                            }
                        }else{
                            mMyCollectAdapter.addData(result.datas)
                            if (result.datas.size < result.size){
                                refreshLayout.finishLoadMoreWithNoMoreData()
                            }else{
                                refreshLayout.finishLoadMore()
                            }
                        }
                        pageNum ++
                    }
                    override fun onError(errorMsg: String) {
                        refreshLayout.finishRefresh(false)
                        showToast(errorMsg)
                    }
                })
        )
    }

    private val onItemClickListener  = BaseQuickAdapter.OnItemClickListener { _, _, position ->
            val itemData = mMyCollectAdapter.data[position]
             ArticlesDetailActivity.start(mActivity,itemData.id,itemData.title,itemData.link)
    }

    private val onItemChildClickListener =
        BaseQuickAdapter.OnItemChildClickListener { _, view, position ->
            if (mMyCollectAdapter.data.size != 0) {
                val data = mMyCollectAdapter.data[position]
                when (view.id) {
                    R.id.iv_like -> {
                        removeCollectArticle(data.id, data.originId,position)
                    }
                }
            }
        }

    /**
     *   RecyclerView的notifyItemRemoved(position) 和 SmartRefreshLayout 结合起来使用有bug
     *   在 notifyItemRemoved 动画未结束前 , SmartRefreshLayout 的 footer 就已经移动到上面了
     */
    private fun removeCollectArticle(id: Int, originId: Int, position: Int) {
        addDisposable(RetrofitHelper.service.removeCollectArticle(id,originId)
            .compose(RxUtil.ioMain())
            .subscribeWith(object : BaseObserver<Any>() {
                override fun onSuccess() {
                    showToast(getString(R.string.cancel_collect_success))
                    mMyCollectAdapter.data.removeAt(position)
                    mMyCollectAdapter.notifyDataSetChanged()
                  //  mMyCollectAdapter.notifyItemChanged(position)
                }
                override fun onError(errorMsg: String) {
                    showToast(errorMsg)
                }
            })
        )
    }

    private val mMyCollectAdapter : MyCollectAdapter by lazy {
        MyCollectAdapter(mActivity)
    }

    private val recyclerViewItemDecoration by lazy {
//        SpaceItemDecoration(mActivity)
        androidx.recyclerview.widget.DividerItemDecoration(
            mActivity,
            androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
        )
    }


}
