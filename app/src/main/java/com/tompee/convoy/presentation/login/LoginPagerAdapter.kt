package com.tompee.convoy.presentation.login

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class LoginPagerAdapter(
    fragmentManager: FragmentManager,
    private val loginFragment: LoginPageFragment,
    private val registerFragment: LoginPageFragment
) :
    FragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(position: Int) = when (position) {
        0 -> loginFragment
        else -> registerFragment
    }

    override fun getCount() = 2
}