package com.tompee.convoy.feature.login.page

import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.tompee.convoy.base.BaseView
import io.reactivex.Observable
import io.reactivex.Single

interface LoginPageView : BaseView {
    fun getEmail(): Observable<String>
    fun getPassword(): Observable<String>
    fun getViewType(): Int
    fun command(): Observable<Any>
    fun loginWithFacebook(): Single<AccessToken>
    fun loginWithGoogle(): Observable<GoogleSignInResult>

    fun showEmailEmptyError()
    fun showEmailInvalidError()
    fun clearEmailError()
    fun showPasswordEmptyError()
    fun showPasswordTooShortError()
    fun clearPasswordError()

    fun showProgressDialog()
    fun dismissProgressDialog()
    fun showSignupSuccessMessage()
    fun showError(message: String)
}
