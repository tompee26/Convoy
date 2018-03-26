package com.tompee.convoy.dependency.module

import android.content.Context
import com.facebook.CallbackManager
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.tompee.convoy.R
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
    fun provideAuthInteractorImpl(firebaseAuth: FirebaseAuth, callbackManager: CallbackManager, client: GoogleApiClient): AuthInteractorImpl {
        return AuthInteractorImpl(firebaseAuth, callbackManager, client)
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFacebookCallbackManager(): CallbackManager {
        return CallbackManager.Factory.create()
    }

    @Provides
    fun provideGoogleApiClient(gso: GoogleSignInOptions): GoogleApiClient {
        return GoogleApiClient.Builder(context)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()
    }

    @Provides
    @Singleton
    fun provideGoogleSignInOptions(): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .build()
    }
}