package com.tompee.convoy.dependency.module

import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Named
import javax.inject.Singleton

@Module
class SchedulerModule {

    @Provides
    @Singleton
    @Named("ui")
    fun providesUiScheduler(): Scheduler {
        return AndroidSchedulers.mainThread()
    }

    @Provides
    @Singleton
    @Named("io")
    fun providesIoScheduler(): Scheduler {
        return Schedulers.io()
    }
}