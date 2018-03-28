package com.tompee.convoy.feature.login.fragment

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.facebook.login.widget.LoginButton
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.tompee.convoy.R
import com.tompee.convoy.base.BaseFragment
import com.tompee.convoy.dependency.component.DaggerAuthComponent
import com.tompee.convoy.dependency.component.DaggerLoginComponent
import com.tompee.convoy.dependency.module.AuthModule
import com.tompee.convoy.dependency.module.LoginModule
import com.tompee.convoy.feature.profile.ProfileActivity
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.fragment_login.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LoginFragment : BaseFragment(), LoginFragmentMvpView {
    @Inject
    lateinit var loginFragmentPresenter: LoginFragmentPresenter

    private lateinit var listener: LoginFragmentListener
    private lateinit var progressDialog: ProgressDialog
    private var facebookResultSubject: BehaviorSubject<Triple<Int, Int, Intent>> = BehaviorSubject.create()
    private var googleResultSubject: BehaviorSubject<Intent> = BehaviorSubject.create()

    companion object {
        const val LOGIN = 0
        const val SIGN_UP = 1
        private const val TYPE_TAG = "type"
        private const val RC_SIGN_IN = 10

        fun newInstance(type: Int): LoginFragment {
            val loginFragment = LoginFragment()
            val bundle = Bundle()
            bundle.putInt(TYPE_TAG, type)
            loginFragment.arguments = bundle
            return loginFragment
        }
    }

    // region View/Presenter initialization
    override fun layoutId(): Int = R.layout.fragment_login

    override fun setupComponent() {
        val loginComponent = DaggerLoginComponent.builder()
                .loginModule(LoginModule(activity!!))
                .authComponent(DaggerAuthComponent.builder().authModule(AuthModule(activity!!)).build())
                .build()
        loginComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginFragmentPresenter.attachView(this)
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
    }

    override fun onDestroy() {
        super.onDestroy()
        loginFragmentPresenter.detachView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        facebookResultSubject.onNext(Triple(requestCode, resultCode, data!!))
        if (RC_SIGN_IN == requestCode) {
            googleResultSubject.onNext(data!!)
        }
    }
    //endregion

    // region Observables
    override fun getType(): Int {
        return arguments?.getInt(TYPE_TAG)!!
    }

    override fun signUpRequest(): Observable<Any> {
        return RxView.clicks(commandButton)
    }

    override fun loginRequest(): Observable<Any> {
        return RxView.clicks(commandButton)
    }

    override fun getEmail(): Observable<String> {
        return RxTextView.textChanges(userView)
                .skipInitialValue()
                .debounce(1, TimeUnit.SECONDS)
                .map { charSequence -> charSequence.toString() }
    }

    override fun getPassword(): Observable<String> {
        return RxTextView.textChanges(passView)
                .skipInitialValue()
                .debounce(1, TimeUnit.SECONDS)
                .map { charSequence -> charSequence.toString() }
    }

    override fun getFacebookLogin(): Observable<LoginButton> {
        return Observable.just(facebookButton)
    }

    override fun googleSignInRequest(): Observable<Any> {
        return RxView.clicks(googleButton)
    }

    override fun facebookResult(): Observable<Triple<Int, Int, Intent>> {
        return facebookResultSubject
    }

    override fun googleResult(): Observable<Intent> {
        return googleResultSubject
    }

    // endregion

    // region Error handling
    override fun showEmailEmptyError() {
        userView.error = getString(R.string.error_field_required)
        userView.requestFocus()
    }

    override fun showEmailInvalidError() {
        userView.error = getString(R.string.error_invalid_email)
        userView.requestFocus()
    }

    override fun showPasswordEmptyError() {
        passView.error = getString(R.string.error_field_required)
        passView.requestFocus()
    }

    override fun showPasswordTooShortError() {
        passView.error = getString(R.string.error_pass_min)
        passView.requestFocus()
    }

    override fun showGenericError(message: String) {
        Snackbar.make(activity?.findViewById(android.R.id.content)!!,
                message, Snackbar.LENGTH_LONG).show()
    }
    // endregion

    // region Exposed APIs
    override fun showProgressDialog() {
        val type = arguments?.getInt(TYPE_TAG)
        if (type == SIGN_UP) {
            progressDialog.setMessage(getString(R.string.progress_login_register))
        } else {
            progressDialog.setMessage(getString(R.string.progress_login_authenticate))
        }
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
        progressDialog.show()
    }

    override fun hideProgressDialog() {
        progressDialog.hide()
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

    override fun showRegistrationSuccessMessage() {
        AlertDialog.Builder(activity!!)
                .setTitle(R.string.email_successful_title)
                .setMessage(R.string.email_rationale)
                .setPositiveButton(R.string.label_positive_button, null)
                .show()
    }

    override fun moveToNextActivity(email: String) {
        val intent = Intent(activity, ProfileActivity::class.java)
        intent.putExtra(ProfileActivity.EMAIL, email)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        activity?.finish()
    }

    override fun startSignInWithIntent(intent: Intent) {
        startActivityForResult(intent, RC_SIGN_IN)
    }

    override fun enableCommand(state: Boolean) {
        commandButton.isEnabled = state
    }
    //endregion
}
