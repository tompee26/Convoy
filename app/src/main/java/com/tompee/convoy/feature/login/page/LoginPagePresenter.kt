package com.tompee.convoy.feature.login.page

import android.util.Patterns
import com.tompee.convoy.Constants
import com.tompee.convoy.R
import com.tompee.convoy.base.BasePresenterTyped
import com.tompee.convoy.core.navigator.Navigator
import com.tompee.convoy.interactor.LoginInteractor
import com.tompee.convoy.model.Account
import com.tompee.convoy.model.SchedulerPool
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.withLatestFrom
import java.util.concurrent.TimeUnit

class LoginPagePresenter(loginInteractor: LoginInteractor,
                         schedulerPool: SchedulerPool,
                         private val navigator: Navigator) :
        BasePresenterTyped<LoginPageView, LoginInteractor>(loginInteractor, schedulerPool) {
    enum class InputError {
        EMAIL_EMPTY,
        EMAIL_INVALID,
        EMAIL_OK,
        PASSWORD_EMPTY,
        PASSWORD_SHORT,
        PASSWORD_OK,
        BOTH_OK
    }

    override fun onAttachView(view: LoginPageView) {
        val inputError = validateInputs()
        setupCommandHandler(inputError)

        setupFacebookLogin()
        setupGoogleLogin()
    }

    override fun onDetachView() {
    }

    private fun validateInputs(): Observable<InputError> {
        val emailError = view.getEmail()
                .map {
                    when {
                        it.isEmpty() -> InputError.EMAIL_EMPTY
                        !Patterns.EMAIL_ADDRESS.matcher(it).matches() -> InputError.EMAIL_INVALID
                        else -> InputError.EMAIL_OK
                    }
                }
        val passError = view.getPassword()
                .map {
                    when {
                        it.isEmpty() -> InputError.PASSWORD_EMPTY
                        it.length < Constants.MIN_PASS_COUNT -> InputError.PASSWORD_SHORT
                        else -> InputError.PASSWORD_OK
                    }
                }

        addSubscription(emailError
                .debounce(1, TimeUnit.SECONDS)
                .observeOn(schedulerPool.main)
                .subscribe(this::showError))
        addSubscription(passError
                .debounce(1, TimeUnit.SECONDS)
                .observeOn(schedulerPool.main)
                .subscribe(this::showError))

        return Observables.combineLatest(
                Observable.just(InputError.EMAIL_EMPTY).concatWith(emailError),
                Observable.just(InputError.PASSWORD_EMPTY).concatWith(passError)) { email, pass ->
            when {
                email != InputError.EMAIL_OK -> email
                pass != InputError.PASSWORD_OK -> pass
                else -> InputError.BOTH_OK
            }
        }
    }

    private fun setupCommandHandler(inputError: Observable<InputError>) {
        val command = view.command().withLatestFrom(inputError) { _, error -> error }
                .observeOn(schedulerPool.main)
                .doOnNext(this::showError)
                .filter { it == InputError.BOTH_OK }
                .doOnNext { view.showProgressDialog() }
                .withLatestFrom(view.getEmail(), view.getPassword()) { _, email, pass -> Pair(email, pass) }
        if (view.getViewType() == LoginPageFragment.SIGN_UP) {
            addSubscription(command.flatMapCompletable { pair ->
                interactor.signup(pair.first, pair.second)
                        .observeOn(schedulerPool.main)
                        .doOnComplete(view::showSignupSuccessMessage)
                        .doOnError { view.showError(it.message ?: "Error occurred") }
                        .onErrorComplete()
                        .doOnComplete(view::dismissProgressDialog)
                        .subscribeOn(schedulerPool.io)
            }.subscribe())
        } else {
            addSubscription(command.flatMapSingle { pair -> observeLogin(interactor.login(pair.first, pair.second)) }.subscribe())
        }
    }

    private fun observeLogin(single: Single<String>): Single<Account> {
        return single.observeOn(schedulerPool.main)
                .doOnError {
                    view.showError(it.message ?: "Error occurred")
                    view.dismissProgressDialog()
                }
                .flatMap { email ->
                    interactor.getUserInfo(email)
                            .observeOn(schedulerPool.main)
                            .doOnSuccess { navigator.popUp(R.id.action_loginFragment_to_mapFragment, R.id.mapFragment) }
                            .doOnError { navigator.popUp(R.id.action_loginFragment_to_profileSetupFragment, R.id.loginFragment) }
                            .subscribeOn(schedulerPool.io)
                }
                .onErrorResumeNext(Single.just(Account("", false, "", "", "", "")))
                .subscribeOn(schedulerPool.io)
    }

    private fun setupFacebookLogin() {
        view.loginWithFacebook()
                .retry()
                .flatMap { observeLogin(interactor.loginWithFacebook(it)) }
                .subscribe()
    }

    private fun setupGoogleLogin() {
        view.loginWithGoogle()
                .retry()
                .flatMapSingle { observeLogin(interactor.loginWithGoogle(it)) }
                .subscribe()
    }

    private fun showError(error: InputError) = when (error) {
        InputError.EMAIL_EMPTY -> view.showEmailEmptyError()
        InputError.EMAIL_INVALID -> view.showEmailInvalidError()
        InputError.EMAIL_OK -> view.clearEmailError()
        InputError.PASSWORD_EMPTY -> view.showPasswordEmptyError()
        InputError.PASSWORD_SHORT -> view.showPasswordTooShortError()
        InputError.PASSWORD_OK -> view.clearEmailError()
        else -> {
            view.clearEmailError()
            view.clearPasswordError()
        }
    }
}