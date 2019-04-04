package com.tompee.convoy.di

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.tompee.convoy.data.location.LocationProviderImpl
import com.tompee.convoy.data.repo.AccountRepositoryImpl
import com.tompee.convoy.data.repo.FirebaseUserRepository
import com.tompee.convoy.domain.location.LocationProvider
import com.tompee.convoy.domain.repo.AccountRepository
import com.tompee.convoy.domain.repo.UserRepository
import dagger.Module
import dagger.Provides
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider
import javax.inject.Singleton

@Module
class DataModule {
    @Singleton
    @Provides
    fun provideLocationProvider(context: Context): LocationProvider =
        LocationProviderImpl(ReactiveLocationProvider(context))

    @Singleton
    @Provides
    fun provideAccountRepository(accountRepositoryImpl: AccountRepositoryImpl): AccountRepository =
        accountRepositoryImpl

    @Singleton
    @Provides
    fun provideAccountRepositoryImpl(): AccountRepositoryImpl = AccountRepositoryImpl()

    @Singleton
    @Provides
    fun provideUserRepository(firebaseUserRepository: FirebaseUserRepository): UserRepository = firebaseUserRepository

    @Singleton
    @Provides
    fun provideFirebaseUserRepository(): FirebaseUserRepository =
        FirebaseUserRepository(FirebaseFirestore.getInstance(), FirebaseStorage.getInstance())
}