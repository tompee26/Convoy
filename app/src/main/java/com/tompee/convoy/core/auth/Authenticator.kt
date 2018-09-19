package com.tompee.convoy.core.auth

import io.reactivex.Completable
import io.reactivex.Single

interface Authenticator {
    fun getCurrentUserEmail(): Single<String>

    fun signup(email: String, password: String): Completable

    fun login(email: String, password: String): Single<String>

    fun logout(): Completable
}