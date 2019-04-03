package com.tompee.convoy.presentation.friends.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tompee.convoy.domain.interactor.FriendInteractor
import com.tompee.convoy.presentation.base.BaseViewModel
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers

class ProfileDialogViewModel(private val interactor: FriendInteractor) : BaseViewModel() {

    class Factory(private val interactor: FriendInteractor) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return ProfileDialogViewModel(interactor) as T
        }
    }

    enum class RelationshipState {
        ALREADY_FRIENDS,
        REQUEST_SENT,
        REQUEST_RECEIVED,
        NO_RELATIONSHIP
    }

    val name = MutableLiveData<String>()
    val displayName = MutableLiveData<String>()
    val image = MutableLiveData<String>()
    val state = MutableLiveData<RelationshipState>()
    val progress = MutableLiveData<Boolean>()

    fun load(email: String) {
        progress.postValue(true)
        subscriptions += interactor.findUser(email)
            .subscribeOn(Schedulers.io())
            .subscribe { user ->
                name.postValue("${user.firstName} ${user.lastName}")
                displayName.postValue(user.displayName)
                image.postValue(user.image)
            }

        subscriptions += interactor.findInFriends(email)
            .doOnComplete { state.postValue(RelationshipState.ALREADY_FRIENDS) }
            .onErrorResumeNext {
                interactor.findInSentRequest(email)
                    .doOnComplete { state.postValue(RelationshipState.REQUEST_SENT) }
            }
            .onErrorResumeNext {
                interactor.findInReceivedRequest(email)
                    .doOnComplete { state.postValue(RelationshipState.REQUEST_RECEIVED) }
            }
            .subscribe({}) { state.postValue(RelationshipState.NO_RELATIONSHIP) }
    }

    fun addFriend(email: String) {
        progress.postValue(true)
        subscriptions += interactor.sendFriendRequest(email)
            .subscribe { state.postValue(RelationshipState.REQUEST_SENT) }
    }

    fun acceptFriendRequest(email: String) {
        progress.postValue(true)
        subscriptions += interactor.acceptFriendRequest(email)
            .subscribe { state.postValue(RelationshipState.ALREADY_FRIENDS) }
    }

    fun rejectFriendRequest(email: String) {
        progress.postValue(true)
        subscriptions += interactor.rejectFriendRequest(email)
            .subscribe { state.postValue(RelationshipState.NO_RELATIONSHIP) }
    }
}