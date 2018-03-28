package com.tompee.convoy.feature.profile

import com.tompee.convoy.base.BasePresenter
import com.tompee.convoy.interactor.user.UserInteractor
import com.tompee.convoy.interactor.user.EmptyDisplayNameException
import com.tompee.convoy.interactor.user.EmptyFirstNameException
import com.tompee.convoy.interactor.user.EmptyLastNameException
import com.tompee.convoy.interactor.model.User
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class ProfilePresenter(private val userInteractor: UserInteractor) : BasePresenter<ProfileMvpView>() {

    fun start(email: String) {
        view?.setEmail(email)
        userInteractor.getUser(email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onUserAvailable, { error ->
                    Timber.e(error.message)
                })
    }

    fun save(firstName: String, lastName: String, displayName: String, email: String) {
        userInteractor.saveUser(firstName, lastName, displayName, email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onUserAvailable, this::onSaveError)
    }

    private fun onUserAvailable(user: User) {
        view?.moveToNextActivity(user.email)
    }

    private fun onSaveError(error: Throwable) {
        when (error) {
            is EmptyFirstNameException -> view?.showFirstNameError()
            is EmptyLastNameException -> view?.showLastNameError()
            is EmptyDisplayNameException -> view?.showDisplayNameError()
        }
    }
}