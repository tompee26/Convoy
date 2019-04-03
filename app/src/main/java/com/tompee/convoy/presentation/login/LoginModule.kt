package com.tompee.convoy.presentation.login

import android.content.Context
import androidx.fragment.app.FragmentManager
import com.facebook.CallbackManager
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.tompee.convoy.R
import com.tompee.convoy.di.scope.LoginScope
import com.tompee.convoy.domain.authenticator.Authenticator
import com.tompee.convoy.domain.interactor.LoginInteractor
import com.tompee.convoy.domain.repo.AccountRepository
import com.tompee.convoy.domain.repo.UserRepository
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class LoginModule {

    @LoginScope
    @Provides
    fun provideFacebookAuthHandler() = FacebookAuthHandler(CallbackManager.Factory.create())

    @LoginScope
    @Provides
    fun provideGoogleAuthHandler(fragment: LoginPageFragment, api: GoogleApiClient) = GoogleAuthHandler(fragment, api)

    @LoginScope
    @Provides
    fun provideGoogleApiClient(context: Context, gso: GoogleSignInOptions): GoogleApiClient {
        return GoogleApiClient.Builder(context)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()
    }

    @LoginScope
    @Provides
    fun provideGoogleSignInOptions(context: Context): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .build()
    }

    @LoginScope
    @Provides
    fun provideLoginViewModelFactory(interactor: LoginInteractor): LoginViewModel.Factory =
        LoginViewModel.Factory(interactor)

    @LoginScope
    @Provides
    fun provideInteractor(
        authenticator: Authenticator,
        userRepository: UserRepository,
        accountRepository: AccountRepository
    ): LoginInteractor = LoginInteractor(authenticator, userRepository, accountRepository)

    @LoginScope
    @Provides
    fun provideLoginPagerAdapter(
        fragmentManager: FragmentManager,
        @Named("login") loginFragment: LoginPageFragment,
        @Named("register") registerFragment: LoginPageFragment
    ) = LoginPagerAdapter(fragmentManager, loginFragment, registerFragment)

    @LoginScope
    @Provides
    @Named("login")
    fun provideLoginPage(): LoginPageFragment = LoginPageFragment.newInstance(
        LoginPageFragment.LOGIN
    )

    @LoginScope
    @Provides
    @Named("register")
    fun provideRegisterPage(): LoginPageFragment = LoginPageFragment.newInstance(
        LoginPageFragment.SIGN_UP
    )

    @LoginScope
    @Provides
    fun provideFragmentManager(loginFragment: LoginFragment): FragmentManager = loginFragment.fragmentManager!!
}