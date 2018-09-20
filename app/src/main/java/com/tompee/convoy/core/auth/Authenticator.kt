package com.tompee.convoy.core.auth

import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import io.reactivex.Completable
import io.reactivex.Single

interface Authenticator {
    fun getCurrentUserEmail(): Single<String>

    fun signup(email: String, password: String): Completable

    fun login(email: String, password: String): Single<String>

    fun logout(): Completable

    fun loginWithFacebook(accessToken: AccessToken): Single<String>

    fun loginWithGoogle(result: GoogleSignInResult): Single<String>
}