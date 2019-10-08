package com.kx.kotlin.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import android.text.Html
import com.kx.kotlin.bean.ProjectTreeBean
import com.kx.kotlin.fragment.ProjectListFragment

class ProjectPagerAdapter(private val list: MutableList<ProjectTreeBean>, fm: FragmentManager?) : FragmentStatePagerAdapter(fm) {

    private val fragments = mutableListOf<Fragment>()

    init {
        fragments.clear()
        list.forEach {
            fragments.add(ProjectListFragment.getInstance(it.id))
        }
    }

    override fun getItem(position: Int): Fragment = fragments[position]

    override fun getCount(): Int = list.size

    override fun getPageTitle(position: Int): CharSequence? = Html.fromHtml(list[position].name)

    override fun getItemPosition(`object`: Any): Int = PagerAdapter.POSITION_NONE
}