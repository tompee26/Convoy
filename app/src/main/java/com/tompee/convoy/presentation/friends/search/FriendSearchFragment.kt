package com.tompee.convoy.presentation.friends.search

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
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

    override fun onDetach() {
        super.onDetach()
        activity?.currentFocus.let {
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(it?.windowToken, 0)
        }
    }

    override fun setupBinding(binding: FragmentFriendSearchBinding) {
        val vm = ViewModelProviders.of(this, factory)[FriendSearchViewModel::class.java]
        binding.viewModel = vm

        userListAdapter.onClick.observe(viewLifecycleOwner, Observer {
            val dialog = ProfileDialog.newInstance(it)
            dialog.show(fragmentManager!!, "profile")
        })
        val navigation = findNavController()
        binding.toolbar.back.setOnClickListener { navigation.navigateUp() }
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = userListAdapter
        }
    }
}