package com.tompee.convoy.dependency.module

import com.tompee.convoy.feature.profile.ProfilePresenter
import com.tompee.convoy.interactor.data.DataInteractor
import dagger.Module
import dagger.Provides

@Module
class ProfileModule {
    @Provides
    fun providesProfilePresenter(dataInteractor: DataInteractor): ProfilePresenter {
        return ProfilePresenter(dataInteractor)
    }
}