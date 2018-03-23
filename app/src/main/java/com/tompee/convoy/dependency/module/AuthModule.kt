package com.tompee.convoy.dependency.module

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.tompee.convoy.interactor.auth.AuthInteractor
import com.tompee.convoy.interactor.auth.AuthInteractorImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Singleton
@Module
class AuthModule(private val context: Context) {
    @Provides
    @Singleton
    fun provideAuthInteractor(authInteractorImpl: AuthInteractorImpl): AuthInteractor {
        return authInteractorImpl
    }

    @Provides
    @Singleton
    fun provideAuthInteractorImpl(firebaseAuth: FirebaseAuth): AuthInteractorImpl {
        return AuthInteractorImpl(firebaseAuth)
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }
}