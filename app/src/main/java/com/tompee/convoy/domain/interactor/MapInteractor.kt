package com.tompee.convoy.domain.interactor

import com.tompee.convoy.domain.entities.Account
import com.tompee.convoy.domain.repo.AccountRepository
import io.reactivex.Single

class MapInteractor(private val accountRepository: AccountRepository) {

    fun getAccount(): Single<Account> {
        return accountRepository.getAccount()
    }
}