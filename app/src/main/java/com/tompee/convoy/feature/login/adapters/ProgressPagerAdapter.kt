package com.tompee.convoy.feature.login.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.tompee.convoy.feature.login.progress.ProgressFragment

class ProgressPagerAdapter(fragmentManager: FragmentManager,
                           private val fragmentList: List<ProgressFragment>) :
        FragmentStatePagerAdapter(fragmentManager) {

    override fun getCount(): Int = fragmentList.size

    override fun getItem(position: Int): Fragment = fragmentList[position]
}