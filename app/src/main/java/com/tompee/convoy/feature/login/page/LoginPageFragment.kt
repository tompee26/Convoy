package com.tompee.convoy.feature.login.page

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.tompee.convoy.R
import com.tompee.convoy.base.BaseFragment
import com.tompee.convoy.core.auth.FacebookAuthHandler
import com.tompee.convoy.core.auth.GoogleAuthHandler
import com.tompee.convoy.feature.login.LoginFragment
import com.tompee.convoy.feature.widget.ProgressDialog
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.android.synthetic.main.fragment_login_page.*
import javax.inject.Inject

class LoginPageFragment : BaseFragment(), LoginPageView {

    @Inject
    lateinit var loginPagePresenter: LoginPagePresenter
    @Inject
    lateinit var facebookAuthHandler: FacebookAuthHandler
    @Inject
    lateinit var googleAuthHandler: GoogleAuthHandler

    private lateinit var listener: PageSwitchListener
    private lateinit var progressDialog: ProgressDialog

    //region Initializer
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
    //endregion

    // region BaseFragment
    override fun layoutId(): Int = R.layout.fragment_login_page

    override fun setupComponent() {
        LoginFragment[parentFragment!!].component.inject(this)
    }
    //endregion

    //region LoginPageFragment
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is PageSwitchListener) {
            listener = context
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginPagePresenter.attachView(this)
        val type = arguments?.getInt(TYPE_TAG) ?: LOGIN
        switchButton.setOnClickListener { listener.onPageSwitch(type) }
        if (type == LOGIN) {
            progressDialog = ProgressDialog.newInstance(R.color.colorLoginButton, R.string.progress_login_authenticate)
            switchButton.text = getString(R.string.label_login_new_account)
            commandButton.text = getString(R.string.label_login_button)
            commandButton.setBackgroundResource(R.drawable.ripple_login)
        } else {
            progressDialog = ProgressDialog.newInstance(R.color.colorSignUpButton, R.string.progress_login_register)
            switchButton.text = getString(R.string.label_login_registered)
            commandButton.text = getString(R.string.label_login_sign_up)
            commandButton.setBackgroundResource(R.drawable.ripple_sign_up)
            googleButton.visibility = View.GONE
            facebookButton.visibility = View.GONE
            optionTextView.visibility = View.GONE
            leftLineView.visibility = View.GONE
            rightLineView.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        loginPagePresenter.detachView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        facebookAuthHandler.onActivityResult(requestCode, resultCode, data)
        googleAuthHandler.onActivityResult(requestCode, resultCode, data)
    }
    //endregion

    //region LoginPageView
    override fun getEmail(): Observable<String> = RxTextView.textChanges(userView)
            .skipInitialValue()
            .map { it.toString() }
            .map { it.trim() }

    override fun getPassword(): Observable<String> = RxTextView.textChanges(passView)
            .skipInitialValue()
            .map { it.toString() }
            .map { it.trim() }

    override fun getViewType(): Int = arguments?.getInt(TYPE_TAG) ?: LOGIN

    override fun command(): Observable<Any> = RxView.clicks(commandButton)
            .doOnNext { hideKeyboard() }

    override fun loginWithFacebook(): Single<AccessToken> =
            facebookAuthHandler.configureLogin(facebookButton)

    override fun loginWithGoogle(): Observable<GoogleSignInResult> = RxView.clicks(googleButton)
            .flatMapSingle { googleAuthHandler.startLogin() }

    override fun showEmailEmptyError() {
        userView.error = getString(R.string.error_field_required)
        userView.requestFocus()
    }

    override fun showEmailInvalidError() {
        userView.error = getString(R.string.error_invalid_email)
        userView.requestFocus()
    }

    override fun clearEmailError() {
        userView.error = null
    }

    override fun showPasswordEmptyError() {
        passView.error = getString(R.string.error_field_required)
        passView.requestFocus()
    }

    override fun showPasswordTooShortError() {
        passView.error = getString(R.string.error_pass_min)
        passView.requestFocus()
    }

    override fun clearPasswordError() {
        passView.error = null
    }

    override fun showProgressDialog() {
        progressDialog.show(fragmentManager, "progress")
    }

    override fun dismissProgressDialog() {
        progressDialog.dismiss()
    }

    override fun showSignupSuccessMessage() {
        AlertDialog.Builder(activity!!, R.style.DialogStyle)
                .setTitle(R.string.signup_successful_title)
                .setMessage(R.string.rationale_email_verification)
                .setPositiveButton(R.string.label_positive_button, null)
                .show()
    }

    override fun showError(message: String) {
        Snackbar.make(activity?.findViewById(android.R.id.content)!!,
                message, Snackbar.LENGTH_LONG).show()
    }

    //endregion

    //region Private methods
    private fun hideKeyboard() {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }
    //endregion
}
