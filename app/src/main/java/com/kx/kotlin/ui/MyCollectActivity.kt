package com.kx.kotlin.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import com.chad.library.adapter.base.BaseQuickAdapter
import com.kx.kotlin.R
import com.kx.kotlin.adapter.MyCollectAdapter
import com.kx.kotlin.base.BaseActivity
import com.kx.kotlin.base.BaseObserver
import com.kx.kotlin.bean.MyCollectResponseBody
import com.kx.kotlin.constant.Constant
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
            val intent = Intent(mActivity, ArticlesDetailActivity::class.java)
            intent.run {
                putExtra(Constant.CONTENT_URL_KEY, itemData.link)
                putExtra(Constant.CONTENT_TITLE_KEY, itemData.title)
                putExtra(Constant.CONTENT_ID_KEY, itemData.id)
                startActivity(this)
            }
        }

    private val mMyCollectAdapter : MyCollectAdapter by lazy {
        MyCollectAdapter(mActivity)
    }

    private val recyclerViewItemDecoration by lazy {
//        SpaceItemDecoration(mActivity)
        DividerItemDecoration(mActivity,DividerItemDecoration.VERTICAL)
    }


}
