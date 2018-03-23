package com.tompee.convoy.dependency.component

import android.support.v4.app.FragmentManager
import com.tompee.convoy.base.BaseActivity
import com.tompee.convoy.dependency.PerActivity
import com.tompee.convoy.dependency.module.ActivityModule
import dagger.Subcomponent

@PerActivity
@Subcomponent(modules = [ActivityModule::class])
interface ActivityComponent {
    fun inject(baseActivity: BaseActivity)

    fun fragmentManager(): FragmentManager
}