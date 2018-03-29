package com.tompee.convoy.dependency.component

import com.tompee.convoy.dependency.module.LoginModule
import com.tompee.convoy.dependency.scopes.LoginScope
import com.tompee.convoy.feature.login.LoginActivity
import com.tompee.convoy.feature.login.login.LoginFragment
import com.tompee.convoy.feature.login.profile.ProfileFragment
import dagger.Component

@LoginScope
@Component(dependencies = [AppComponent::class],
        modules = [LoginModule::class])
interface LoginComponent {
    fun inject(loginActivity: LoginActivity)
    fun inject(loginFragment: LoginFragment)
    fun inject(profileFragment: ProfileFragment)
}