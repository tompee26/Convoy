package com.tompee.convoy.dependency.module

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.facebook.CallbackManager
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.tompee.convoy.R
import com.tompee.convoy.core.auth.Authenticator
import com.tompee.convoy.core.auth.FacebookAuthHandler
import com.tompee.convoy.core.auth.GoogleAuthHandler
import com.tompee.convoy.core.model.MutableAccount
import com.tompee.convoy.core.navigator.Navigator
import com.tompee.convoy.core.repo.UserRepository
import com.tompee.convoy.dependency.scopes.LoginScope
import com.tompee.convoy.feature.login.LoginPagerAdapter
import com.tompee.convoy.feature.login.page.LoginPageFragment
import com.tompee.convoy.feature.login.page.LoginPagePresenter
import com.tompee.convoy.interactor.LoginInteractor
import com.tompee.convoy.model.SchedulerPool
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class LoginModule(private val fragment: Fragment,
                  private val fragmentManager: FragmentManager) {

    @Provides
    @Named("login")
    fun provideLoginFragment(): LoginPageFragment = LoginPageFragment.newInstance(LoginPageFragment.LOGIN)

    @Provides
    @Named("signup")
    fun provideSignUpFragment(): LoginPageFragment = LoginPageFragment.newInstance(LoginPageFragment.SIGN_UP)

    @LoginScope
    @Provides
    fun provideLoginPagerAdapter(@Named("login") loginFragment: LoginPageFragment,
                                 @Named("signup") signupFragment: LoginPageFragment) =
            LoginPagerAdapter(fragmentManager, loginFragment, signupFragment)

    @Provides
    fun provideLoginPagePresenter(loginInteractor: LoginInteractor,
                                  schedulerPool: SchedulerPool,
                                  navigator: Navigator) =
            LoginPagePresenter(loginInteractor, schedulerPool, navigator)

    @LoginScope
    @Provides
    fun provideLoginInteractor(authenticator: Authenticator,
                               userRepository: UserRepository,
                               loggedInUser: MutableAccount): LoginInteractor =
            LoginInteractor(authenticator, userRepository, loggedInUser)

    @LoginScope
    @Provides
    fun provideFacebookAuthHandler(): FacebookAuthHandler = FacebookAuthHandler(CallbackManager.Factory.create())

    @LoginScope
    @Provides
    fun provideGoogleAuthHandler(api: GoogleApiClient): GoogleAuthHandler = GoogleAuthHandler(fragment, api)

    @Provides
    @LoginScope
    fun provideGoogleApiClient(context: Context, gso: GoogleSignInOptions): GoogleApiClient {
        return GoogleApiClient.Builder(context)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()
    }

    @Provides
    @LoginScope
    fun provideGoogleSignInOptions(context: Context): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .build()
    }
}
