package com.tompee.convoy.base

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BasePresenter<T : BaseView> {
    private val compositeDisposable = CompositeDisposable()
    protected var view: T? = null
        private set

    fun attachView(mvpView: T) {
        view = mvpView
        onAttachView(mvpView)
    }

    fun detachView() {
        compositeDisposable.clear()
        onDetachView()
        view = null
    }

    protected abstract fun onAttachView(view: T)

    protected abstract fun onDetachView()

    fun addSubscription(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }
}