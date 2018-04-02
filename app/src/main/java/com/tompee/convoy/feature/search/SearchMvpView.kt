package com.tompee.convoy.feature.search

import com.tompee.convoy.base.MvpView
import com.tompee.convoy.feature.search.adapter.UserListAdapter
import io.reactivex.Observable

interface SearchMvpView : MvpView {
    fun getEmail(): String
    fun getSearchString(): Observable<String>

    fun showEmptyView()
    fun showUserList(adapter: UserListAdapter)
    fun showUserProfile(email: String)
}