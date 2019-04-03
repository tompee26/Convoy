package com.tompee.convoy.di

import com.tompee.convoy.di.scope.*
import com.tompee.convoy.presentation.friends.FriendListFragment
import com.tompee.convoy.presentation.friends.FriendModule
import com.tompee.convoy.presentation.friends.profile.ProfileDialog
import com.tompee.convoy.presentation.friends.search.FriendSearchFragment
import com.tompee.convoy.presentation.login.LoginFragment
import com.tompee.convoy.presentation.login.LoginModule
import com.tompee.convoy.presentation.login.LoginPageFragment
import com.tompee.convoy.presentation.map.MapFragment
import com.tompee.convoy.presentation.map.MapModule
import com.tompee.convoy.presentation.profile.ProfileModule
import com.tompee.convoy.presentation.profile.ProfileSetupFragment
import com.tompee.convoy.presentation.splash.SplashFragment
import com.tompee.convoy.presentation.splash.SplashModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentModule {
    @SplashScope
    @ContributesAndroidInjector(modules = [SplashModule::class])
    abstract fun bindSplashFragment(): SplashFragment

    @LoginScope
    @ContributesAndroidInjector(modules = [LoginModule::class])
    abstract fun bindLoginFragment(): LoginFragment

    @LoginScope
    @ContributesAndroidInjector(modules = [LoginModule::class])
    abstract fun bindLoginPageFragment(): LoginPageFragment

    @ProfileScope
    @ContributesAndroidInjector(modules = [ProfileModule::class])
    abstract fun bindProfileSetupFragment(): ProfileSetupFragment

    @MapScope
    @ContributesAndroidInjector(modules = [MapModule::class])
    abstract fun bindMapFragment(): MapFragment

    @FriendScope
    @ContributesAndroidInjector(modules = [FriendModule::class])
    abstract fun bindFriendListFragment(): FriendListFragment

    @FriendScope
    @ContributesAndroidInjector(modules = [FriendModule::class])
    abstract fun bindFriendSearchFragment(): FriendSearchFragment

    @FriendScope
    @ContributesAndroidInjector(modules = [FriendModule::class])
    abstract fun bindProfileDialog(): ProfileDialog
}