package com.tompee.convoy.interactor

import com.tompee.convoy.base.BaseInteractor
import com.tompee.convoy.core.auth.Authenticator
import com.tompee.convoy.core.model.MutableAccount
import com.tompee.convoy.core.repo.UserRepository
import com.tompee.convoy.model.Account
import io.reactivex.Single

class SplashInteractor(private val authenticator: Authenticator,
                       private val userRepository: UserRepository,
                       private val loggedInUser: MutableAccount) : BaseInteractor {

    fun getCurrentUser(): Single<String> {
        return authenticator.getCurrentUserEmail()
                .doOnSuccess {
                    loggedInUser.apply {
                        email = it
                        isAuthenticated = true
                    }
                }
    }

    fun getUserInfo(email: String): Single<Account> {
        return userRepository.getUser(email)
                .map { Account(it.email, loggedInUser.email == it.email, it.firstName, it.lastName, it.displayName, it.image) }
                .doOnSuccess {
                    loggedInUser.apply {
                        firstName = it.firstName
                        lastName = it.lastName
                        displayName = it.displayName
                        imageUrl = it.imageUrl
                    }
                }
    }
}