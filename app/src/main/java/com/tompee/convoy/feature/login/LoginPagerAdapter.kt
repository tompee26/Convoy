package com.tompee.convoy.feature.login

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import javax.inject.Inject

class LoginPagerAdapter @Inject constructor(fragmentManager: FragmentManager,
                                            private val fragmentList: List<LoginFragment>) :
        FragmentStatePagerAdapter(fragmentManager) {

    override fun getCount(): Int = fragmentList.size

    override fun getItem(position: Int): Fragment = fragmentList[position]
}