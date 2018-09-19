package com.tompee.convoy.base

import com.tompee.convoy.model.SchedulerPool
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BasePresenterTyped<T : BaseView, V : BaseInteractor>(protected val interactor: V,
                                                                    protected val schedulerPool: SchedulerPool) {
    private val compositeDisposable = CompositeDisposable()
    protected lateinit var view: T

    fun attachView(mvpView: T) {
        view = mvpView
        onAttachView(mvpView)
    }

    fun detachView() {
        compositeDisposable.clear()
        onDetachView()
    }

    protected abstract fun onAttachView(view: T)

    protected abstract fun onDetachView()

    fun addSubscription(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }
}