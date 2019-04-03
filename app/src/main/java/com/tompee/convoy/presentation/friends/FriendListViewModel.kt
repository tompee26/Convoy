package com.tompee.convoy.presentation.friends

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tompee.convoy.R
import com.tompee.convoy.domain.entities.User
import com.tompee.convoy.domain.interactor.FriendInteractor
import com.tompee.convoy.presentation.base.BaseViewModel
import com.tompee.convoy.presentation.common.SectionAdapter
import io.reactivex.rxkotlin.Flowables
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers

class FriendListViewModel(
    private val context: Context,
    interactor: FriendInteractor
) : BaseViewModel() {
    class Factory(
        private val context: Context,
        private val interactor: FriendInteractor
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return FriendListViewModel(context, interactor) as T
        }
    }

    val userList = MutableLiveData<List<User>>()
    val sectionList = MutableLiveData<Array<SectionAdapter.Section>>()

    init {
        subscriptions += Flowables.combineLatest(
            interactor.getFriendsList(),
            interactor.getFriendRequests(),
            interactor.getIncomingFriendRequests()
        )
            .subscribeOn(Schedulers.io())
            .subscribe {
                val list = mutableListOf<User>().apply {
                    addAll(it.first)
                    addAll(it.second)
                    addAll(it.third)
                }
                userList.postValue(list)

                val sections = mutableListOf<SectionAdapter.Section>()
                if (it.first.isNotEmpty()) {
                    sections.add(
                        SectionAdapter.Section(
                            0,
                            context.getString(R.string.friends_label),
                            it.first.size
                        )
                    )
                }
                if (it.second.isNotEmpty()) {
                    sections.add(
                        SectionAdapter.Section(
                            it.first.size,
                            context.getString(R.string.friends_pending_sent),
                            it.second.size
                        )
                    )
                }
                if (it.third.isNotEmpty()) {
                    sections.add(
                        SectionAdapter.Section(
                            it.first.size + it.second.size,
                            context.getString(R.string.friends_pending_received),
                            it.third.size
                        )
                    )
                }
                sectionList.postValue(sections.toTypedArray())
            }
    }
}