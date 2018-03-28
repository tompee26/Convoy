package com.tompee.convoy.interactor.user

import com.tompee.convoy.interactor.model.User
import io.reactivex.Single

interface UserInteractor {
    fun getUser(email: String): Single<User>
    fun saveUser(firstName: String, lastName: String, displayName: String, email: String): Single<User>
}