package com.tompee.convoy.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.tompee.convoy.ConvoyApplication
import com.tompee.convoy.domain.authenticator.Authenticator
import com.tompee.convoy.domain.authenticator.firebase.FirebaseAuthenticator
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DomainModule {
    @Singleton
    @Provides
    fun provideContext(application : ConvoyApplication) : Context = application

    @Singleton
    @Provides
    fun provideAuthenticator(firebaseAuthenticator: FirebaseAuthenticator): Authenticator = firebaseAuthenticator

    @Singleton
    @Provides
    fun provideFirebaseAuthenticator(): FirebaseAuthenticator = FirebaseAuthenticator(FirebaseAuth.getInstance())
}