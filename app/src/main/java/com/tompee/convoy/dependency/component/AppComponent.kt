package com.tompee.convoy.dependency.component

import android.content.Context
import com.tompee.convoy.ConvoyApplication
import com.tompee.convoy.dependency.module.AppModule
import com.tompee.convoy.dependency.module.AuthModule
import com.tompee.convoy.dependency.module.SchedulerModule
import com.tompee.convoy.dependency.module.UserModule
import com.tompee.convoy.interactor.auth.AuthInteractor
import com.tompee.convoy.interactor.user.UserInteractor
import dagger.Component
import io.reactivex.Scheduler
import javax.inject.Named
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class,
    AuthModule::class,
    UserModule::class,
    SchedulerModule::class])
interface AppComponent {
    // region Application
    fun context(): Context

    fun convoyApplication(): ConvoyApplication
    // endregion

    // region Interactors
    fun authInteractor(): AuthInteractor

    fun userInteractor(): UserInteractor
    // endregion

    // region Schedulers
    @Named("io")
    fun ioScheduler(): Scheduler

    @Named("ui")
    fun uiScheduler(): Scheduler
    //endregion
}