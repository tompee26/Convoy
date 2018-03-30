package com.tompee.convoy.feature.search

import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.jakewharton.rxbinding2.widget.RxTextView
import com.tompee.convoy.ConvoyApplication
import com.tompee.convoy.R
import com.tompee.convoy.base.BaseActivity
import com.tompee.convoy.dependency.component.DaggerSearchComponent
import com.tompee.convoy.dependency.module.SearchModule
import com.tompee.convoy.feature.search.adapter.UserListAdapter
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.toolbar_search.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SearchActivity : BaseActivity(), SearchMvpView {
    companion object {
        const val USER_ID = "uid"
    }

    @Inject
    lateinit var presenter: SearchPresenter

    // region View/Presenter setup
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_home)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        val divider = DividerItemDecoration(this, layoutManager.orientation)
        recyclerView.addItemDecoration(divider)

        presenter.attachView(this)
    }

    override fun layoutId(): Int = R.layout.activity_search

    override fun setupComponent() {
        val component = DaggerSearchComponent.builder()
                .appComponent(ConvoyApplication[this].component)
                .searchModule(SearchModule())
                .build()
        component.inject(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }
    // endregion

    // region Observables
    override fun getSearchString(): Observable<String> {
        return RxTextView.textChanges(searchView)
                .skipInitialValue()
                .debounce(1, TimeUnit.SECONDS)
                .map { it.toString() }
    }

    // endregion

    // region Interface methods
    override fun showEmptyView() {
        recyclerView.visibility = View.GONE
        emptyView.visibility = View.VISIBLE
    }

    override fun showUserList(adapter: UserListAdapter) {
        recyclerView.adapter = adapter
        emptyView.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
    }

    // endregion
}