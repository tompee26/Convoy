package com.tompee.convoy.dependency.module

import android.content.Context
import com.google.android.gms.location.LocationRequest
import com.tompee.convoy.Constants
import com.tompee.convoy.feature.map.MapPresenter
import com.tompee.convoy.interactor.location.LocationInteractor
import com.tompee.convoy.interactor.location.LocationInteractorImpl
import dagger.Module
import dagger.Provides
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider
import javax.inject.Singleton

@Module
class MapModule(private val context: Context) {
    @Provides
    fun provideMapPresenter(locationInteractor: LocationInteractor): MapPresenter {
        return MapPresenter(locationInteractor, context)
    }

    @Provides
    @Singleton
    fun provideLocationInteractor(locationInteractorImpl: LocationInteractorImpl): LocationInteractor {
        return locationInteractorImpl
    }

    @Provides
    @Singleton
    fun provideLocationInteractorImpl(locationProvider: ReactiveLocationProvider,
                                      locationRequest: LocationRequest): LocationInteractorImpl {
        return LocationInteractorImpl(locationProvider, locationRequest)
    }

    @Provides
    @Singleton
    fun provideLocationProvider(): ReactiveLocationProvider {
        return ReactiveLocationProvider(context)
    }

    @Provides
    @Singleton
    fun provideLocationRequest(): LocationRequest {
        val locationRequest = LocationRequest().apply {
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
            interval = Constants.LOCATION_INTERVAL
            fastestInterval = Constants.LOCATION_INTERVAL
        }
        return locationRequest
    }
}