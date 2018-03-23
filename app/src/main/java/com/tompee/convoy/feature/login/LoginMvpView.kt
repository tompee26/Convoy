package com.tompee.convoy.feature.login

import com.tompee.convoy.base.MvpView

interface LoginMvpView : MvpView {
    fun showProgressDialog()
    fun showEmailEmptyError()
    fun showEmailInvalidError()
    fun showPasswordEmptyError()
    fun showPasswordTooShortError()
}
