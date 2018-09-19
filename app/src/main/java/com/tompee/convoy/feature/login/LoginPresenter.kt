package com.tompee.convoy.feature.login

import com.tompee.convoy.base.BasePresenter
import com.tompee.convoy.interactor.auth.AuthInteractor
import com.tompee.convoy.interactor.model.User
import com.tompee.convoy.interactor.user.UserInteractor
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable

class LoginPresenter(private val authInteractor: AuthInteractor,
                     private val userInteractor: UserInteractor,
                     private val io: Scheduler,
                     private val ui: Scheduler) : BasePresenter<LoginView>() {


    override fun onAttachView(view: LoginView) {
        addSubscription(authInteractor.getUser()
                .subscribeOn(io)
                .observeOn(ui)
                .subscribe({
                    addSubscription(getUserProfile(Observable.just(it)))
                }, {
                    view.showLoginScreen()
                })
        )
        addSubscription(getUserProfile(view.loginEmail()))
        addSubscription(finishLogin(view.saveUser()))
    }

    private fun getUserProfile(email: Observable<String>): Disposable {
        return email.compose({ observable ->
            observable.flatMapSingle { it -> userInteractor.getUser(it) }
        })
                .subscribeOn(io)
                .observeOn(ui)
                .subscribe({ user ->
                    view?.moveToNextActivity(user.email)
                }, {
                    view?.showProfileSetupScreen(it.message!!)
                })
    }

    private fun finishLogin(user: Observable<User>): Disposable {
        return user
                .subscribeOn(io)
                .observeOn(ui)
                .subscribe({ it ->
                    view?.moveToNextActivity(it.email)
                })
    }

    override fun onDetachView() {
    }
}