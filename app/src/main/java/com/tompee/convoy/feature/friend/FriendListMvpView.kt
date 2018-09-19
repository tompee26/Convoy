package com.tompee.convoy.feature.friend

import com.tompee.convoy.base.BaseView
import com.tompee.convoy.feature.friend.adapter.SectionAdapter

interface FriendListMvpView : BaseView {
    fun getEmail(): String
    fun setList(sectionedAdapter: SectionAdapter)
}