package com.tompee.convoy.interactor.auth

import android.content.Intent
import com.facebook.login.widget.LoginButton
import com.tompee.convoy.interactor.model.User
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Singleton

@Singleton
interface AuthInteractor {

    fun getUser(): Completable

    fun signUp(email: String, password: String): Single<User>

    fun login(email: String, password: String): Single<User>

    fun configureFacebookLogin(loginButton: LoginButton): Single<User>

    fun startGoogleLogin(): Observable<Intent>

    fun signInGoogle(data: Intent): Single<User>

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent)
}