package com.tompee.convoy.dependency.component

import com.tompee.convoy.dependency.module.LoginModule
import com.tompee.convoy.feature.login.LoginActivity
import com.tompee.convoy.feature.login.LoginFragment
import dagger.Component

@Component(modules = [LoginModule::class])
interface LoginComponent {
    fun inject(loginActivity: LoginActivity)
    fun inject(loginFragment: LoginFragment)
}