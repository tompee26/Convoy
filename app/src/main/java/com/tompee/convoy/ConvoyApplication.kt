package com.tompee.convoy

import android.content.Context
import android.support.multidex.MultiDex
import android.support.multidex.MultiDexApplication
import android.support.v7.app.AppCompatDelegate
import com.tompee.convoy.dependency.component.AppComponent
import com.tompee.convoy.dependency.component.DaggerAppComponent
import com.tompee.convoy.dependency.module.AppModule
import com.tompee.convoy.dependency.module.AuthModule
import com.tompee.convoy.dependency.module.SchedulerModule
import com.tompee.convoy.dependency.module.RepoModule
import timber.log.Timber

class ConvoyApplication : MultiDexApplication() {

    val component: AppComponent by lazy {
        DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .authModule(AuthModule())
                .repoModule(RepoModule())
                .schedulerModule(SchedulerModule())
                .build()
    }

    companion object {
        operator fun get(context: Context): ConvoyApplication {
            return context.applicationContext as ConvoyApplication
        }
    }

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        Timber.plant(Timber.DebugTree())
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}