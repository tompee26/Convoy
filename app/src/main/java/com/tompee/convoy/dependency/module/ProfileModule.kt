package com.tompee.convoy.dependency.module

import android.content.Context
import androidx.fragment.app.Fragment
import com.tompee.convoy.core.cropper.ImageCropper
import com.tompee.convoy.core.model.MutableAccount
import com.tompee.convoy.core.navigator.Navigator
import com.tompee.convoy.core.repo.ProfileImageRepo
import com.tompee.convoy.core.repo.UserRepository
import com.tompee.convoy.dependency.scopes.ProfileScope
import com.tompee.convoy.feature.profilesetup.ProfileSetupPresenter
import com.tompee.convoy.interactor.ProfileInteractor
import com.tompee.convoy.model.SchedulerPool
import dagger.Module
import dagger.Provides

@Module
class ProfileModule(private val fragment: Fragment,
                    private val context: Context) {
    @Provides
    @ProfileScope
    fun provideProfileInteractor(userRepository: UserRepository,
                                 profileImageRepo: ProfileImageRepo,
                                 loggedInUser: MutableAccount):
            ProfileInteractor = ProfileInteractor(userRepository, profileImageRepo, loggedInUser)

    @Provides
    fun provideProfileSetupPresenter(profileInteractor: ProfileInteractor,
                                     schedulerPool: SchedulerPool,
                                     navigator: Navigator):
            ProfileSetupPresenter = ProfileSetupPresenter(profileInteractor, schedulerPool, navigator)

    @ProfileScope
    @Provides
    fun provideImageCropper(): ImageCropper = ImageCropper(fragment, context)
}