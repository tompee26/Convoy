package com.tompee.convoy.dependency.module

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import com.tompee.convoy.dependency.PerFragment
import dagger.Module
import dagger.Provides

@PerFragment
@Module
class FragmentModule(private val fragment: Fragment) {

    @Provides
    @PerFragment
    internal fun providesFragment(): Fragment = fragment

    @Provides
    @PerFragment
    internal fun provideActivity(): FragmentActivity? = fragment.activity

    @Provides
    @PerFragment
    internal fun providesContext(): Context? = fragment.context

    @Provides
    @PerFragment
    internal fun providesFragmentManager(): FragmentManager = fragment.childFragmentManager
}