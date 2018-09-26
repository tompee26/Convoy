package com.tompee.convoy.dependency.component

import com.tompee.convoy.dependency.module.MapModule
import com.tompee.convoy.dependency.scopes.MapScope
import com.tompee.convoy.feature.map.MapFragment
import dagger.Component

@MapScope
@Component(dependencies = [AppComponent::class, NavigatorComponent::class],
        modules = [MapModule::class])
interface MapComponent {
    fun inject(mapFragment: MapFragment)
}