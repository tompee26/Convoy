package com.tompee.convoy

import android.app.Application
import android.content.Context
import com.tompee.convoy.dependency.component.AppComponent
import com.tompee.convoy.dependency.component.DaggerAppComponent
import com.tompee.convoy.dependency.module.AppModule
import io.realm.Realm
import timber.log.Timber

class ConvoyApplication : Application() {

    private var appComponent: AppComponent? = null

    companion object {
        operator fun get(context: Context): ConvoyApplication {
            return context.applicationContext as ConvoyApplication
        }
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        Realm.init(this)
    }

    var component: AppComponent
        get() {
            if (appComponent == null) {
                appComponent = DaggerAppComponent.builder()
                        .appModule(AppModule(this))
                        .build()
            }
            return appComponent as AppComponent
        }
        set(appComponent) {
            this.appComponent = appComponent
        }
}