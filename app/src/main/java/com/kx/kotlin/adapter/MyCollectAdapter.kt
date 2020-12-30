package com.kx.kotlin.adapter

import android.content.Context
import android.text.Html
import android.view.View
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kx.kotlin.R
import com.kx.kotlin.bean.CollectionArticle
import com.kx.kotlin.util.ImageLoader

class MyCollectAdapter(private val context: Context?)
    : BaseQuickAdapter<CollectionArticle, BaseViewHolder>(R.layout.item_collect_list) {
    override fun convert(helper: BaseViewHolder, item: CollectionArticle?) {

        helper ?: return
        item ?: return
        helper.setText(R.id.tv_article_title, Html.fromHtml(item.title))
                .setText(R.id.tv_article_author, item.author)
                .setText(R.id.tv_article_date, item.niceDate)
                .setImageResource(R.id.iv_like, R.drawable.ic_like)
                .addOnClickListener(R.id.iv_like)

        helper.setText(R.id.tv_article_chapterName, item.chapterName)
        if (item.envelopePic.isNotEmpty()) {
            helper.getView<ImageView>(R.id.iv_article_thumbnail)
                    .visibility = View.VISIBLE
            context?.let {
                ImageLoader.load(it, item.envelopePic, helper.getView(R.id.iv_article_thumbnail))
            }
        } else {
            helper.getView<ImageView>(R.id.iv_article_thumbnail)
                    .visibility = View.GONE
        }
    }
}