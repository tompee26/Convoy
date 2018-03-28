package com.tompee.convoy.dependency.component

import com.tompee.convoy.dependency.module.UserModule
import com.tompee.convoy.interactor.user.UserInteractor
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [UserModule::class])
interface UserComponent {
    fun userInteractor(): UserInteractor
}