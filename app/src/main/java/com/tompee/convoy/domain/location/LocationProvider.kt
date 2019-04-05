package com.tompee.convoy.domain.location

import android.location.Address
import android.location.Location
import io.reactivex.Flowable
import io.reactivex.Single

interface LocationProvider {
    fun getLocation(): Flowable<Location>

    fun geocode(query: String): Single<List<Address>>
}