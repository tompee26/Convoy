package com.tompee.convoy.dependency.module

import com.tompee.convoy.core.auth.Authenticator
import com.tompee.convoy.core.model.MutableAccount
import com.tompee.convoy.core.navigator.Navigator
import com.tompee.convoy.core.repo.UserRepository
import com.tompee.convoy.feature.splash.SplashPresenter
import com.tompee.convoy.interactor.SplashInteractor
import com.tompee.convoy.model.SchedulerPool
import dagger.Module
import dagger.Provides

@Module
class SplashModule {

    @Provides
    fun provideSplashPresenter(splashInteractor: SplashInteractor,
                               schedulerPool: SchedulerPool,
                               navigator: Navigator):
            SplashPresenter = SplashPresenter(splashInteractor, schedulerPool, navigator)

    @Provides
    fun provideSplashInteractor(authenticator: Authenticator,
                                userRepository: UserRepository,
                                loggedInUser: MutableAccount): SplashInteractor =
            SplashInteractor(authenticator, userRepository, loggedInUser)
}