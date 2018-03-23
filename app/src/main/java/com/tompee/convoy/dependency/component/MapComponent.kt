package com.tompee.convoy.dependency.component

import com.tompee.convoy.dependency.module.MapModule
import com.tompee.convoy.feature.map.MapActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [MapModule::class])
interface MapComponent {
    fun inject(mapActivity: MapActivity)
}