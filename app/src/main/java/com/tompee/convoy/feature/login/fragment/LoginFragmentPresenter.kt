package com.tompee.convoy.feature.login.fragment

import android.content.Intent
import com.facebook.login.widget.LoginButton
import com.tompee.convoy.base.BasePresenter
import com.tompee.convoy.interactor.auth.*
import com.tompee.convoy.interactor.model.User
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class LoginFragmentPresenter(private val authInteractor: AuthInteractor) : BasePresenter<LoginFragmentMvpView>() {

    fun startSignUp(email: String, password: String) {
        view?.showProgressDialog()
        authInteractor.signUp(email, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onUserCreated, this::onSignUpError)
    }

    fun startLogin(email: String, password: String) {
        view?.showProgressDialog()
        authInteractor.login(email, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onLoginSuccessful, this::onLoginError)
    }

    fun configureFacebookLogin(loginButton: LoginButton) {
        val disposable = authInteractor.configureFacebookLogin(loginButton)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onFacebookLoginSuccessful, this::onFacebookLoginError)
        addSubscription(disposable)
    }

    fun startGoogleLogin() {
        authInteractor.startGoogleLogin()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ intent ->
                    view?.startSignInWithIntent(intent)
                })
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent, isGoogle: Boolean) {
        if (isGoogle) {
            authInteractor.signInGoogle(data)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::onFacebookLoginSuccessful, this::onFacebookLoginError)
        } else {
            authInteractor.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun onUserCreated(user: User) {
        Timber.i("Created user: " + user.email)
        view?.hideProgressDialog()
        view?.showRegistrationSuccessMessage()
    }

    private fun onSignUpError(e: Throwable) {
        view?.hideProgressDialog()
        when (e) {
            is EmailEmptyException -> view?.showEmailEmptyError()
            is InvalidEmailFormatException -> view?.showEmailInvalidError()
            is PasswordEmptyException -> view?.showPasswordEmptyError()
            is PasswordTooShortException -> view?.showPasswordTooShortError()
            else -> {
                view?.showGenericError(e.message!!)
                Timber.e(e.message)
            }
        }
    }

    private fun onLoginSuccessful(user: User) {
        Timber.i("Login user: " + user.email)
        view?.hideProgressDialog()
        view?.moveToMainActivity()
    }

    private fun onLoginError(e: Throwable) {
        view?.hideProgressDialog()
        view?.showGenericError(e.message!!)
        Timber.e(e.message)
    }

    private fun onFacebookLoginSuccessful(user: User) {
        Timber.i("Login user: " + user.email)
        view?.moveToMainActivity()
    }

    private fun onFacebookLoginError(e: Throwable) {
        view?.showGenericError(e.message!!)
        Timber.e(e.message)
    }
}