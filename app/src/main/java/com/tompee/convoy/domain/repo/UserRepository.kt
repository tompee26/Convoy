package com.tompee.convoy.domain.repo

import com.tompee.convoy.domain.entities.User
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface UserRepository {
    fun getUser(email: String): Single<User>

    fun saveUser(user: User): Completable

    fun searchUser(text: String, exclusionList: List<String> = emptyList()): Single<List<User>>

    fun searchInFriends(email: String, friendEmail: String): Completable

    fun searchInSentRequest(email: String, friendEmail: String): Completable

    fun searchInReceivedRequest(email: String, friendEmail: String): Completable

    fun sendFriendRequest(email: String, friendEmail: String): Completable

    fun acceptFriendRequest(email: String, friendEmail: String): Completable

    fun rejectFriendRequest(email: String, friendEmail: String): Completable

    fun getFriendsList(email: String): Flowable<List<User>>

    fun getFriendRequests(email: String): Flowable<List<User>>

    fun getIncomingFriendRequests(email: String): Flowable<List<User>>
}