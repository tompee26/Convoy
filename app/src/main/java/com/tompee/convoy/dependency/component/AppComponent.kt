package com.tompee.convoy.dependency.component

import android.content.Context
import com.tompee.convoy.ConvoyApplication
import com.tompee.convoy.core.auth.Authenticator
import com.tompee.convoy.core.model.MutableAccount
import com.tompee.convoy.core.repo.UserRepository
import com.tompee.convoy.dependency.module.AppModule
import com.tompee.convoy.dependency.module.AuthModule
import com.tompee.convoy.dependency.module.SchedulerModule
import com.tompee.convoy.dependency.module.UserModule
import com.tompee.convoy.interactor.auth.AuthInteractor
import com.tompee.convoy.interactor.user.UserInteractor
import com.tompee.convoy.model.SchedulerPool
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
    fun context(): Context
    fun convoyApplication(): ConvoyApplication
    fun schedulerPool(): SchedulerPool
    fun loggedInAccount(): MutableAccount

    fun authenticator(): Authenticator
    fun userRepository(): UserRepository

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