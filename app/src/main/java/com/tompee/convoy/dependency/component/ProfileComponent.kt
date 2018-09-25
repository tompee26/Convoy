package com.tompee.convoy.dependency.component

import com.tompee.convoy.dependency.module.ProfileModule
import com.tompee.convoy.dependency.scopes.ProfileScope
import com.tompee.convoy.feature.profilesetup.ProfileSetupActivity
import dagger.Component

@ProfileScope
@Component(modules = [ProfileModule::class],
        dependencies = [AppComponent::class, NavigatorComponent::class])
interface ProfileComponent {
    fun inject(profileSetupActivity: ProfileSetupActivity)
}