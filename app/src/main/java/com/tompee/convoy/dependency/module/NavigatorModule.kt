package com.tompee.convoy.dependency.module

import androidx.navigation.NavController
import com.tompee.convoy.core.navigator.Navigator
import com.tompee.convoy.core.navigator.impl.NavigatorImpl
import dagger.Module
import dagger.Provides

@Module
class NavigatorModule(private val navController: NavController) {

    @Provides
    fun provideNavigator(navigatorImpl: NavigatorImpl): Navigator = navigatorImpl

    @Provides
    fun provideNavigatorImpl(): NavigatorImpl = NavigatorImpl(navController)
}