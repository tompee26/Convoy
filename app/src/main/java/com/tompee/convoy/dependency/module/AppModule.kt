package com.tompee.convoy.dependency.module

import android.content.Context
import com.tompee.convoy.ConvoyApplication
import com.tompee.convoy.core.model.MutableAccount
import com.tompee.convoy.model.SchedulerPool
import dagger.Module
import dagger.Provides
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Singleton

@Module
class AppModule(private val convoyApplication: ConvoyApplication) {
    @Provides
    @Singleton
    fun providesApplication(): ConvoyApplication = convoyApplication

    @Provides
    @Singleton
    fun providesApplicationContext(): Context = convoyApplication

    @Provides
    @Singleton
    fun provideSchedulerPool(): SchedulerPool = SchedulerPool(Schedulers.io(),
            AndroidSchedulers.mainThread(), Schedulers.computation(), Schedulers.trampoline())

    @Provides
    @Singleton
    fun provideLoggedInAccount(): MutableAccount = MutableAccount()
}