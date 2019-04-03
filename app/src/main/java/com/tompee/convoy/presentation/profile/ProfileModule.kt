package com.tompee.convoy.presentation.profile

import android.content.Context
import com.tompee.convoy.di.scope.ProfileScope
import com.tompee.convoy.domain.authenticator.Authenticator
import com.tompee.convoy.domain.interactor.ProfileInteractor
import com.tompee.convoy.domain.repo.AccountRepository
import com.tompee.convoy.domain.repo.UserRepository
import dagger.Module
import dagger.Provides

@Module
class ProfileModule {

    @ProfileScope
    @Provides
    fun provideImageCropper(context: Context, fragment: ProfileSetupFragment): ImageCropper =
        ImageCropper(fragment, context)

    @ProfileScope
    @Provides
    fun provideProfileSetupViewModelFactory(interactor: ProfileInteractor): ProfileSetupViewModel.Factory =
        ProfileSetupViewModel.Factory(interactor)

    @ProfileScope
    @Provides
    fun provideInteractor(
        authenticator: Authenticator,
        accountRepository: AccountRepository,
        userRepository: UserRepository
    ): ProfileInteractor =
        ProfileInteractor(authenticator, accountRepository, userRepository)
}