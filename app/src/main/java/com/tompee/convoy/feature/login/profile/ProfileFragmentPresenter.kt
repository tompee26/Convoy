package com.tompee.convoy.feature.login.profile

import com.tompee.convoy.base.BasePresenter
import com.tompee.convoy.interactor.user.UserInteractor
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function3
import timber.log.Timber

class ProfileFragmentPresenter(private val userInteractor: UserInteractor,
                               private val io: Scheduler,
                               private val ui: Scheduler) : BasePresenter<ProfileFragmentMvpView>() {

    override fun onAttachView(view: ProfileFragmentMvpView) {
        addSubscription(getTextValidation(view.getFirstName()
                .subscribeOn(ui)
                .observeOn(ui))
                .subscribe({
                    if (!it) {
                        view.showFirstNameError()
                    }
                }))
        addSubscription(getTextValidation(view.getLastName()
                .subscribeOn(ui)
                .observeOn(ui))
                .subscribe({
                    if (!it) {
                        view.showLastNameError()
                    }
                }))
        addSubscription(getTextValidation(view.getDisplayName()
                .subscribeOn(ui)
                .observeOn(ui))
                .subscribe({
                    if (!it) {
                        view.showDisplayNameError()
                    }
                }))
        addSubscription(getInputValidation(view.getFirstName(), view.getLastName(), view.getDisplayName())
                .subscribeOn(ui)
                .observeOn(ui)
                .subscribe({
                    view.setSaveButtonState(it)
                })
        )
        addSubscription(view.saveRequest()
                .withLatestFrom(getInputObservable(view.getFirstName(), view.getLastName(), view.getDisplayName()),
                        BiFunction<Any, Triple<String, String, String>, Triple<String, String, String>> { _, triple -> triple })
                .subscribeOn(ui)
                .observeOn(ui)
                .subscribe({
                    saveProfile(it.first, it.second, it.third)
                })
        )
    }

    override fun onDetachView() {
    }

    private fun getTextValidation(textObservable: Observable<String>): Observable<Boolean> {
        return textObservable
                .map { email ->
                    if (email.isEmpty()) {
                        return@map false
                    }
                    return@map true
                }
    }

    private fun getInputValidation(firstNameObservable: Observable<String>,
                                   lastNameObservable: Observable<String>,
                                   displayNameObservable: Observable<String>): Observable<Boolean> {
        return Observable.combineLatest(getTextValidation(firstNameObservable),
                getTextValidation(lastNameObservable),
                getTextValidation(displayNameObservable),
                Function3<Boolean, Boolean, Boolean, Boolean> { first: Boolean, last: Boolean, display: Boolean ->
                    first && last && display
                })
    }

    private fun getInputObservable(first: Observable<String>,
                                   last: Observable<String>,
                                   display: Observable<String>): Observable<Triple<String, String, String>> {
        return Observable.combineLatest(first, last, display,
                Function3 { a: String, b: String, c: String -> Triple(a, b, c) })
    }

    private fun saveProfile(first: String, last: String, display: String) {
        userInteractor.saveUser(first, last, display, view?.getEmail()!!)
                .subscribeOn(io)
                .observeOn(ui)
                .subscribe({ user ->
                    view?.saveSuccessful(user)
                }, {
                    Timber.e(it.message)
                })
    }
}