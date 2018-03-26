package com.tompee.convoy.interactor.auth

import android.content.Intent
import com.facebook.login.widget.LoginButton
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Singleton

@Singleton
interface AuthInteractor {

    fun getUser(): Single<String>

    fun signUp(email: String, password: String): Single<String>

    fun login(email: String, password: String): Single<String>

    fun configureFacebookLogin(loginButton: LoginButton): Single<String>

    fun startGoogleLogin(): Observable<Intent>

    fun signInGoogle(data: Intent): Single<String>

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent)
}