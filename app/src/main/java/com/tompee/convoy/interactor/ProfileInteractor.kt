package com.tompee.convoy.interactor

import android.net.Uri
import com.tompee.convoy.base.BaseInteractor
import com.tompee.convoy.core.model.MutableAccount
import com.tompee.convoy.core.repo.ProfileImageRepo
import com.tompee.convoy.core.repo.UserEntity
import com.tompee.convoy.core.repo.UserRepository
import com.tompee.convoy.model.Account
import io.reactivex.Completable
import io.reactivex.Single

class ProfileInteractor(private val userRepository: UserRepository,
                        private val profileImageRepo: ProfileImageRepo,
                        private val loggedInUser: MutableAccount) : BaseInteractor {

    fun getEmail(): Single<String> = Single.just(loggedInUser.email)

    fun saveAccount(account: Account): Completable =
            if (account.imageUrl.isNotEmpty()) {
                profileImageRepo.uploadProfileImage(account.email, Uri.parse(account.imageUrl))
                        .doOnSuccess {
                            loggedInUser.apply {
                                firstName = account.firstName
                                lastName = account.lastName
                                displayName = account.displayName
                                imageUrl = it
                            }
                        }
                        .flatMapCompletable {
                            userRepository.saveUser(UserEntity(account.email,
                                    account.firstName, account.lastName, account.displayName, it))
                        }
            } else {
                userRepository.saveUser(UserEntity(account.email, account.firstName, account.lastName,
                        account.displayName, account.imageUrl))
                        .doOnComplete {
                            loggedInUser.apply {
                                firstName = account.firstName
                                lastName = account.lastName
                                displayName = account.displayName
                            }
                        }
            }
}