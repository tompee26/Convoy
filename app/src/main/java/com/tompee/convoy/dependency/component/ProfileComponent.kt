package com.tompee.convoy.dependency.component

import com.tompee.convoy.dependency.module.ProfileModule
import com.tompee.convoy.dependency.scopes.ProfileScope
import com.tompee.convoy.feature.profilesetup.ProfileSetupFragment
import dagger.Component

@ProfileScope
@Component(modules = [ProfileModule::class],
        dependencies = [AppComponent::class, NavigatorComponent::class])
interface ProfileComponent {
    fun inject(profileSetupFragment: ProfileSetupFragment)
}