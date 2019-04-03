package com.tompee.convoy.presentation.friends.search

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.tompee.convoy.R
import com.tompee.convoy.databinding.FragmentFriendSearchBinding
import com.tompee.convoy.presentation.base.BaseFragment
import com.tompee.convoy.presentation.friends.profile.ProfileDialog
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class FriendSearchFragment : BaseFragment<FragmentFriendSearchBinding>() {

    @Inject
    lateinit var factory: FriendSearchViewModel.Factory

    @Inject
    lateinit var userListAdapter: FriendSearchAdapter

    override val layoutId: Int = R.layout.fragment_friend_search

    override fun performInject() {
        AndroidSupportInjection.inject(this)
    }

    override fun setupBinding(binding: FragmentFriendSearchBinding) {
        val vm = ViewModelProviders.of(this, factory)[FriendSearchViewModel::class.java]
        binding.viewModel = vm

        userListAdapter.onClick.observe(this, Observer {
            val dialog = ProfileDialog.newInstance(it)
            dialog.show(fragmentManager!!, "profile")
        })
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = userListAdapter
        }
    }
}