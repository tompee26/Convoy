package com.tompee.convoy.interactor.user

import com.tompee.convoy.interactor.model.User
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

interface UserInteractor {
    fun getUser(email: String): Single<User>
    fun getUserChanges(email: String): Observable<User>
    fun saveUser(user: User): Single<User>
    fun searchUser(user: String, email: String): Single<List<User>>
    fun addFriendRequest(own: String, target: String): Completable
    fun findUserInOutgoingFriendRequest(own: String, target: String): Completable
    fun findUserInIncomingFriendRequest(own: String, target: String): Completable
    fun acceptFriendRequest(own: String, target: String): Completable
}