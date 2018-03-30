package com.tompee.convoy.feature.friend

import android.os.Bundle
import com.tompee.convoy.R
import com.tompee.convoy.base.BaseActivity
import kotlinx.android.synthetic.main.toolbar_friends.*

class FriendListActivity : BaseActivity(), FriendListMvpView {
    companion object {
        const val USER_ID = "uid"
    }

    // region View/Presenter setup
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_home)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar_text.text = getString(R.string.friends_label)
    }

    override fun layoutId(): Int = R.layout.activity_friends_list

    override fun setupComponent() {
    }
    // endregion

    // region Observables
    override fun getUserId(): String {
        return intent.getStringExtra(USER_ID)
    }
    // endregion
}