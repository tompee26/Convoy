package com.tompee.convoy.dependency.module

import com.tompee.convoy.core.navigator.Navigator
import com.tompee.convoy.dependency.scopes.MapScope
import com.tompee.convoy.feature.map.MapPresenter
import com.tompee.convoy.interactor.MapInteractor
import com.tompee.convoy.model.SchedulerPool
import dagger.Module
import dagger.Provides

@Module
class MapModule {
    @Provides
    fun provideMapPresenter(mapInteractor: MapInteractor,
                            schedulerPool: SchedulerPool,
                            navigator: Navigator): MapPresenter {
        return MapPresenter(mapInteractor, schedulerPool, navigator)
    }

    @Provides
    @MapScope
    fun provideMapInteractor(): MapInteractor = MapInteractor()
//
//    @Provides
//    fun provideLocationInteractorImpl(locationProvider: ReactiveLocationProvider,
//                                      locationRequest: LocationRequest): LocationInteractorImpl {
//        return LocationInteractorImpl(locationProvider, locationRequest)
//    }
//
//    @Provides
//    fun provideLocationProvider(context: Context): ReactiveLocationProvider {
//        return ReactiveLocationProvider(context)
//    }
//
//    @Provides
//    fun provideLocationRequest(): LocationRequest {
//        return LocationRequest.create().apply {
//            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
//            interval = Constants.LOCATION_INTERVAL
//            fastestInterval = Constants.LOCATION_INTERVAL
//        }
//    }
}