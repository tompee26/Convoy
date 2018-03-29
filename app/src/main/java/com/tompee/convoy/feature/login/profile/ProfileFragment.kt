package com.tompee.convoy.feature.login.profile

import android.content.Context
import android.os.Bundle
import android.view.View
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.tompee.convoy.ConvoyApplication
import com.tompee.convoy.R
import com.tompee.convoy.base.BaseFragment
import com.tompee.convoy.dependency.component.DaggerLoginComponent
import com.tompee.convoy.dependency.module.LoginModule
import com.tompee.convoy.interactor.model.User
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_profile.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ProfileFragment : BaseFragment(), ProfileFragmentMvpView {
    @Inject
    lateinit var presenter: ProfileFragmentPresenter
    private lateinit var listener: ProfileFragment.ProfileFragmentListener

    companion object {
        const val EMAIL = "email"
        fun newInstance(): ProfileFragment {
            return ProfileFragment()
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is ProfileFragment.ProfileFragmentListener) {
            listener = context
        }
    }

    // region View/Presenter setup
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.attachView(this)
        emailView.text = arguments?.getString(EMAIL)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun layoutId(): Int = R.layout.fragment_profile

    override fun setupComponent() {
        val component = DaggerLoginComponent.builder()
                .appComponent(ConvoyApplication[activity!!].component)
                .loginModule(LoginModule(activity!!))
                .build()
        component.inject(this)
    }

    interface ProfileFragmentListener {
        fun onSaveSuccessful(user: User)
    }

    // endregion

    // region Observables
    override fun getFirstName(): Observable<String> {
        return RxTextView.textChanges(firstName)
                .skipInitialValue()
                .debounce(1, TimeUnit.SECONDS)
                .map { it.toString().trim() }
    }

    override fun getLastName(): Observable<String> {
        return RxTextView.textChanges(lastName)
                .skipInitialValue()
                .debounce(1, TimeUnit.SECONDS)
                .map { it.toString().trim() }
    }

    override fun getDisplayName(): Observable<String> {
        return RxTextView.textChanges(displayName)
                .skipInitialValue()
                .debounce(1, TimeUnit.SECONDS)
                .map { it.toString().trim() }
    }

    override fun getEmail(): String {
        return arguments?.getString(EMAIL)!!
    }

    override fun saveRequest(): Observable<Any> {
        return RxView.clicks(saveButton)
    }
    // endregion

    // region Interface methods
    override fun showFirstNameError() {
        firstName.error = getString(R.string.error_field_required)
    }

    override fun showLastNameError() {
        lastName.error = getString(R.string.error_field_required)
    }

    override fun showDisplayNameError() {
        displayName.error = getString(R.string.error_field_required)
    }

    override fun saveSuccessful(user: User) {
        listener.onSaveSuccessful(user)
    }

    override fun setSaveButtonState(state: Boolean) {
        saveButton.isEnabled = state
    }
    // endregion
}