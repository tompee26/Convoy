package com.tompee.convoy.feature.login.fragment

import com.tompee.convoy.base.MvpView

interface LoginFragmentMvpView : MvpView {
    fun showProgressDialog()
    fun hideProgressDialog()
    fun showEmailEmptyError()
    fun showEmailInvalidError()
    fun showPasswordEmptyError()
    fun showPasswordTooShortError()
    fun showRegistrationFailedError(message: String)
}
