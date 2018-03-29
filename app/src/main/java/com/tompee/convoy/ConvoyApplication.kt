package com.tompee.convoy

import android.content.Context
import android.support.multidex.MultiDex
import android.support.multidex.MultiDexApplication
import com.tompee.convoy.dependency.component.AppComponent
import com.tompee.convoy.dependency.component.DaggerAppComponent
import com.tompee.convoy.dependency.module.AppModule
import com.tompee.convoy.dependency.module.AuthModule
import com.tompee.convoy.dependency.module.SchedulerModule
import com.tompee.convoy.dependency.module.UserModule
import timber.log.Timber

class ConvoyApplication : MultiDexApplication() {

    private var appComponent: AppComponent? = null

    companion object {
        operator fun get(context: Context): ConvoyApplication {
            return context.applicationContext as ConvoyApplication
        }
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    var component: AppComponent
        get() {
            if (appComponent == null) {
                appComponent = DaggerAppComponent.builder()
                        .appModule(AppModule(this))
                        .authModule(AuthModule())
                        .userModule(UserModule())
                        .schedulerModule(SchedulerModule())
                        .build()
            }
            return appComponent as AppComponent
        }
        set(appComponent) {
            this.appComponent = appComponent
        }
}