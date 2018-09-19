package com.tompee.convoy.feature.splash

import com.tompee.convoy.base.BasePresenterTyped
import com.tompee.convoy.core.navigator.Navigator
import com.tompee.convoy.feature.login.LoginActivity
import com.tompee.convoy.interactor.SplashInteractor
import com.tompee.convoy.model.SchedulerPool
import timber.log.Timber

class SplashPresenter(splashInteractor: SplashInteractor,
                      schedulerPool: SchedulerPool,
                      private val navigator: Navigator) :
        BasePresenterTyped<SplashView, SplashInteractor>(splashInteractor, schedulerPool) {

    override fun onAttachView(view: SplashView) {
        val subscription = interactor.getCurrentUser()
                .observeOn(schedulerPool.main)
                .doOnError { navigator.moveToScreen(LoginActivity::class.java) }
                .observeOn(schedulerPool.io)
                .flatMap { email ->
                    interactor.getUserInfo(email)
                            .observeOn(schedulerPool.main)
                            .doOnSuccess { /*view.moveToTimelineScreen()*/ }
                            .doOnError { /*view.moveToProfileScreen()*/ }
                            .subscribeOn(schedulerPool.io)
                }
                .subscribeOn(schedulerPool.io)
                .subscribe({ Timber.d(it.toString()) }, Timber::e)
        addSubscription(subscription)
    }

    override fun onDetachView() {
    }
}