package com.tompee.convoy.dependency.component

import com.tompee.convoy.dependency.module.DataModule
import com.tompee.convoy.interactor.data.DataInteractor
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [DataModule::class])
interface DataComponent {
    fun dataInteractor(): DataInteractor
}