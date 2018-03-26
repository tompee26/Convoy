package com.tompee.convoy.feature.profile

import com.tompee.convoy.base.MvpView

interface ProfileMvpView : MvpView {
    fun moveToNextActivity()
    fun showFirstNameError()
    fun showLastNameError()
    fun showDisplayNameError()
}