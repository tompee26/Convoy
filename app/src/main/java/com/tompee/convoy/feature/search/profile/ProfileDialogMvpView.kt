package com.tompee.convoy.feature.search.profile

import android.graphics.Bitmap
import com.tompee.convoy.base.BaseView
import com.tompee.convoy.interactor.model.User
import io.reactivex.Observable

interface ProfileDialogMvpView : BaseView {
    fun getUserEmail(): String
    fun getTargetEmail(): String
    fun addFriendRequest(): Observable<Pair<String, String>>
    fun acceptRequest(): Observable<Pair<String, String>>
    fun rejectRequest(): Observable<Pair<String, String>>

    fun setProfile(user: User, bitmap: Bitmap)
    fun showAddFriend()
    fun showProgress()
    fun showCustomMessage(message: String)
    fun showAcceptRequest()
}