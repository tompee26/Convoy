package com.tompee.convoy.feature.search.profile

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import com.tompee.convoy.R
import com.tompee.convoy.base.BasePresenter
import com.tompee.convoy.interactor.user.UserInteractor
import io.reactivex.Completable
import io.reactivex.Scheduler

class ProfileDialogPresenter(private val context: Context,
                             private val userInteractor: UserInteractor,
                             private val io: Scheduler,
                             private val ui: Scheduler) : BasePresenter<ProfileDialogMvpView>() {
    override fun onAttachView(view: ProfileDialogMvpView) {
        setupUser(view.getTargetEmail())
        setupAddFriend(view)
        setupAcceptRequest(view)
        setupFromOutgoing(view.getUserEmail(), view.getTargetEmail())
    }

    private fun setupFromOutgoing(own: String, target: String) {
        userInteractor.findUserInOutgoingFriendRequest(own, target)
                .doOnComplete {
                    view?.showCustomMessage(context.getString(R.string.profile_label_sent))
                }
                .doOnError {
                    setupFromIncoming(own, target)
                }
                .onErrorResumeNext {
                    return@onErrorResumeNext Completable.complete()
                }
                .subscribeOn(io)
                .observeOn(ui)
                .subscribe()
    }

    private fun setupFromIncoming(own: String, target: String) {
        userInteractor.findUserInIncomingFriendRequest(own, target)
                .doOnComplete {
                    view?.showAcceptRequest()
                }
                .doOnError {
                    view?.showAddFriend()
                }
                .onErrorResumeNext {
                    return@onErrorResumeNext Completable.complete()
                }
                .subscribeOn(io)
                .observeOn(ui)
                .subscribe()
    }

    private fun setupUser(email: String) {
        addSubscription(userInteractor.getUser(email)
                .compose { observable ->
                    observable.map { user ->
                        val decoded = Base64.decode(user.image, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(decoded, 0, decoded.size)
                        return@map Pair(user, bitmap)
                    }
                }
                .subscribeOn(io)
                .observeOn(ui)
                .subscribe({ pair ->
                    view?.setProfile(pair.first, pair.second)
                })
        )
    }

    private fun setupAddFriend(view: ProfileDialogMvpView) {
        addSubscription(view.addFriendRequest()
                .subscribeOn(ui)
                .observeOn(ui)
                .subscribe({ pair ->
                    view.showProgress()
                    startAddRequest(pair.first, pair.second)
                })
        )
    }

    private fun setupAcceptRequest(view: ProfileDialogMvpView) {
        addSubscription(view.acceptRequest()
                .subscribeOn(ui)
                .observeOn(ui)
                .subscribe({ pair ->
                    view.showProgress()
                    startAcceptRequest(pair.first, pair.second)
                }))
    }

    private fun startAddRequest(own: String, target: String) {
        userInteractor.addFriendRequest(own, target)
                .subscribeOn(io)
                .observeOn(ui)
                .subscribe({
                    view?.showCustomMessage(context.getString(R.string.profile_label_sent))
                })
    }

    private fun startAcceptRequest(own: String, target: String) {
        userInteractor.acceptFriendRequest(own, target)
                .subscribeOn(io)
                .observeOn(ui)
                .subscribe({
                    view?.showCustomMessage(context.getString(R.string.profile_label_accepted))
                })
    }

    override fun onDetachView() {
    }
}