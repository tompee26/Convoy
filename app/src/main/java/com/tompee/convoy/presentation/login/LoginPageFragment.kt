package com.tompee.convoy.presentation.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.tompee.convoy.R
import com.tompee.convoy.databinding.FragmentLoginPageBinding
import com.tompee.convoy.presentation.base.BaseFragment
import com.tompee.convoy.presentation.common.ProgressDialog
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class LoginPageFragment : BaseFragment<FragmentLoginPageBinding>() {

    companion object {
        const val LOGIN = 0
        const val SIGN_UP = 1
        private const val TYPE_TAG = "type"

        fun newInstance(type: Int): LoginPageFragment {
            val loginFragment = LoginPageFragment()
            val bundle = Bundle()
            bundle.putInt(TYPE_TAG, type)
            loginFragment.arguments = bundle
            return loginFragment
        }
    }

    override val layoutId: Int = R.layout.fragment_login_page

    private lateinit var progressDialog: ProgressDialog

    @Inject
    lateinit var factory: LoginViewModel.Factory

    @Inject
    lateinit var facebookAuthHandler: FacebookAuthHandler

    @Inject
    lateinit var googleAuthHandler: GoogleAuthHandler

    override fun performInject() {
        AndroidSupportInjection.inject(this)
    }

    override fun setupBinding(binding: FragmentLoginPageBinding) {
        fun setupViews() {
            val type = arguments?.getInt(TYPE_TAG) ?: LOGIN
            if (type == LOGIN) {
                progressDialog =
                    ProgressDialog.newInstance(R.color.colorLoginButton, R.string.progress_login_authenticate)
                binding.switchButton.text = getString(R.string.label_login_new_account)
                binding.commandButton.apply {
                    text = getString(R.string.label_login_button)
                    setBackgroundResource(R.drawable.ripple_login)
                }
            } else {
                progressDialog = ProgressDialog.newInstance(R.color.colorSignUpButton, R.string.progress_login_register)
                binding.switchButton.text = getString(R.string.label_login_registered)
                binding.commandButton.apply {
                    text = getString(R.string.label_login_sign_up)
                    setBackgroundResource(R.drawable.ripple_sign_up)
                }
                binding.googleButton.visibility = View.GONE
                binding.facebookButton.visibility = View.GONE
                binding.optionTextView.visibility = View.GONE
                binding.leftLineView.visibility = View.GONE
                binding.rightLineView.visibility = View.GONE
            }
        }

        fun setupBinding(vm: LoginViewModel) {
            vm.state.observe(viewLifecycleOwner, Observer {
                when (it) {
                    LoginViewModel.InputState.EMAIL_EMPTY -> {
                        binding.userView.error = getString(R.string.error_field_required)
                    }
                    LoginViewModel.InputState.EMAIL_INVALID -> {
                        binding.userView.error = getString(R.string.error_invalid_email)
                    }
                    LoginViewModel.InputState.EMAIL_OK -> {
                        binding.userView.error = null
                    }
                    LoginViewModel.InputState.PASSWORD_EMPTY -> {
                        binding.passView.error = getString(R.string.error_field_required)
                    }
                    LoginViewModel.InputState.PASSWORD_SHORT -> {
                        binding.passView.error = getString(R.string.error_pass_min)
                    }
                    LoginViewModel.InputState.PASSWORD_OK -> {
                        binding.passView.error = null
                    }
                    else -> {
                        binding.userView.error = null
                        binding.passView.error = null
                    }

                }
            })
            binding.commandButton.setOnClickListener {
                val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view?.windowToken, 0)
                val type = arguments?.getInt(TYPE_TAG) ?: LOGIN
                if (type == LOGIN) {
                    vm.login()
                } else {
                    vm.register()
                }
            }
            vm.progressVisibility.observe(viewLifecycleOwner, Observer {
                if (!userVisibleHint) {
                    return@Observer
                }
                if (it) {
                    progressDialog.show(fragmentManager!!, "progress")
                } else {
                    progressDialog.dismiss()
                }
            })
            vm.registerSuccessful.observe(viewLifecycleOwner, Observer {
                if (!userVisibleHint) {
                    return@Observer
                }
                AlertDialog.Builder(activity!!, R.style.DialogStyle)
                    .setTitle(R.string.label_register_successful_title)
                    .setMessage(R.string.rationale_email_verification)
                    .setPositiveButton(R.string.label_positive_button, null)
                    .show()
            })
            facebookAuthHandler.configureLogin(binding.facebookButton)
                .retry()
                .subscribe { token -> vm.login(token) }
            binding.googleButton.setOnClickListener {
                googleAuthHandler.startLogin()
                    .retry()
                    .subscribe { result -> vm.login(result) }
            }
        }

        val vm = ViewModelProviders.of(activity!!, factory)[LoginViewModel::class.java]
        binding.viewModel = vm

        setupViews()
        setupBinding(vm)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        facebookAuthHandler.onActivityResult(requestCode, resultCode, data)
        googleAuthHandler.onActivityResult(requestCode, resultCode, data)
    }
}