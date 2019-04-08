package com.tompee.convoy.presentation.friends

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.tompee.convoy.R
import com.tompee.convoy.databinding.FragmentFriendListBinding
import com.tompee.convoy.presentation.base.BaseFragment
import com.tompee.convoy.presentation.common.SectionAdapter
import com.tompee.convoy.presentation.friends.profile.ProfileDialog
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class FriendListFragment : BaseFragment<FragmentFriendListBinding>() {

    override val layoutId: Int = R.layout.fragment_friend_list

    @Inject
    lateinit var sectionAdapter: SectionAdapter

    @Inject
    lateinit var friendListAdapter: FriendListAdapter

    @Inject
    lateinit var factory: FriendListViewModel.Factory

    override fun performInject() {
        AndroidSupportInjection.inject(this)
    }

    override fun setupBinding(binding: FragmentFriendListBinding) {
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
            adapter = sectionAdapter
        }

        val vm = ViewModelProviders.of(this, factory)[FriendListViewModel::class.java]
        vm.userList.observe(viewLifecycleOwner, Observer {
            friendListAdapter.setData(it)
        })
        vm.sectionList.observe(viewLifecycleOwner, Observer {
            sectionAdapter.setSections(it)
        })
        friendListAdapter.setOnClickListener {
            val dialog = ProfileDialog.newInstance(it)
            dialog.show(fragmentManager!!, "profile")
        }
        val navigation = findNavController()
        binding.toolbar.back.setOnClickListener { navigation.navigateUp() }
        binding.toolbar.search.setOnClickListener { navigation.navigate(R.id.action_friendListFragment_to_friendSearchFragment) }
    }
}