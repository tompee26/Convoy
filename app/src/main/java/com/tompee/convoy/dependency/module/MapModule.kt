package com.tompee.convoy.dependency.module

import android.content.Context
import com.google.android.gms.location.LocationRequest
import com.tompee.convoy.Constants
import com.tompee.convoy.feature.map.MapPresenter
import com.tompee.convoy.interactor.user.UserInteractor
import com.tompee.convoy.interactor.location.LocationInteractor
import com.tompee.convoy.interactor.location.LocationInteractorImpl
import dagger.Module
import dagger.Provides
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider

@Module
class MapModule(private val context: Context) {
    @Provides
    fun provideMapPresenter(locationInteractor: LocationInteractor,
                            userInteractor: UserInteractor): MapPresenter {
        return MapPresenter(locationInteractor, userInteractor, context)
    }

    @Provides
    fun provideLocationInteractor(locationInteractorImpl: LocationInteractorImpl): LocationInteractor {
        return locationInteractorImpl
    }

    @Provides
    fun provideLocationInteractorImpl(locationProvider: ReactiveLocationProvider,
                                      locationRequest: LocationRequest): LocationInteractorImpl {
        return LocationInteractorImpl(locationProvider, locationRequest)
    }

    @Provides
    fun provideLocationProvider(): ReactiveLocationProvider {
        return ReactiveLocationProvider(context)
    }

    @Provides
    fun provideLocationRequest(): LocationRequest {
        return LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
            interval = Constants.LOCATION_INTERVAL
            fastestInterval = Constants.LOCATION_INTERVAL
        }
    }
}