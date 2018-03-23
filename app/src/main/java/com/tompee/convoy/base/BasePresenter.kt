package com.tompee.convoy.base

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class BasePresenter<T : MvpView> {

    private val compositeDisposable = CompositeDisposable()
    var mvpView: T? = null
        private set

    fun attachView(mvpView: T) {
        this.mvpView = mvpView
    }

    fun detachView() {
        compositeDisposable.clear()
        mvpView = null
    }

    fun addSubscription(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }
}