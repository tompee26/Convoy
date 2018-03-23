package com.tompee.convoy.interactor.auth

import android.content.Intent
import com.facebook.login.widget.LoginButton
import com.google.android.gms.common.SignInButton
import javax.inject.Singleton

@Singleton
interface AuthInteractor {

    interface OnLoginFinishedListener {
        fun onSuccess()

        fun onError(error: AuthException)
    }

    fun login(email: String, password: String, listener: OnLoginFinishedListener)

    fun configureFacebookLogin(loginButton: LoginButton, listener: AuthInteractor.OnLoginFinishedListener)

    fun configureGoogleLogin(signInButton: SignInButton, listener: AuthInteractor.OnLoginFinishedListener)

    fun register(email: String, password: String, listener: OnLoginFinishedListener)

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent)

}