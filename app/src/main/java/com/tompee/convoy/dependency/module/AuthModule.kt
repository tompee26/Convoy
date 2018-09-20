package com.tompee.convoy.dependency.module

import com.google.firebase.auth.FirebaseAuth
import com.tompee.convoy.core.auth.Authenticator
import com.tompee.convoy.core.auth.firebase.FirebaseAuthenticator
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AuthModule {

    @Provides
    @Singleton
    fun provideAuthenticator(firebaseAuthenticator: FirebaseAuthenticator): Authenticator = firebaseAuthenticator

    @Provides
    @Singleton
    fun provideFirebaseAuthenticator(): FirebaseAuthenticator = FirebaseAuthenticator(FirebaseAuth.getInstance())
}