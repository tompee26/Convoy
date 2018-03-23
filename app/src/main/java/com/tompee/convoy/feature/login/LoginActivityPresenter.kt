package com.tompee.convoy.feature.login

import com.tompee.convoy.base.BasePresenter
import com.tompee.convoy.interactor.auth.AuthInteractor
import io.reactivex.CompletableObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class LoginActivityPresenter(private val authInteractor: AuthInteractor) :
        BasePresenter<LoginActivityMvpView>() {

    fun checkIfUserLoggedIn() {
        authInteractor.getUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        Timber.i("User is logged in")
                        view?.moveToMainActivity()
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onError(e: Throwable) {
                        Timber.i("No logged in user")
                    }
                })
    }
}