package com.tompee.convoy.dependency.module

import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import com.tompee.convoy.feature.login.LoginActivityPresenter
import com.tompee.convoy.feature.login.fragment.LoginFragment
import com.tompee.convoy.interactor.auth.AuthInteractor
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
    fun provideLoginActivityPresenter(authInteractor: AuthInteractor): LoginActivityPresenter {
        return LoginActivityPresenter(authInteractor)
    }
}