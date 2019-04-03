package com.tompee.convoy.presentation.friends.search

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tompee.convoy.domain.entities.User
import com.tompee.convoy.domain.interactor.FriendInteractor
import com.tompee.convoy.extensions.toFlowable
import com.tompee.convoy.presentation.base.BaseViewModel
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class FriendSearchViewModel(private val interactor: FriendInteractor) : BaseViewModel() {
    class Factory(private val interactor: FriendInteractor) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return FriendSearchViewModel(interactor) as T
        }
    }

    val searchText = ObservableField<String>()
    val userList = MutableLiveData<List<User>>()

    init {
        userList.postValue(emptyList())
        subscriptions += searchText.toFlowable()
            .debounce(300, TimeUnit.MILLISECONDS)
            .flatMapSingle {
                interactor.searchUser(it)
                    .subscribeOn(Schedulers.io())
            }
            .subscribe(userList::postValue)
    }
}