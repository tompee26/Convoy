package com.tompee.convoy.interactor.auth

import com.tompee.convoy.Constants.AUTH_URL
import com.tompee.convoy.interactor.auth.AccountDoesNotExistException
import com.tompee.convoy.interactor.auth.AuthException
import com.tompee.convoy.interactor.auth.AuthInteractor
import com.tompee.convoy.interactor.auth.InvalidCredentialsException
import io.realm.ErrorCode
import io.realm.ObjectServerError
import io.realm.SyncCredentials
import io.realm.SyncUser

class UserPasswordAuth {

    fun loginAsync(email: String, password: String, listener: AuthInteractor.OnLoginFinishedListener) {
        SyncUser.loginAsync(SyncCredentials.usernamePassword(email,
                password, false), AUTH_URL,
                object : SyncUser.Callback<SyncUser> {
                    override fun onSuccess(result: SyncUser?) {
                        listener.onSuccess()
                    }

                    override fun onError(error: ObjectServerError) {
                        when (error.errorCode) {
                            ErrorCode.UNKNOWN_ACCOUNT -> listener.onError(AccountDoesNotExistException())
                            ErrorCode.INVALID_CREDENTIALS -> listener.onError(InvalidCredentialsException())
                            else -> listener.onError(AuthException())
                        }
                    }
                })
    }

    fun registerAsync(email: String, password: String, listener: AuthInteractor.OnLoginFinishedListener) {
        SyncUser.loginAsync(SyncCredentials.usernamePassword(email,
                password, true), AUTH_URL,
                object : SyncUser.Callback<SyncUser> {
                    override fun onSuccess(user: SyncUser) {
                        listener.onSuccess()
                    }

                    override fun onError(error: ObjectServerError) {
                        when (error.errorCode) {
                            ErrorCode.UNKNOWN_ACCOUNT -> listener.onError(AccountDoesNotExistException())
                            else -> listener.onError(AuthException())
                        }
                    }
                })
    }
}