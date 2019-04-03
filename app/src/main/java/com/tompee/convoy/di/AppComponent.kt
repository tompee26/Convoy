package com.tompee.convoy.di

import com.tompee.convoy.ConvoyApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules =
    [
        AndroidSupportInjectionModule::class,
        FragmentModule::class,
        DomainModule::class,
        DataModule::class
    ]
)
interface AppComponent : AndroidInjector<ConvoyApplication> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: ConvoyApplication): Builder

        fun build(): AppComponent
    }
}