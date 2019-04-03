package com.tompee.convoy.domain.interactor

import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.tompee.convoy.domain.authenticator.Authenticator
import com.tompee.convoy.domain.entities.Account
import com.tompee.convoy.domain.repo.AccountRepository
import com.tompee.convoy.domain.repo.UserRepository
import io.reactivex.Completable
import io.reactivex.Single

class LoginInteractor(
    private val authenticator: Authenticator,
    private val userRepo: UserRepository,
    private val accountRepo: AccountRepository
) {

    fun register(email: String, password: String): Completable {
        return authenticator.register(email, password)
    }

    fun login(email: String, password: String): Single<String> {
        return authenticator.login(email, password)
    }

    fun login(token: AccessToken): Single<String> {
        return authenticator.login(token)
    }

    fun login(result: GoogleSignInResult): Single<String> {
        return authenticator.login(result)
    }

    fun getAccountInfo(email: String): Single<Account> {
        return userRepo.getUser(email)
            .map { Account(it.email, true, it.firstName, it.lastName, it.displayName, it.image) }
            .flatMap {
                accountRepo.saveAccount(it)
                    .andThen(Single.just(it))
            }
    }
}