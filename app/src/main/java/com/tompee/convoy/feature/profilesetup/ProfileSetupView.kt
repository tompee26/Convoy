package com.tompee.convoy.feature.profilesetup

import com.tompee.convoy.base.BaseView
import io.reactivex.Observable

interface ProfileSetupView : BaseView {
    fun getFirstName(): Observable<String>
    fun getLastName(): Observable<String>
    fun getDisplayName(): Observable<String>
    fun getImageUrl(): Observable<String>
    fun saveRequest(): Observable<Any>

    fun showEmptyFirstNameError()
    fun showEmptyLastNameError()
    fun showEmptyDisplayNameError()

    fun showProgress()
    fun dismissProgress()
    fun showError(message: String)
}