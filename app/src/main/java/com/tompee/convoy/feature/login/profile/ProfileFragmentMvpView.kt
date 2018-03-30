package com.tompee.convoy.feature.login.profile

import android.net.Uri
import com.tompee.convoy.base.MvpView
import com.tompee.convoy.interactor.model.User
import io.reactivex.Observable

interface ProfileFragmentMvpView : MvpView {
    fun getFirstName(): Observable<String>
    fun getLastName(): Observable<String>
    fun getDisplayName(): Observable<String>
    fun getEmail(): String
    fun saveRequest(): Observable<Any>
    fun getImage(): Observable<Uri>

    fun showFirstNameError()
    fun showLastNameError()
    fun showDisplayNameError()
    fun setSaveButtonState(state: Boolean)
    fun saveSuccessful(user: User)
}