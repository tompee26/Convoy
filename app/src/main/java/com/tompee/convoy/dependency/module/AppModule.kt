package com.tompee.convoy.dependency.module

import android.content.Context
import com.tompee.convoy.ConvoyApplication
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Singleton
@Module
class AppModule(private val convoyApplication: ConvoyApplication) {
    @Provides
    @Singleton
    fun providesApplication(): ConvoyApplication = convoyApplication

    @Provides
    @Singleton
    fun providesApplicationContext(): Context = convoyApplication
}