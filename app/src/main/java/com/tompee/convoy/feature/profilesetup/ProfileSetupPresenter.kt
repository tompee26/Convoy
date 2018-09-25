package com.tompee.convoy.feature.profilesetup

import com.tompee.convoy.base.BasePresenterTyped
import com.tompee.convoy.core.navigator.Navigator
import com.tompee.convoy.feature.map.MapActivity
import com.tompee.convoy.interactor.ProfileInteractor
import com.tompee.convoy.model.Account
import com.tompee.convoy.model.SchedulerPool
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.withLatestFrom
import timber.log.Timber
import java.util.concurrent.TimeUnit

class ProfileSetupPresenter(profileInteractor: ProfileInteractor,
                            schedulerPool: SchedulerPool,
                            private val navigator: Navigator) :
        BasePresenterTyped<ProfileSetupView, ProfileInteractor>(profileInteractor, schedulerPool) {

    override fun onAttachView(view: ProfileSetupView) {
        setupFieldValidation()
        setupSave()
    }

    override fun onDetachView() {
    }

    private fun setupFieldValidation() {
        fun decorateObservable(observable: Observable<String>): Observable<String> {
            return observable
                    .debounce(1, TimeUnit.SECONDS)
                    .filter { it.isEmpty() }
                    .observeOn(schedulerPool.main)
        }
        decorateObservable(view.getFirstName())
                .subscribe { view.showEmptyFirstNameError() }
        decorateObservable(view.getLastName())
                .subscribe { view.showEmptyLastNameError() }
        decorateObservable(view.getDisplayName())
                .subscribe { view.showEmptyDisplayNameError() }
    }

    private fun setupSave() {
        val subscription = view.saveRequest()
                .withLatestFrom(getAccount()) { _, account -> account }
                .observeOn(schedulerPool.main)
                .filter(::verifyAccount)
                .doOnNext { view.showProgress() }
                .observeOn(schedulerPool.computation)
                .flatMapCompletable { account ->
                    interactor.saveAccount(account)
                            .observeOn(schedulerPool.main)
                            .doOnComplete { navigator.moveToScreen(MapActivity::class.java) }
                            .doOnError { view.showError(it.message ?: "Error saving profile") }
                            .onErrorComplete()
                            .doOnComplete { view.dismissProgress() }
                }
                .subscribe({ Timber.d("Completed") }, Timber::e)
        addSubscription(subscription)
    }

    private fun getAccount(): Observable<Account> {
        val firstName = Observable.just("").concatWith(view.getFirstName())
        val lastName = Observable.just("").concatWith(view.getLastName())
        val displayName = Observable.just("").concatWith(view.getDisplayName())
        val image = Observable.just("").concatWith(view.getImageUrl())
        return Observables.combineLatest(interactor.getEmail().toObservable(), firstName, lastName, displayName, image) { e, f, l, d, i ->
            Account(e, true, f, l, d, i)
        }
    }

    private fun verifyAccount(account: Account): Boolean {
        if (account.firstName.isEmpty()) {
            view.showEmptyFirstNameError()
            return false
        }
        if (account.lastName.isEmpty()) {
            view.showEmptyLastNameError()
            return false
        }
        if (account.displayName.isEmpty()) {
            view.showEmptyDisplayNameError()
            return false
        }
        return true
    }
}