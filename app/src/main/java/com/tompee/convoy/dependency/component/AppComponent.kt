package com.tompee.convoy.dependency.component

import android.content.Context
import com.tompee.convoy.ConvoyApplication
import com.tompee.convoy.dependency.module.AppModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun context(): Context

    fun convoyApplication(): ConvoyApplication
}