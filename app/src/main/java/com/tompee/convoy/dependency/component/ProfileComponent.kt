package com.tompee.convoy.dependency.component

import com.tompee.convoy.dependency.module.ProfileModule
import com.tompee.convoy.dependency.scopes.ProfileScope
import com.tompee.convoy.feature.profile.ProfileActivity
import dagger.Component

@ProfileScope
@Component(dependencies = [DataComponent::class],
        modules = [ProfileModule::class])
interface ProfileComponent {
    fun inject(profileActivity: ProfileActivity)
}
