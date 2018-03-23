package com.tompee.convoy.interactor.auth

import com.tompee.convoy.interactor.auth.model.User
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Singleton

@Singleton
interface AuthInteractor {

    interface OnLoginFinishedListener {
        fun onSuccess()

        fun onError(error: AuthException)
    }

    fun getUser(): Completable

    fun signUp(email: String, password: String): Single<User>
}