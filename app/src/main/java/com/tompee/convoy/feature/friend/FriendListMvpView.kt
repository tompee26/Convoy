package com.tompee.convoy.feature.friend

import com.tompee.convoy.base.MvpView
import com.tompee.convoy.feature.friend.adapter.SectionAdapter

interface FriendListMvpView : MvpView {
    fun getEmail(): String
    fun setList(sectionedAdapter: SectionAdapter)
}