package com.tompee.convoy.dependency.component

import com.tompee.convoy.dependency.module.LoginModule
import com.tompee.convoy.dependency.scopes.LoginScope
import com.tompee.convoy.feature.login.LoginFragment
import com.tompee.convoy.feature.login.page.LoginPageFragment
import dagger.Component

@LoginScope
@Component(dependencies = [AppComponent::class, NavigatorComponent::class],
        modules = [LoginModule::class])
interface LoginComponent {
    fun inject(loginFragment: LoginFragment)
    fun inject(loginFragment: LoginPageFragment)
}