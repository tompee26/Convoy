package com.tompee.convoy.feature.login.fragment

import android.content.Intent
import com.facebook.login.widget.LoginButton
import com.tompee.convoy.base.MvpView
import io.reactivex.Observable

interface LoginFragmentMvpView : MvpView {
    fun getType(): Int
    fun signUpRequest(): Observable<Any>
    fun loginRequest(): Observable<Any>
    fun getEmail(): Observable<String>
    fun getPassword(): Observable<String>
    fun getFacebookLogin(): Observable<LoginButton>
    fun googleSignInRequest(): Observable<Any>
    fun facebookResult(): Observable<Triple<Int, Int, Intent>>
    fun googleResult(): Observable<Intent>

    fun showEmailEmptyError()
    fun showEmailInvalidError()
    fun showPasswordEmptyError()
    fun showPasswordTooShortError()
    fun showGenericError(message: String)

    fun showProgressDialog()
    fun hideProgressDialog()
    fun showRegistrationSuccessMessage()
    fun moveToNextActivity(email: String)
    fun startSignInWithIntent(intent: Intent)
    fun enableCommand(state: Boolean)
}
