package com.tompee.convoy.feature.friend

import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.tompee.convoy.ConvoyApplication
import com.tompee.convoy.R
import com.tompee.convoy.base.BaseActivity
import com.tompee.convoy.dependency.component.DaggerFriendComponent
import com.tompee.convoy.dependency.module.FriendModule
import com.tompee.convoy.feature.friend.adapter.SectionAdapter
import kotlinx.android.synthetic.main.activity_friends_list.*
import kotlinx.android.synthetic.main.toolbar_friends.*
import javax.inject.Inject

class FriendListActivity : BaseActivity(), FriendListMvpView {
    companion object {
        const val EMAIL = "email"
    }

    @Inject
    lateinit var presenter: FriendListPresenter

    // region View/Presenter setup
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_home)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar_text.text = getString(R.string.friends_label)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))

        presenter.attachView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun layoutId(): Int = R.layout.activity_friends_list

    override fun setupComponent() {
        val component = DaggerFriendComponent.builder()
                .appComponent(ConvoyApplication[this].component)
                .friendModule(FriendModule())
                .build()
        component.inject(this)
    }
    // endregion

    // region Observables
    override fun getEmail(): String {
        return intent.getStringExtra(EMAIL)
    }
    // endregion

    // region Interface methods
    override fun setList(sectionedAdapter: SectionAdapter) {
        recyclerView.adapter = sectionedAdapter
    }
    // endregion
}