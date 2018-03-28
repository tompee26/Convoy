package com.tompee.convoy.feature.login

import com.tompee.convoy.base.MvpView

interface LoginActivityMvpView : MvpView {
    fun moveToNextActivity(email: String)
}