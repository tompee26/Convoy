package com.tompee.convoy.domain.interactor

import com.tompee.convoy.domain.authenticator.Authenticator
import com.tompee.convoy.domain.entities.Account
import com.tompee.convoy.domain.entities.User
import com.tompee.convoy.domain.repo.AccountRepository
import com.tompee.convoy.domain.repo.UserRepository
import io.reactivex.Completable

class ProfileInteractor(
    private val authenticator: Authenticator,
    private val accountRepository: AccountRepository,
    private val userRepository: UserRepository
) {

    fun saveAccount(firstName: String, lastName: String, displayName: String, imageUrl: String): Completable {
        return authenticator.getCurrentUserEmail()
            .map { User(it, firstName, lastName, displayName, imageUrl) }
            .flatMapCompletable {
                userRepository.saveUser(it)
                    .andThen(
                        accountRepository.saveAccount(
                            Account(
                                it.email,
                                true,
                                it.firstName,
                                it.lastName,
                                it.displayName,
                                imageUrl
                            )
                        )
                    )
            }
    }
}