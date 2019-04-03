package com.tompee.convoy.domain.authenticator

import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import io.reactivex.Completable
import io.reactivex.Single

interface Authenticator {
    fun getCurrentUserEmail(): Single<String>

    fun register(email: String, password: String): Completable

    fun login(email: String, password: String): Single<String>

    fun login(token: AccessToken): Single<String>

    fun login(result: GoogleSignInResult): Single<String>

    fun logout(): Completable
}