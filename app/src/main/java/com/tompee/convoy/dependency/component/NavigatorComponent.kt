package com.tompee.convoy.dependency.component

import com.tompee.convoy.core.navigator.Navigator
import com.tompee.convoy.dependency.module.NavigatorModule
import dagger.Component

@Component(modules = [NavigatorModule::class])
interface NavigatorComponent {
    fun navigator(): Navigator
}