package com.tompee.convoy.dependency.module

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.tompee.convoy.interactor.user.UserInteractor
import com.tompee.convoy.interactor.user.UserInteractorImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Singleton
@Module
class UserModule {
    @Provides
    @Singleton
    fun provideDataInteractor(dataInteractorImpl: UserInteractorImpl): UserInteractor {
        return dataInteractorImpl
    }

    @Provides
    @Singleton
    fun provideDataInteractorImpl(databaseReference: DatabaseReference): UserInteractorImpl {
        return UserInteractorImpl(databaseReference)
    }

    @Provides
    @Singleton
    fun provideDatabaseReference(): DatabaseReference {
        val reference = FirebaseDatabase.getInstance()
        reference.setPersistenceEnabled(true)
        return reference.reference
    }
}