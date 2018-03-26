package com.tompee.convoy.feature.login.fragment

import android.content.Intent
import com.tompee.convoy.base.MvpView

interface LoginFragmentMvpView : MvpView {
    fun showProgressDialog()
    fun hideProgressDialog()
    fun showEmailEmptyError()
    fun showEmailInvalidError()
    fun showPasswordEmptyError()
    fun showPasswordTooShortError()
    fun showGenericError(message: String)
    fun showRegistrationSuccessMessage()
    fun moveToNextActivity(email: String)
    fun startSignInWithIntent(intent: Intent)
}
