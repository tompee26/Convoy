package com.tompee.convoy.interactor.location

import android.location.Location
import io.reactivex.Observable

interface LocationInteractor {
    fun getLastKnownLocation(): Observable<Location>

    fun getLocationUpdates(): Observable<Location>
}