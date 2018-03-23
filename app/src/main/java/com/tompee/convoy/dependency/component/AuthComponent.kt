package com.tompee.convoy.dependency.component

import com.tompee.convoy.dependency.module.AuthModule
import com.tompee.convoy.interactor.auth.AuthInteractor
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AuthModule::class])
interface AuthComponent {
    fun authInteractor(): AuthInteractor
}