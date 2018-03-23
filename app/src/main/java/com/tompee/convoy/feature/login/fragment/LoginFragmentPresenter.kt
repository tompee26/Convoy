package com.tompee.convoy.feature.login.fragment

import android.content.Intent
import com.facebook.login.widget.LoginButton
import com.google.android.gms.common.SignInButton
import com.tompee.convoy.base.BasePresenter
import com.tompee.convoy.interactor.auth.AuthInteractor
import com.tompee.convoy.interactor.auth.model.User
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class LoginFragmentPresenter
@Inject constructor(private val authInteractor: AuthInteractor) : BasePresenter<LoginFragmentMvpView>() {

    fun startSignUp(email: String, password: String) {
        view?.showProgressDialog()
        authInteractor.signUp(email, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<User> {
                    override fun onSuccess(user: User) {
                        Timber.i("Created user: " + user.name)
                        view?.hideProgressDialog()
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onError(e: Throwable) {
                        view?.hideProgressDialog()
                        view?.showRegistrationFailedError(e.message!!)
                        Timber.e(e.message)
                    }
                })
    }

    fun startLogin(email: String, password: String) {
//        try {
//            authInteractor.login(email, password, object : AuthInteractor.OnLoginFinishedListener {
//                override fun onSuccess() {
//                }
//
//                override fun onError(error: AuthException) {
//                }
//            })
//            view?.showProgressDialog()
//        } catch (emailEmptyException: EmailEmptyException) {
//            view?.showEmailEmptyError()
//        } catch (invalidEmailFormatException: InvalidEmailFormatException) {
//            view?.showEmailInvalidError()
//        } catch (passwordEmptyException: PasswordEmptyException) {
//            view?.showPasswordEmptyError()
//        } catch (passwordTooShortException: PasswordTooShortException) {
//            view?.showPasswordTooShortError()
//        }
    }

    fun configureFacebookLogin(loginButton: LoginButton) {
//        authInteractor.configureFacebookLogin(loginButton, object : AuthInteractor.OnLoginFinishedListener {
//            override fun onSuccess() {
//            }
//
//            override fun onError(error: AuthException) {
//            }
//        })
    }

    fun configureGoogleLogin(signInButton: SignInButton) {
//        authInteractor.configureGoogleLogin(signInButton, object : AuthInteractor.OnLoginFinishedListener {
//            override fun onSuccess() {
//            }
//
//            override fun onError(error: AuthException) {
//            }
//        })
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
//        authInteractor.onActivityResult(requestCode, resultCode, data)
    }
}