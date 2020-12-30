package com.kx.kotlin.adapter

import android.content.Context
import android.text.Html
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kx.kotlin.R
import com.kx.kotlin.bean.KnowledgeTreeBody

class KnowledgeTreeAdapter(private val context: Context?) :
    BaseQuickAdapter<KnowledgeTreeBody, BaseViewHolder>(
    R.layout.item_knowledge_tree_list) {

    override fun convert(helper: BaseViewHolder, item: KnowledgeTreeBody?) {
        helper?.setText(R.id.title_first, item?.name)
        item?.children.let {
            helper?.setText(R.id.title_second,
                    it?.joinToString("    ", transform = { child ->
                        Html.fromHtml(child.name)
                    })
            )

        }
    }

}