package com.tompee.convoy.interactor.location

import android.annotation.SuppressLint
import android.location.Location
import com.google.android.gms.location.LocationRequest
import io.reactivex.Observable
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider

class LocationInteractorImpl(private val locationProvider: ReactiveLocationProvider,
                             private val locationRequest: LocationRequest) : LocationInteractor {

    companion object {
        private var locationObservable: Observable<Location>? = null
    }

    @SuppressLint("MissingPermission")
    override fun getLastKnownLocation(): Observable<Location> {
        return locationProvider.lastKnownLocation
    }

    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(): Observable<Location> {
        if (locationObservable == null) {
            synchronized(locationProvider) {
                locationObservable = locationProvider.getUpdatedLocation(locationRequest)
            }
        }
        return locationObservable!!
    }
}
