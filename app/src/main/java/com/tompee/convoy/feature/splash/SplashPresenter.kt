package com.tompee.convoy.feature.splash

import com.tompee.convoy.R
import com.tompee.convoy.base.BasePresenterTyped
import com.tompee.convoy.core.navigator.Navigator
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
                .doOnError { navigator.popUp(R.id.action_splashFragment_to_loginFragment, R.id.splashFragment) }
                .observeOn(schedulerPool.io)
                .flatMap { email ->
                    interactor.getUserInfo(email)
                            .observeOn(schedulerPool.main)
                            .doOnSuccess { navigator.popUp(R.id.action_splashFragment_to_mapFragment, R.id.splashFragment) }
                            .doOnError { navigator.popUp(R.id.action_splashFragment_to_profileSetupFragment, R.id.splashFragment) }
                            .subscribeOn(schedulerPool.io)
                }
                .subscribeOn(schedulerPool.io)
                .subscribe({ Timber.d(it.toString()) }, Timber::e)
        addSubscription(subscription)
    }

    override fun onDetachView() {
    }
}