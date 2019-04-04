package com.tompee.convoy.domain.interactor

import android.location.Location
import com.tompee.convoy.domain.entities.Account
import com.tompee.convoy.domain.location.LocationProvider
import com.tompee.convoy.domain.repo.AccountRepository
import io.reactivex.Flowable
import io.reactivex.Single

class MapInteractor(
    private val accountRepository: AccountRepository,
    private val locationProvider: LocationProvider
) {

    fun getAccount(): Single<Account> {
        return accountRepository.getAccount()
    }

    fun getLocation(): Flowable<Location> {
        return locationProvider.getLocation()
    }
}