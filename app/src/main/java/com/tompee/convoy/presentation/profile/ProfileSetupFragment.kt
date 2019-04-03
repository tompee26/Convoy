package com.tompee.convoy.presentation.profile

import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.tompee.convoy.R
import com.tompee.convoy.databinding.FragmentProfileSetupBinding
import com.tompee.convoy.presentation.base.BaseFragment
import com.tompee.convoy.presentation.common.ProgressDialog
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class ProfileSetupFragment : BaseFragment<FragmentProfileSetupBinding>() {

    @Inject
    lateinit var imageCropper: ImageCropper

    @Inject
    lateinit var factory: ProfileSetupViewModel.Factory

    private lateinit var progressDialog: ProgressDialog

    override val layoutId: Int = R.layout.fragment_profile_setup

    override fun performInject() {
        AndroidSupportInjection.inject(this)
    }

    override fun setupBinding(binding: FragmentProfileSetupBinding) {
        val vm = ViewModelProviders.of(this, factory)[ProfileSetupViewModel::class.java]
        binding.viewModel = vm
        progressDialog = ProgressDialog.newInstance(R.color.colorPrimary, R.string.progress_save_profile)
        binding.imageUpload.setOnClickListener {
            imageCropper.startImageCropper(binding.profileImage)
                .take(1)
                .map { it.toString() }
                .subscribe { vm.imageUrl.set(it) }
        }
        vm.inputState.observe(this, Observer {
            when (it) {
                ProfileSetupViewModel.InputState.FIRST_NAME_EMPTY ->
                    binding.firstName.error = getString(R.string.error_field_required)
                ProfileSetupViewModel.InputState.FIRST_NAME_OK ->
                    binding.firstName.error = null
                ProfileSetupViewModel.InputState.LAST_NAME_EMPTY ->
                    binding.lastName.error = getString(R.string.error_field_required)
                ProfileSetupViewModel.InputState.LAST_NAME_OK ->
                    binding.lastName.error = null
                ProfileSetupViewModel.InputState.DISPLAY_NAME_EMPTY ->
                    binding.displayName.error = getString(R.string.error_field_required)
                ProfileSetupViewModel.InputState.DISPLAY_NAME_OK ->
                    binding.displayName.error = null
                else -> {
                    binding.firstName.error = null
                    binding.lastName.error = null
                    binding.displayName.error = null
                }
            }
        })
        vm.progressVisible.observe(this, Observer {
            if (it) {
                progressDialog.show(fragmentManager!!, "progress")
            } else {
                progressDialog.dismiss()
            }
        })
        vm.message.observe(this, Observer {
            Snackbar.make(
                activity?.findViewById(android.R.id.content)!!,
                it, Snackbar.LENGTH_LONG
            ).show()
        })
        val navigator = findNavController()
        vm.exit.observe(this, Observer {
            navigator.navigate(R.id.action_profileSetupFragment_to_mapFragment)
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        imageCropper.onActivityResult(requestCode, resultCode, data)
    }
}