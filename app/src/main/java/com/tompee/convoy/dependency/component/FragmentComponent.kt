package com.tompee.convoy.dependency.component

import android.support.v4.app.FragmentManager
import com.tompee.convoy.dependency.PerFragment
import com.tompee.convoy.dependency.module.FragmentModule
import dagger.Subcomponent

@PerFragment
@Subcomponent(modules = [FragmentModule::class])
interface FragmentComponent {
    fun fragmentManager(): FragmentManager
}