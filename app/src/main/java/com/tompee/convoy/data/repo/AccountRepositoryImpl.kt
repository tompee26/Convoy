package com.tompee.convoy.data.repo

import com.tompee.convoy.domain.entities.Account
import com.tompee.convoy.domain.repo.AccountRepository
import io.reactivex.Completable
import io.reactivex.Single

class AccountRepositoryImpl : AccountRepository {

    private var savedAccount: Account? = null

    override fun saveAccount(account: Account): Completable {
        return Completable.fromAction { savedAccount = account }
    }

    override fun getAccount(): Single<Account> {
        return Single.create { emitter ->
            if (savedAccount == null) {
                emitter.onError(Throwable("No saved account"))
            } else {
                emitter.onSuccess(savedAccount!!)
            }
        }
    }
}