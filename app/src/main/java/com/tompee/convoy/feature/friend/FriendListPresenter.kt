package com.tompee.convoy.feature.friend

import android.content.Context
import com.tompee.convoy.R
import com.tompee.convoy.base.BasePresenter
import com.tompee.convoy.feature.friend.adapter.FriendListAdapter
import com.tompee.convoy.feature.friend.adapter.SectionAdapter
import com.tompee.convoy.interactor.model.User
import com.tompee.convoy.interactor.user.UserInteractor
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.functions.Function3

class FriendListPresenter(private val context: Context,
                          private val userInteractor: UserInteractor,
                          private val io: Scheduler,
                          private val ui: Scheduler) : BasePresenter<FriendListMvpView>() {
    override fun onAttachView(view: FriendListMvpView) {
        setupFriendList(view)
    }

    private fun setupFriendList(view: FriendListMvpView) {
        Observable.combineLatest(
                userInteractor.getFriendsListPersistent(view.getEmail()),
                userInteractor.getIncomingRequestPersistent(view.getEmail()),
                userInteractor.getOutgoingRequestPersistent(view.getEmail()),
                Function3<List<User>, List<User>, List<User>, Triple<List<User>, List<User>, List<User>>> { friends, incoming, outgoing ->
                    Triple(incoming, outgoing, friends)
                }
        )
                .subscribeOn(io)
                .observeOn(ui)
                .subscribe({
                    val combinedList = mutableListOf<User>()
                    combinedList.addAll(it.first)
                    combinedList.addAll(it.second)
                    combinedList.addAll(it.third)

                    val sectionList = mutableListOf<SectionAdapter.Section>()
                    if (it.first.isNotEmpty()) {
                        sectionList.add(SectionAdapter.Section(0, context.getString(R.string.friends_pending_received), it.first.size))
                    }
                    if (it.second.isNotEmpty()) {
                        sectionList.add(SectionAdapter.Section(it.first.size, context.getString(R.string.friends_pending_sent), it.second.size))
                    }
                    if (it.third.isNotEmpty()) {
                        sectionList.add(SectionAdapter.Section(it.first.size + it.second.size, context.getString(R.string.friends_label), it.third.size))
                    }

                    val sectionedAdapter = SectionAdapter(FriendListAdapter(combinedList))
                    sectionedAdapter.setSections(sectionList.toTypedArray())
                    view.setList(sectionedAdapter)
                })
    }

    override fun onDetachView() {
    }
}