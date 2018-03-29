package com.tompee.convoy.feature.login.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.tompee.convoy.feature.login.login.LoginFragment

class LoginPagerAdapter(fragmentManager: FragmentManager,
                        private val fragmentList: List<LoginFragment>) :
        FragmentStatePagerAdapter(fragmentManager) {

    override fun getCount(): Int = fragmentList.size

    override fun getItem(position: Int): Fragment = fragmentList[position]
}