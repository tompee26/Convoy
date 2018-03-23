package com.tompee.convoy.feature.login

import android.content.Intent
import com.facebook.login.widget.LoginButton
import com.google.android.gms.common.SignInButton
import com.tompee.convoy.base.BasePresenter
import com.tompee.convoy.interactor.auth.*
import javax.inject.Inject

class LoginPresenter @Inject constructor(private val authInteractor: AuthInteractor) : BasePresenter<LoginMvpView>() {

    fun startSignUp(email: String, password: String) {
        try {
            authInteractor.register(email, password, object : AuthInteractor.OnLoginFinishedListener {
                override fun onSuccess() {
                }

                override fun onError(error: AuthException) {
                }
            })
            mvpView?.showProgressDialog()
        } catch (emailEmptyException: EmailEmptyException) {
            mvpView?.showEmailEmptyError()
        } catch (invalidEmailFormatException: InvalidEmailFormatException) {
            mvpView?.showEmailInvalidError()
        } catch (passwordEmptyException: PasswordEmptyException) {
            mvpView?.showPasswordEmptyError()
        } catch (passwordTooShortException: PasswordTooShortException) {
            mvpView?.showPasswordTooShortError()
        }
    }

    fun startLogin(email: String, password: String) {
        try {
            authInteractor.login(email, password, object : AuthInteractor.OnLoginFinishedListener {
                override fun onSuccess() {
                }

                override fun onError(error: AuthException) {
                }
            })
            mvpView?.showProgressDialog()
        } catch (emailEmptyException: EmailEmptyException) {
            mvpView?.showEmailEmptyError()
        } catch (invalidEmailFormatException: InvalidEmailFormatException) {
            mvpView?.showEmailInvalidError()
        } catch (passwordEmptyException: PasswordEmptyException) {
            mvpView?.showPasswordEmptyError()
        } catch (passwordTooShortException: PasswordTooShortException) {
            mvpView?.showPasswordTooShortError()
        }
    }

    fun configureFacebookLogin(loginButton: LoginButton) {
        authInteractor.configureFacebookLogin(loginButton, object : AuthInteractor.OnLoginFinishedListener {
            override fun onSuccess() {
            }

            override fun onError(error: AuthException) {
            }
        })
    }

    fun configureGoogleLogin(signInButton: SignInButton) {
        authInteractor.configureGoogleLogin(signInButton, object : AuthInteractor.OnLoginFinishedListener {
            override fun onSuccess() {
            }

            override fun onError(error: AuthException) {
            }
        })
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        authInteractor.onActivityResult(requestCode, resultCode, data)
    }
}