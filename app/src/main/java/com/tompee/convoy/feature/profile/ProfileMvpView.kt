package com.tompee.convoy.feature.profile

import com.tompee.convoy.base.MvpView

interface ProfileMvpView : MvpView {
    fun moveToNextActivity(email: String)
    fun showFirstNameError()
    fun showLastNameError()
    fun showDisplayNameError()
    fun setEmail(email: String)
}