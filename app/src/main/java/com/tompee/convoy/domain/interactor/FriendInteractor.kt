package com.tompee.convoy.domain.interactor

import com.tompee.convoy.domain.entities.User
import com.tompee.convoy.domain.repo.AccountRepository
import com.tompee.convoy.domain.repo.UserRepository
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

class FriendInteractor(
    private val accountRepository: AccountRepository,
    private val userRepository: UserRepository
) {

    fun searchUser(text: String): Single<List<User>> {
        return accountRepository.getAccount()
            .flatMap { userRepository.searchUser(text, listOf(it.email)) }
    }

    fun findUser(email: String): Single<User> {
        return userRepository.getUser(email)
    }

    fun findInFriends(email: String): Completable {
        return accountRepository.getAccount()
            .flatMapCompletable { userRepository.searchInFriends(it.email, email) }
    }

    fun findInSentRequest(email: String): Completable {
        return accountRepository.getAccount()
            .flatMapCompletable { userRepository.searchInSentRequest(it.email, email) }
    }

    fun findInReceivedRequest(email: String): Completable {
        return accountRepository.getAccount()
            .flatMapCompletable { userRepository.searchInReceivedRequest(it.email, email) }
    }

    fun sendFriendRequest(email: String): Completable {
        return accountRepository.getAccount()
            .flatMapCompletable { userRepository.sendFriendRequest(it.email, email) }
    }

    fun acceptFriendRequest(email: String): Completable {
        return accountRepository.getAccount()
            .flatMapCompletable { userRepository.acceptFriendRequest(it.email, email) }
    }

    fun rejectFriendRequest(email: String): Completable {
        return accountRepository.getAccount()
            .flatMapCompletable { userRepository.rejectFriendRequest(it.email, email) }
    }

    fun getFriendsList(): Flowable<List<User>> {
        return accountRepository.getAccount()
            .flatMapPublisher { userRepository.getFriendsList(it.email) }
    }

    fun getFriendRequests(): Flowable<List<User>> {
        return accountRepository.getAccount()
            .flatMapPublisher { userRepository.getFriendRequests(it.email) }
    }

    fun getIncomingFriendRequests(): Flowable<List<User>> {
        return accountRepository.getAccount()
            .flatMapPublisher { userRepository.getIncomingFriendRequests(it.email) }
    }
}