package com.tompee.convoy.core.repo

import io.reactivex.Completable
import io.reactivex.Single

interface UserRepository {
    fun getUser(email: String): Single<UserEntity>

    fun getUserImage(email: String): Single<String>

    fun saveUser(userEntity: UserEntity): Completable
}