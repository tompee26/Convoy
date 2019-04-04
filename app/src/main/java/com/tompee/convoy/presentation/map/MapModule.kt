package com.tompee.convoy.presentation.map

import com.tompee.convoy.di.scope.MapScope
import com.tompee.convoy.domain.interactor.MapInteractor
import com.tompee.convoy.domain.location.LocationProvider
import com.tompee.convoy.domain.repo.AccountRepository
import dagger.Module
import dagger.Provides

@Module
class MapModule {

    @MapScope
    @Provides
    fun provideMapViewModelFactory(interactor: MapInteractor): MapViewModel.Factory = MapViewModel.Factory(interactor)

    @MapScope
    @Provides
    fun provideInteractor(accountRepository: AccountRepository, locationProvider: LocationProvider): MapInteractor =
        MapInteractor(accountRepository, locationProvider)

}