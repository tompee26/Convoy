package com.tompee.convoy

import com.tompee.convoy.di.DaggerAppComponent
import dagger.android.AndroidInjector
import timber.log.Timber

class ConvoyApplication : dagger.android.support.DaggerApplication() {
    override fun applicationInjector(): AndroidInjector<out dagger.android.support.DaggerApplication> =
        DaggerAppComponent.builder().application(this).build()

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}