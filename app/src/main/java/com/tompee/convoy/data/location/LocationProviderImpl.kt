package com.tompee.convoy.data.location

import android.location.Address
import android.location.Location
import com.google.android.gms.location.LocationRequest
import com.tompee.convoy.domain.location.LocationProvider
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider

class LocationProviderImpl(private val provider: ReactiveLocationProvider) : LocationProvider {

    override fun getLocation(): Flowable<Location> {
        val request = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(1000)

        return provider.getUpdatedLocation(request).toFlowable(BackpressureStrategy.LATEST)
    }

    override fun geocode(query: String): Single<List<Address>> {
        return provider.getGeocodeObservable(query, 5).firstOrError()
    }

}