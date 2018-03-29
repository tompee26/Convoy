package com.tompee.convoy.feature.login.progress

import com.tompee.convoy.R
import com.tompee.convoy.base.BaseFragment

class ProgressFragment : BaseFragment() {

    companion object {
        fun newInstance(): ProgressFragment {
            return ProgressFragment()
        }
    }

    override fun layoutId(): Int = R.layout.fragment_check_user

    override fun setupComponent() {
    }

}