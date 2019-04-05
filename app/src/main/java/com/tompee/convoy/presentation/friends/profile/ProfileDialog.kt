package com.tompee.convoy.presentation.friends.profile

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.tompee.convoy.R
import com.tompee.convoy.databinding.FragmentFriendProfileBinding
import com.tompee.convoy.presentation.base.BaseDialogFragment
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class ProfileDialog : BaseDialogFragment() {

    companion object {
        private const val EMAIL = "email"

        fun newInstance(email: String): ProfileDialog {
            val dialog = ProfileDialog()
            dialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.FragmentDialog)
            val bundle = Bundle()
            bundle.putString(EMAIL, email)
            dialog.arguments = bundle
            return dialog
        }
    }

    @Inject
    lateinit var factory: ProfileDialogViewModel.Factory

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding: FragmentFriendProfileBinding =
            DataBindingUtil.inflate(activity?.layoutInflater!!, R.layout.fragment_friend_profile, null, false)
        val vm = ViewModelProviders.of(this, factory)[ProfileDialogViewModel::class.java]
        binding.lifecycleOwner = this
        binding.viewModel = vm

        vm.state.observe(viewLifecycleOwner, Observer {
            when (it) {
                ProfileDialogViewModel.RelationshipState.ALREADY_FRIENDS -> {
                    binding.apply {
                        addFriend.visibility = View.INVISIBLE
                        progress.visibility = View.INVISIBLE
                        messageView.text = getString(R.string.profile_label_friends)
                        messageView.visibility = View.VISIBLE
                        acceptRequest.root.visibility = View.INVISIBLE
                    }
                }
                ProfileDialogViewModel.RelationshipState.REQUEST_SENT -> {
                    binding.apply {
                        addFriend.visibility = View.INVISIBLE
                        progress.visibility = View.INVISIBLE
                        messageView.text = getString(R.string.profile_label_sent)
                        messageView.visibility = View.VISIBLE
                        acceptRequest.root.visibility = View.INVISIBLE
                    }
                }
                ProfileDialogViewModel.RelationshipState.REQUEST_RECEIVED -> {
                    binding.apply {
                        addFriend.visibility = View.INVISIBLE
                        progress.visibility = View.INVISIBLE
                        messageView.visibility = View.INVISIBLE
                        acceptRequest.root.visibility = View.VISIBLE
                    }
                }
                else -> {
                    binding.apply {
                        addFriend.visibility = View.VISIBLE
                        progress.visibility = View.INVISIBLE
                        messageView.visibility = View.INVISIBLE
                        acceptRequest.root.visibility = View.INVISIBLE
                    }
                }
            }
        })
        vm.progress.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.progress.visibility = View.VISIBLE
            }
        })

        binding.addFriend.setOnClickListener {
            vm.addFriend(arguments?.getString(EMAIL)!!)
        }
        binding.acceptRequest.accept.setOnClickListener {
            vm.acceptFriendRequest(arguments?.getString(EMAIL)!!)
        }
        binding.acceptRequest.reject.setOnClickListener {
            vm.rejectFriendRequest(arguments?.getString(EMAIL)!!)
        }

        vm.load(arguments?.getString(EMAIL)!!)

        return AlertDialog.Builder(activity!!)
            .setView(binding.root)
            .setCancelable(false)
            .create()
    }

    override fun performInject() {
        AndroidSupportInjection.inject(this)
    }
}