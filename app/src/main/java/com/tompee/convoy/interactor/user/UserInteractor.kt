package com.tompee.convoy.interactor.user

import com.tompee.convoy.interactor.model.User
import io.reactivex.Observable
import io.reactivex.Single

interface UserInteractor {
    fun getUser(email: String): Single<User>
    fun getUserChanges(userId: String): Observable<User>
    fun saveUser(user: User): Single<User>
}