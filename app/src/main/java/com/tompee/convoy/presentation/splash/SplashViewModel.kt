package com.tompee.convoy.presentation.splash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tompee.convoy.domain.interactor.SplashInteractor
import com.tompee.convoy.presentation.base.BaseViewModel
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class SplashViewModel(private val interactor: SplashInteractor) : BaseViewModel() {

    enum class AuthenticationState {
        COMPLETE,
        NO_PROFILE,
        UNAUTHENTICATED
    }

    class Factory(private val interactor: SplashInteractor) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return SplashViewModel(interactor) as T
        }
    }

    val state = MutableLiveData<AuthenticationState>()

    init {
        subscriptions += interactor.getLoggedInEmail()
            .doOnError { state.postValue(AuthenticationState.UNAUTHENTICATED) }
            .flatMap { email ->
                interactor.getAccount(email)
                    .doOnError { state.postValue(AuthenticationState.NO_PROFILE) }
            }
            .subscribeOn(Schedulers.io())
            .subscribe({ state.postValue(AuthenticationState.COMPLETE) }, Timber::e)
    }
}