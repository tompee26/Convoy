package com.tompee.convoy.dependency.component

import com.tompee.convoy.dependency.module.LoginModule
import com.tompee.convoy.dependency.scopes.LoginScope
import com.tompee.convoy.feature.login.LoginActivity
import com.tompee.convoy.feature.login.fragment.LoginFragment
import dagger.Component

@LoginScope
@Component(dependencies = [AuthComponent::class],
        modules = [LoginModule::class])
interface LoginComponent {
    fun inject(loginActivity: LoginActivity)
    fun inject(loginFragment: LoginFragment)
}