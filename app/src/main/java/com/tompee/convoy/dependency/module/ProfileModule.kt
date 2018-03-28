package com.tompee.convoy.dependency.module

import com.tompee.convoy.feature.profile.ProfilePresenter
import com.tompee.convoy.interactor.user.UserInteractor
import dagger.Module
import dagger.Provides

@Module
class ProfileModule {
    @Provides
    fun providesProfilePresenter(userInteractor: UserInteractor): ProfilePresenter {
        return ProfilePresenter(userInteractor)
    }
}