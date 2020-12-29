package com.kx.kotlin.ui

import android.os.Bundle
import com.google.android.material.appbar.AppBarLayout
import com.kx.kotlin.R
import com.kx.kotlin.adapter.ScoreAdapter
import com.kx.kotlin.base.BaseActivity
import com.kx.kotlin.base.BaseObserver
import com.kx.kotlin.bean.ScoreResponseBody
import com.kx.kotlin.ext.showToast
import com.kx.kotlin.http.RetrofitHelper
import com.kx.kotlin.util.RxUtil
import com.kx.kotlin.widget.SpaceItemDecoration
import kotlinx.android.synthetic.main.activity_score.*
import kotlinx.android.synthetic.main.toolbar.*

class ScoreActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)

        toolbar.run {
            title = getString(R.string.score_detail)
            setSupportActionBar(this)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            setToolBarEnableScroll(false)
        }
        recyclerView.run {
            adapter = mScoreAdapter
            addItemDecoration(recyclerViewItemDecoration)
        }
    }

    private val recyclerViewItemDecoration by lazy {
        SpaceItemDecoration(this)
    }

    private val mScoreAdapter: ScoreAdapter by lazy {
        ScoreAdapter()
    }

    override fun onStart() {
        super.onStart()
        getUserScore()
    }

    private fun getUserScore() {
        addDisposable(
            RetrofitHelper.service.getUserScore(1).compose(RxUtil.ioMain())
                .subscribeWith(object : BaseObserver<ScoreResponseBody>() {
                    override fun onSuccess(result: ScoreResponseBody) {
                        var totalScore = 0
                        for (item in result.datas) {
                            totalScore += item.coinCount
                        }
                      //  tv_score.text = "$totalScore"
                        mScoreAdapter.setNewData(result.datas)
                    }

                    override fun onError(errorMsg: String) {
                        showToast(errorMsg)
                    }
                })
        )
    }
    /**
     * 设置TootBar的滚动模式
     */
    private fun setToolBarEnableScroll(enable: Boolean) {
        val layoutParams = toolbar.layoutParams
        if (layoutParams is AppBarLayout.LayoutParams)
            if (enable) {
                layoutParams.scrollFlags = 5
            } else {
                layoutParams.scrollFlags = 0
            }
    }
}
