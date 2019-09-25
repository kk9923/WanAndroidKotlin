package com.kx.kotlin.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kx.kotlin.R
import com.kx.kotlin.bean.UserScoreBean

class ScoreAdapter : BaseQuickAdapter<UserScoreBean,BaseViewHolder>(R.layout.item_score_list){

    override fun convert(helper: BaseViewHolder?, item: UserScoreBean?) {
        helper ?: return
        item ?: return
        helper.setText(R.id.tv_reason, item.reason)
            .setText(R.id.tv_desc, item.desc)
            .setText(R.id.tv_score, "+${item.coinCount}")
    }

}