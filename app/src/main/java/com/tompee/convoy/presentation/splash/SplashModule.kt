package com.tompee.convoy.presentation.splash

import com.tompee.convoy.di.scope.SplashScope
import com.tompee.convoy.domain.authenticator.Authenticator
import com.tompee.convoy.domain.interactor.SplashInteractor
import com.tompee.convoy.domain.repo.AccountRepository
import com.tompee.convoy.domain.repo.UserRepository
import dagger.Module
import dagger.Provides

@Module
class SplashModule {

    @SplashScope
    @Provides
    fun provideSplashViewModelFactory(interactor: SplashInteractor):
            SplashViewModel.Factory = SplashViewModel.Factory(interactor)

    @SplashScope
    @Provides
    fun provideInteractor(
        authenticator: Authenticator,
        userRepository: UserRepository,
        accountRepository: AccountRepository
    ): SplashInteractor = SplashInteractor(authenticator, userRepository, accountRepository)
}