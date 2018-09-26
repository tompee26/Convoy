package com.tompee.convoy.dependency.component

import com.tompee.convoy.dependency.module.SplashModule
import com.tompee.convoy.dependency.scopes.SplashScope
import com.tompee.convoy.feature.splash.SplashFragment
import dagger.Component

@SplashScope
@Component(modules = [SplashModule::class],
        dependencies = [AppComponent::class, NavigatorComponent::class])
interface SplashComponent {
    fun inject(splashFragment: SplashFragment)
}