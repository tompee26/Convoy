package com.tompee.convoy.dependency.module

import android.content.Context
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import com.tompee.convoy.dependency.PerActivity
import dagger.Module
import dagger.Provides

@PerActivity
@Module
class ActivityModule(private val baseActivity: FragmentActivity) {
    @Provides
    @PerActivity
    fun provideActivity(): FragmentActivity = baseActivity

    @Provides
    @PerActivity
    fun provideContext(): Context = baseActivity

    @Provides
    @PerActivity
    fun provideFragmentManager(): FragmentManager = baseActivity.supportFragmentManager
}