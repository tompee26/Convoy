package com.tompee.convoy.dependency.module

import android.content.Context
import com.google.android.gms.location.LocationRequest
import com.tompee.convoy.Constants
import com.tompee.convoy.feature.map.MapPresenter
import com.tompee.convoy.interactor.location.LocationInteractor
import com.tompee.convoy.interactor.location.LocationInteractorImpl
import com.tompee.convoy.interactor.user.UserInteractor
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider
import javax.inject.Named

@Module
class MapModule {
    @Provides
    fun provideMapPresenter(context: Context,
                            locationInteractor: LocationInteractor,
                            userInteractor: UserInteractor,
                            @Named("io") io: Scheduler,
                            @Named("ui") ui: Scheduler): MapPresenter {
        return MapPresenter(context, locationInteractor, userInteractor, io, ui)
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
    fun provideLocationProvider(context: Context): ReactiveLocationProvider {
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