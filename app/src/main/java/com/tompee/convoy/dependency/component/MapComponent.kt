package com.tompee.convoy.dependency.component

import com.tompee.convoy.dependency.module.MapModule
import com.tompee.convoy.dependency.scopes.MapScope
import com.tompee.convoy.feature.map.MapActivity
import dagger.Component

@MapScope
@Component(dependencies = [UserComponent::class],
        modules = [MapModule::class])
interface MapComponent {
    fun inject(mapActivity: MapActivity)
}