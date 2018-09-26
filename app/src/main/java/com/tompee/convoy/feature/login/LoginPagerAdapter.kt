package com.tompee.convoy.feature.login

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.tompee.convoy.feature.login.page.LoginPageFragment

class LoginPagerAdapter(fragmentManager: FragmentManager,
                        private val loginFragment: LoginPageFragment,
                        private val signupFragment: LoginPageFragment) :
        FragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(position: Int) = when (position) {
        0 -> loginFragment
        else -> signupFragment
    }

    override fun getCount() = 2
}