package com.tompee.convoy.feature.map

import com.tompee.convoy.base.MvpView
import com.tompee.convoy.interactor.model.User

interface MapMvpView : MvpView {
    fun setupProfile(user: User)
}