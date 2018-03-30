package com.tompee.convoy.feature.search

import com.tompee.convoy.base.BasePresenter
import com.tompee.convoy.feature.search.adapter.UserListAdapter
import com.tompee.convoy.interactor.user.UserInteractor
import io.reactivex.Observable
import io.reactivex.Scheduler

class SearchPresenter(private val userInteractor: UserInteractor,
                      private val io: Scheduler,
                      private val ui: Scheduler) : BasePresenter<SearchMvpView>() {
    override fun onAttachView(view: SearchMvpView) {
        setupSearchField(view.getSearchString())
    }

    override fun onDetachView() {
    }

    private fun setupSearchField(field: Observable<String>) {
        field.flatMapSingle { userInteractor.searchUser(it) }
                .observeOn(ui)
                .subscribe({
                    if (it.isEmpty()) {
                        view?.showEmptyView()
                    } else {
                        view?.showUserList(UserListAdapter(it))
                    }
                })
    }
}