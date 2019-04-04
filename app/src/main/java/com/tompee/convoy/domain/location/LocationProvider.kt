package com.tompee.convoy.domain.location

import android.location.Location
import io.reactivex.Flowable

interface LocationProvider {
    fun getCurrentLocation(): Flowable<Location>
}