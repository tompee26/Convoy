package com.tompee.convoy.dependency.module

import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import com.facebook.CallbackManager
import com.tompee.convoy.feature.login.LoginFragment
import com.tompee.convoy.interactor.auth.*
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class LoginModule(private val fragmentActivity: FragmentActivity) {

    @Provides
    fun provideFragmentManager(): FragmentManager = fragmentActivity.supportFragmentManager

    @Provides
    @Named("login")
    fun provideLoginFragment(): LoginFragment {
        return LoginFragment.newInstance(LoginFragment.LOGIN)
    }

    @Provides
    @Named("signup")
    fun provideSignupFragment(): LoginFragment {
        return LoginFragment.newInstance(LoginFragment.SIGN_UP)
    }

    @Provides
    fun provideLoginFragmentList(@Named("login") fragmentLogin: LoginFragment,
                                 @Named("signup") fragmentSignup: LoginFragment): List<LoginFragment> {
        return listOf(fragmentLogin, fragmentSignup)
    }

    @Provides
    fun provideAuthInteractor(authInteractorImpl: AuthInteractorImpl): AuthInteractor {
        return authInteractorImpl
    }

    @Provides
    fun provideAuthInteractorImpl(userPasswordAuth: UserPasswordAuth,
                                  facebookAuth: FacebookAuth,
                                  googleAuth: GoogleAuth): AuthInteractorImpl {
        return AuthInteractorImpl(userPasswordAuth, facebookAuth, googleAuth)
    }

    @Provides
    fun provideUserPasswordAuth(): UserPasswordAuth {
        return UserPasswordAuth()
    }

    @Provides
    fun provideFacebookAuth(callbackManager: CallbackManager): FacebookAuth {
        return FacebookAuth(callbackManager)
    }

    @Provides
    fun provideFacebookCallbackManager(): CallbackManager {
        return CallbackManager.Factory.create()
    }

    @Provides
    fun provideGoogleAuth(): GoogleAuth {
        return GoogleAuth(fragmentActivity)
    }
}