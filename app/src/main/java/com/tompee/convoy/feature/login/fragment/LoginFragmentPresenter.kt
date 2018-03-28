package com.tompee.convoy.feature.login.fragment

import android.content.Intent
import android.util.Patterns
import com.facebook.login.widget.LoginButton
import com.tompee.convoy.Constants
import com.tompee.convoy.base.BasePresenter
import com.tompee.convoy.interactor.auth.AuthInteractor
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class LoginFragmentPresenter(private val authInteractor: AuthInteractor) : BasePresenter<LoginFragmentMvpView>() {
    companion object {
        private const val EMAIL_EMPTY = 1
        private const val EMAIL_INVALID = 2
        private const val PASSWORD_EMPTY = 3
        private const val PASSWORD_TOO_SHORT = 4
    }

    override fun onAttachView(view: LoginFragmentMvpView) {
        addSubscription(getEmailValidation(view.getEmail())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    Timber.i("Email validation result: $result")
                    when (result) {
                        EMAIL_EMPTY -> view.showEmailEmptyError()
                        EMAIL_INVALID -> view.showEmailInvalidError()
                    }
                }))
        addSubscription(getPasswordValidation(view.getPassword())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    Timber.i("Password validation result: $result")
                    when (result) {
                        PASSWORD_EMPTY -> view.showPasswordEmptyError()
                        PASSWORD_TOO_SHORT -> view.showPasswordTooShortError()
                    }
                })
        )
        addSubscription(getInputValidation(view.getEmail(), view.getPassword())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    view.enableCommand(result)
                }))

        if (view.getType() == LoginFragment.LOGIN) {
            addSubscription(view.loginRequest()
                    .withLatestFrom(getLoginObservable(view.getEmail(), view.getPassword()),
                            BiFunction<Any, Pair<String, String>, Pair<String, String>> { _, loginPair -> loginPair })
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ pair ->
                        startLogin(pair.first, pair.second)
                    })
            )

            view.getFacebookLogin().subscribe({ button ->
                configureFacebookLogin(button)
            })

            addSubscription(view.facebookResult()
                    .subscribe({ triple ->
                        authInteractor.onActivityResult(triple.first, triple.second, triple.third)
                    })
            )

            addSubscription(view.googleSignInRequest().subscribe({
                startGoogleLogin()
            }))

            addSubscription(view.googleResult()
                    .subscribe({ data ->
                        authInteractor.signInGoogle(data)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(this::onFacebookLoginSuccessful, this::onFacebookLoginError)
                    }))
        } else {
            addSubscription(view.signUpRequest()
                    .withLatestFrom(getSignUpObservable(view.getEmail(), view.getPassword()),
                            BiFunction<Any, Pair<String, String>, Pair<String, String>> { _, signUpPair -> signUpPair })
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ pair ->
                        startSignUp(pair.first, pair.second)
                    })
            )
        }
    }

    override fun onDetachView() {
    }

    //region Validation observables
    private fun getEmailValidation(emailObservable: Observable<String>): Observable<Int> {
        return emailObservable
                .map { it.trim() }
                .map { email ->
                    if (email.isEmpty()) {
                        return@map EMAIL_EMPTY
                    }
                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        return@map EMAIL_INVALID
                    }
                    return@map 0
                }
    }

    private fun getPasswordValidation(passwordObservable: Observable<String>): Observable<Int> {
        return passwordObservable
                .map { it.trim() }
                .map { password ->
                    if (password.isEmpty()) {
                        return@map PASSWORD_EMPTY
                    }
                    if (password.length < Constants.MIN_PASS_CHAR) {
                        return@map PASSWORD_TOO_SHORT
                    }
                    return@map 0
                }
    }

    private fun getInputValidation(emailObservable: Observable<String>,
                                   passwordObservable: Observable<String>): Observable<Boolean> {
        return Observable.combineLatest(
                getEmailValidation(emailObservable),
                getPasswordValidation(passwordObservable),
                BiFunction { email, password -> email == 0 && password == 0 }
        )
    }
    //endregion

    // region UserPass Login
    private fun getLoginObservable(emailObservable: Observable<String>,
                                   passwordObservable: Observable<String>): Observable<Pair<String, String>> {
        return Observable.combineLatest(emailObservable,
                passwordObservable,
                BiFunction { email: String, password: String -> Pair(email, password) })
    }

    private fun startLogin(email: String, password: String) {
        view?.showProgressDialog()
        authInteractor.login(email, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onLoginSuccessful, this::onLoginError)
    }

    private fun onLoginSuccessful(email: String) {
        Timber.i("Login email: $email")
        view?.hideProgressDialog()
        view?.moveToNextActivity(email)
    }

    private fun onLoginError(e: Throwable) {
        view?.hideProgressDialog()
        view?.showGenericError(e.message!!)
        Timber.e(e.message)
    }
    // endregion

    //region UserPass Sign Up
    private fun getSignUpObservable(emailObservable: Observable<String>,
                                    passwordObservable: Observable<String>): Observable<Pair<String, String>> {
        return Observable.combineLatest(emailObservable,
                passwordObservable,
                BiFunction { email: String, password: String -> Pair(email, password) })
    }

    private fun startSignUp(email: String, password: String) {
        view?.showProgressDialog()
        authInteractor.signUp(email, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onUserCreated, this::onSignUpError)
    }

    private fun onUserCreated(email: String) {
        Timber.i("New email: $email")
        view?.hideProgressDialog()
        view?.showRegistrationSuccessMessage()
    }

    private fun onSignUpError(e: Throwable) {
        view?.hideProgressDialog()
        view?.showGenericError(e.message!!)
    }
    //endregion

    // region Facebook configuration
    private fun configureFacebookLogin(loginButton: LoginButton) {
        val disposable = authInteractor.configureFacebookLogin(loginButton)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onFacebookLoginSuccessful, this::onFacebookLoginError)
        addSubscription(disposable)
    }

    private fun onFacebookLoginSuccessful(email: String) {
        Timber.i("Login user: $email")
        view?.moveToNextActivity(email)
    }

    private fun onFacebookLoginError(e: Throwable) {
        view?.showGenericError(e.message!!)
        Timber.e(e.message)
    }
    // endregion

    // region Google configuration
    private fun startGoogleLogin() {
        authInteractor.startGoogleLogin()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ intent ->
                    view?.startSignInWithIntent(intent)
                })
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent, isGoogle: Boolean) {
        if (isGoogle) {

        } else {
            authInteractor.onActivityResult(requestCode, resultCode, data)
        }
    }
    // endregion
}