package com.tompee.convoy.dependency.component

import com.tompee.convoy.dependency.ConfigPersistent
import com.tompee.convoy.dependency.module.ActivityModule
import com.tompee.convoy.dependency.module.FragmentModule
import dagger.Component

@ConfigPersistent
@Component(dependencies = [AppComponent::class])
interface ConfigPersistentComponent {
    fun activityComponent(activityModule: ActivityModule): ActivityComponent

    fun fragmentComponent(fragmentModule: FragmentModule): FragmentComponent
}