package com.kx.kotlin.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import android.text.Html
import com.kx.kotlin.bean.ProjectTreeBean
import com.kx.kotlin.fragment.ProjectListFragment

class ProjectPagerAdapter(private val list: MutableList<ProjectTreeBean>, fm: androidx.fragment.app.FragmentManager?) : androidx.fragment.app.FragmentStatePagerAdapter(fm) {

    private val fragments = mutableListOf<androidx.fragment.app.Fragment>()

    init {
        fragments.clear()
        list.forEach {
            fragments.add(ProjectListFragment.getInstance(it.id))
        }
    }

    override fun getItem(position: Int): androidx.fragment.app.Fragment = fragments[position]

    override fun getCount(): Int = list.size

    override fun getPageTitle(position: Int): CharSequence? = Html.fromHtml(list[position].name)

    override fun getItemPosition(`object`: Any): Int = androidx.viewpager.widget.PagerAdapter.POSITION_NONE
}