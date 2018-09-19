package com.tompee.convoy.dependency.module

import com.tompee.convoy.base.BaseActivity
import com.tompee.convoy.core.navigator.Navigator
import com.tompee.convoy.core.navigator.impl.NavigatorImpl
import dagger.Module
import dagger.Provides

@Module
class NavigatorModule(private val baseActivity: BaseActivity) {

    @Provides
    fun provideNavigator(navigatorImpl: NavigatorImpl): Navigator = navigatorImpl

    @Provides
    fun provideNavigatorImpl(): NavigatorImpl = NavigatorImpl(baseActivity)
}