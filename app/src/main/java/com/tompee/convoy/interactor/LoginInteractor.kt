package com.tompee.convoy.interactor

import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.tompee.convoy.base.BaseInteractor
import com.tompee.convoy.core.auth.Authenticator
import com.tompee.convoy.core.model.MutableAccount
import com.tompee.convoy.core.repo.UserRepository
import com.tompee.convoy.model.Account
import io.reactivex.Single

class LoginInteractor(private val authenticator: Authenticator,
                      private val userRepository: UserRepository,
                      private val loggedInUser: MutableAccount) : BaseInteractor {
    fun signup(email: String, pass: String) = authenticator.signup(email, pass)

    fun login(email: String, pass: String): Single<String> =
            authenticator.login(email, pass)
                    .doOnSuccess {
                        loggedInUser.apply {
                            this.email = it
                            isAuthenticated = true
                        }
                    }

    fun getUserInfo(email: String): Single<Account> {
        return userRepository.getUser(email)
                .map { Account(it.email, it.email == email, it.firstName, it.lastName, it.displayName, it.image) }
                .doOnSuccess {
                    loggedInUser.apply {
                        this.firstName = it.firstName
                        this.lastName = it.lastName
                        this.displayName = it.displayName
                        imageUrl = it.imageUrl
                    }
                }
    }

    fun loginWithFacebook(accessToken: AccessToken): Single<String> =
            authenticator.loginWithFacebook(accessToken)
                    .doOnSuccess {
                        loggedInUser.apply {
                            this.email = it
                            isAuthenticated = true
                        }
                    }

    fun loginWithGoogle(result: GoogleSignInResult): Single<String> =
            authenticator.loginWithGoogle(result)
                    .doOnSuccess {
                        loggedInUser.apply {
                            this.email = it
                            isAuthenticated = true
                        }
                    }
}