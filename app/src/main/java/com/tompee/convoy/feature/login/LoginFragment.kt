package com.tompee.convoy.feature.login

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.tompee.convoy.R
import com.tompee.convoy.base.BaseFragment
import com.tompee.convoy.dependency.component.DaggerLoginComponent
import com.tompee.convoy.dependency.module.LoginModule
import kotlinx.android.synthetic.main.fragment_login.*
import javax.inject.Inject

class LoginFragment : BaseFragment(), LoginMvpView, View.OnClickListener {
    @Inject
    lateinit var loginPresenter: LoginPresenter

    private lateinit var listener: LoginFragmentListener
    private lateinit var progressDialog: ProgressDialog

    companion object {
        const val LOGIN = 0
        const val SIGN_UP = 1
        private const val TYPE_TAG = "type"

        fun newInstance(type: Int): LoginFragment {
            val loginFragment = LoginFragment()
            val bundle = Bundle()
            bundle.putInt(TYPE_TAG, type)
            loginFragment.arguments = bundle
            return loginFragment
        }
    }

    override fun layoutId(): Int = R.layout.fragment_login

    override fun setupComponent() {
        val loginComponent = DaggerLoginComponent.builder().loginModule(LoginModule(activity!!)).build()
        loginComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginPresenter.attachView(this)
        val type = arguments?.getInt(TYPE_TAG)
        switchButton.setOnClickListener { listener.onSwitchPage(type!!) }
        if (type == LOGIN) {
            progressDialog = ProgressDialog(context, R.style.AppTheme_Login_Dialog)
            switchButton.text = getString(R.string.label_login_new_account)
            commandButton.text = getString(R.string.label_login_button)
            commandButton.setBackgroundResource(R.drawable.ripple_login)
        } else {
            progressDialog = ProgressDialog(context, R.style.AppTheme_SignUp_Dialog)
            switchButton.text = getString(R.string.label_login_registered)
            commandButton.text = getString(R.string.label_login_sign_up)
            commandButton.setBackgroundResource(R.drawable.ripple_sign_up)
            googleButton.visibility = View.GONE
            facebookButton.visibility = View.GONE
            optionTextView.visibility = View.GONE
            leftLineView.visibility = View.GONE
            rightLineView.visibility = View.GONE
        }
        progressDialog.isIndeterminate = true
        commandButton.setOnClickListener(this)

        if (type == LOGIN) {
            loginPresenter.configureFacebookLogin(facebookButton)
            loginPresenter.configureGoogleLogin(googleButton)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        loginPresenter.detachView()
    }

    override fun onClick(v: View?) {
        val type = arguments?.getInt(TYPE_TAG)
        userView.error = null
        passView.error = null

        if (type == SIGN_UP) {
            loginPresenter.startSignUp(userView.text.toString(), passView.text.toString())
        } else {
            loginPresenter.startLogin(userView.text.toString(), passView.text.toString())
        }
    }

    override fun showProgressDialog() {
        val type = arguments?.getInt(TYPE_TAG)
        if (type == SIGN_UP) {
            progressDialog.setMessage(getString(R.string.progress_login_register))
        } else {
            progressDialog.setMessage(getString(R.string.progress_login_authenticate))
        }
        progressDialog.show()
    }

    override fun showEmailEmptyError() {
        userView.error = getString(R.string.error_login_required)
        userView.requestFocus()
    }

    override fun showEmailInvalidError() {
        userView.error = getString(R.string.error_login_invalid_email)
        userView.requestFocus()
    }

    override fun showPasswordEmptyError() {
        passView.error = getString(R.string.error_login_required)
        passView.requestFocus()
    }

    override fun showPasswordTooShortError() {
        passView.error = getString(R.string.error_login_pass_min)
        passView.requestFocus()
    }

    interface LoginFragmentListener {
        fun onSwitchPage(type: Int)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is LoginFragmentListener) {
            listener = context
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        loginPresenter.onActivityResult(requestCode, resultCode, data!!)
    }

    private fun moveToMainActivity() {
    }
}
