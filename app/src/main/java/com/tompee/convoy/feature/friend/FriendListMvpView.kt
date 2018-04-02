package com.tompee.convoy.feature.friend

import com.tompee.convoy.base.MvpView

interface FriendListMvpView : MvpView {
    fun getEmail(): String
}