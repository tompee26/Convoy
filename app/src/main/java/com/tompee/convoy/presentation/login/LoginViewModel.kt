package com.tompee.convoy.presentation.login

import android.util.Patterns
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.tompee.convoy.Constants
import com.tompee.convoy.domain.interactor.LoginInteractor
import com.tompee.convoy.extensions.toFlowable
import com.tompee.convoy.presentation.base.BaseViewModel
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.rxkotlin.Flowables
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class LoginViewModel(private val interactor: LoginInteractor) : BaseViewModel() {
    class Factory(private val interactor: LoginInteractor) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(interactor) as T
        }
    }

    enum class InputState {
        EMAIL_EMPTY,
        EMAIL_INVALID,
        EMAIL_OK,
        PASSWORD_EMPTY,
        PASSWORD_SHORT,
        PASSWORD_OK,
        BOTH_OK
    }

    enum class ProfileState {
        NO_PROFILE,
        COMPLETE
    }

    val switch = MutableLiveData<Unit>()
    val email = ObservableField<String>()
    val password = ObservableField<String>()
    val state = MutableLiveData<InputState>()
    val commandButtonState = MutableLiveData<Boolean>()
    val progressVisibility = MutableLiveData<Boolean>()
    val message = MutableLiveData<String>()
    val registerSuccessful = MutableLiveData<Unit>()
    val profileState = MutableLiveData<ProfileState>()

    init {
        fun getEmailValidation(): Flowable<InputState> {
            return email.toFlowable()
                .map {
                    when {
                        it.isEmpty() -> InputState.EMAIL_EMPTY
                        !Patterns.EMAIL_ADDRESS.matcher(it).matches() -> InputState.EMAIL_INVALID
                        else -> InputState.EMAIL_OK
                    }
                }
        }

        fun getPasswordValidation(): Flowable<InputState> {
            return password.toFlowable()
                .map {
                    when {
                        it.isEmpty() -> InputState.PASSWORD_EMPTY
                        it.length < Constants.MIN_PASS_COUNT -> InputState.PASSWORD_SHORT
                        else -> InputState.PASSWORD_OK
                    }
                }
        }

        subscriptions += getEmailValidation()
            .debounce(1, TimeUnit.SECONDS)
            .subscribe(state::postValue)

        subscriptions += getPasswordValidation()
            .debounce(1, TimeUnit.SECONDS)
            .subscribe(state::postValue)

        subscriptions += Flowables.combineLatest(
            getEmailValidation().startWith(InputState.EMAIL_EMPTY),
            getPasswordValidation().startWith(InputState.PASSWORD_EMPTY)
        ) { email, password ->
            when {
                email != InputState.EMAIL_OK -> email
                password != InputState.PASSWORD_OK -> password
                else -> InputState.BOTH_OK
            }
        }
            .map { it == InputState.BOTH_OK }
            .subscribe(commandButtonState::postValue)
    }

    fun switchPage() {
        switch.postValue(Unit)
    }

    fun login() {
        subscriptions += Single.just(Pair(email.get()!!, password.get()!!))
            .doOnSubscribe { progressVisibility.postValue(true) }
            .flatMap { interactor.login(it.first, it.second) }
            .flatMap {
                interactor.getAccountInfo(it)
                    .map { ProfileState.COMPLETE }
                    .onErrorResumeNext(Single.just(ProfileState.NO_PROFILE))
            }
            .doFinally { progressVisibility.postValue(false) }
            .subscribeOn(Schedulers.io())
            .subscribe(profileState::postValue) { message.postValue(it.message) }
    }

    fun login(token: AccessToken) {
        subscriptions += Single.just(token)
            .flatMap(interactor::login)
            .flatMap {
                interactor.getAccountInfo(it)
                    .map { ProfileState.COMPLETE }
                    .onErrorResumeNext(Single.just(ProfileState.NO_PROFILE))
            }
            .subscribeOn(Schedulers.io())
            .subscribe(profileState::postValue) { message.postValue(it.message) }
    }

    fun login(result: GoogleSignInResult) {
        subscriptions += Single.just(result)
            .flatMap(interactor::login)
            .flatMap {
                interactor.getAccountInfo(it)
                    .map { ProfileState.COMPLETE }
                    .onErrorResumeNext(Single.just(ProfileState.NO_PROFILE))
            }
            .subscribeOn(Schedulers.io())
            .subscribe(profileState::postValue) { message.postValue(it.message) }
    }

    fun register() {
        subscriptions += Single.just(Pair(email.get()!!, password.get()!!))
            .doOnSubscribe { progressVisibility.postValue(true) }
            .flatMapCompletable { interactor.register(it.first, it.second) }
            .doFinally { progressVisibility.postValue(false) }
            .subscribeOn(Schedulers.io())
            .subscribe({ registerSuccessful.postValue(Unit) }) { message.postValue(it.message) }
    }
}