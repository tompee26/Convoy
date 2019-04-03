package com.tompee.convoy.domain.interactor

import com.tompee.convoy.domain.authenticator.Authenticator
import com.tompee.convoy.domain.entities.Account
import com.tompee.convoy.domain.repo.AccountRepository
import com.tompee.convoy.domain.repo.UserRepository
import io.reactivex.Single

class SplashInteractor(
    private val authenticator: Authenticator,
    private val userRepo: UserRepository,
    private val accountRepo: AccountRepository
) {

    fun getLoggedInEmail(): Single<String> {
        return authenticator.getCurrentUserEmail()
    }

    fun getAccount(email: String): Single<Account> = userRepo.getUser(email)
        .map { Account(it.email, true, it.firstName, it.lastName, it.displayName, it.image) }
        .flatMap {
            accountRepo.saveAccount(it)
                .andThen(Single.just(it))
        }
}