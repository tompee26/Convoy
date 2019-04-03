package com.tompee.convoy.domain.repo

import com.tompee.convoy.domain.entities.Account
import io.reactivex.Completable
import io.reactivex.Single

interface AccountRepository {

    fun saveAccount(account: Account): Completable

    fun getAccount(): Single<Account>
}