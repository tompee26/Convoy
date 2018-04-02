package com.tompee.convoy.dependency.module

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
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
    fun providesDataInteractorFirestoreImpl(db: FirebaseFirestore): UserInteractorImpl {
        return UserInteractorImpl(db)
    }

    @Provides
    @Singleton
    fun provideFirestoreDatabaseReference(): FirebaseFirestore {
        val reference = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build()
        reference.firestoreSettings = settings
        return reference
    }
}