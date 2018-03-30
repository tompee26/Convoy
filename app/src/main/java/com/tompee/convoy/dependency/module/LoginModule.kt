package com.tompee.convoy.dependency.module

import android.content.Context
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import com.tompee.convoy.feature.login.LoginActivityPresenter
import com.tompee.convoy.feature.login.adapters.LoginPagerAdapter
import com.tompee.convoy.feature.login.adapters.ProfilePagerAdapter
import com.tompee.convoy.feature.login.adapters.ProgressPagerAdapter
import com.tompee.convoy.feature.login.login.LoginFragment
import com.tompee.convoy.feature.login.login.LoginFragmentPresenter
import com.tompee.convoy.feature.login.profile.ProfileFragment
import com.tompee.convoy.feature.login.profile.ProfileFragmentPresenter
import com.tompee.convoy.feature.login.progress.ProgressFragment
import com.tompee.convoy.interactor.auth.AuthInteractor
import com.tompee.convoy.interactor.user.UserInteractor
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import javax.inject.Named

@Module
class LoginModule(private val fragmentActivity: FragmentActivity) {

    @Provides
    fun provideFragmentManager(): FragmentManager = fragmentActivity.supportFragmentManager

    // region Fragments
    @Provides
    @Named("login")
    fun provideLoginFragment(): LoginFragment {
        return LoginFragment.newInstance(LoginFragment.LOGIN)
    }

    @Provides
    @Named("signUp")
    fun provideSignUpFragment(): LoginFragment {
        return LoginFragment.newInstance(LoginFragment.SIGN_UP)
    }

    @Provides
    fun provideCheckFragment(): ProgressFragment {
        return ProgressFragment.newInstance()
    }

    @Provides
    fun provideProfileFragment(): ProfileFragment {
        return ProfileFragment.newInstance()
    }
    // endregion

    // region Fragment list
    @Provides
    fun provideLoginFragmentList(@Named("login") fragmentLogin: LoginFragment,
                                 @Named("signUp") fragmentSignUp: LoginFragment): List<LoginFragment> {
        return listOf(fragmentLogin, fragmentSignUp)
    }

    @Provides
    fun provideCheckFragmentList(check: ProgressFragment): List<ProgressFragment> {
        return listOf(check)
    }

    @Provides
    fun provideProfileFragmentList(profile: ProfileFragment): List<ProfileFragment> {
        return listOf(profile)
    }
    // endregion

    // region Adapters
    @Provides
    fun provideLoginAdapter(fragmentManager: FragmentManager,
                            fragmentList: List<LoginFragment>): LoginPagerAdapter {
        return LoginPagerAdapter(fragmentManager, fragmentList)
    }

    @Provides
    fun provideProgressAdapter(fragmentManager: FragmentManager,
                               fragmentList: List<ProgressFragment>): ProgressPagerAdapter {
        return ProgressPagerAdapter(fragmentManager, fragmentList)
    }

    @Provides
    fun provideProfileAdapter(fragmentManager: FragmentManager,
                              fragmentList: List<ProfileFragment>): ProfilePagerAdapter {
        return ProfilePagerAdapter(fragmentManager, fragmentList)
    }
    // endregion

    // region Presenters
    @Provides
    fun provideLoginActivityPresenter(authInteractor: AuthInteractor,
                                      userInteractor: UserInteractor,
                                      @Named("io") io: Scheduler,
                                      @Named("ui") ui: Scheduler): LoginActivityPresenter {
        return LoginActivityPresenter(authInteractor, userInteractor, io, ui)
    }

    @Provides
    fun provideLoginFragmentPresenter(authInteractor: AuthInteractor,
                                      @Named("io") io: Scheduler,
                                      @Named("ui") ui: Scheduler): LoginFragmentPresenter {
        return LoginFragmentPresenter(authInteractor, io, ui)
    }

    @Provides
    fun provideProfileFragmentPresenter(context: Context,
                                        userInteractor: UserInteractor,
                                        @Named("io") io: Scheduler,
                                        @Named("ui") ui: Scheduler): ProfileFragmentPresenter {
        return ProfileFragmentPresenter(context, userInteractor, io, ui)
    }
    // endregion
}