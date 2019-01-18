package io.github.christianmz.whatsapp.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class PagerAdapter(fm: FragmentManager): FragmentPagerAdapter(fm) {

    private val listFragment = ArrayList<Fragment>()

    override fun getItem(position: Int)= listFragment[position]

    override fun getCount(): Int = listFragment.size

    fun addFragment(fragment: Fragment) = listFragment.add(fragment)
}