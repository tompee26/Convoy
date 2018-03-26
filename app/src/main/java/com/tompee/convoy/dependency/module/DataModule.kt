package com.tompee.convoy.dependency.module

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.tompee.convoy.interactor.data.DataInteractor
import com.tompee.convoy.interactor.data.DataInteractorImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Singleton
@Module
class DataModule {
    @Provides
    @Singleton
    fun provideDataInteractor(dataInteractorImpl: DataInteractorImpl): DataInteractor {
        return dataInteractorImpl
    }

    @Provides
    @Singleton
    fun provideDataInteractorImpl(databaseReference: DatabaseReference): DataInteractorImpl {
        return DataInteractorImpl(databaseReference)
    }

    @Provides
    @Singleton
    fun provideDatabaseReference(): DatabaseReference {
        return FirebaseDatabase.getInstance().reference
    }
}