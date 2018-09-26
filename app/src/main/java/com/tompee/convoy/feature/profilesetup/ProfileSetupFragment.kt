package com.tompee.convoy.feature.profilesetup

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.tompee.convoy.ConvoyApplication
import com.tompee.convoy.R
import com.tompee.convoy.base.BaseFragment
import com.tompee.convoy.core.cropper.ImageCropper
import com.tompee.convoy.dependency.component.DaggerProfileComponent
import com.tompee.convoy.dependency.module.ProfileModule
import com.tompee.convoy.feature.navhost.NavigationHostActivity
import com.tompee.convoy.feature.widget.ProgressDialog
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_profile_setup.*
import javax.inject.Inject

class ProfileSetupFragment : BaseFragment(), ProfileSetupView {

    @Inject
    lateinit var profileSetupPresenter: ProfileSetupPresenter

    @Inject
    lateinit var imageCropper: ImageCropper

    private lateinit var progressDialog: ProgressDialog

    //region ProfileSetupFragment
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog = ProgressDialog.newInstance(R.color.colorPrimary, R.string.progress_save_profile)
        profileSetupPresenter.attachView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        profileSetupPresenter.detachView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        imageCropper.onActivityResult(requestCode, resultCode, data)
    }
    //endregion

    //region BaseFragment
    override fun layoutId(): Int = R.layout.fragment_profile_setup

    override fun setupComponent() {
        DaggerProfileComponent.builder()
                .appComponent(ConvoyApplication[activity!!].component)
                .navigatorComponent(NavigationHostActivity[activity!!].component)
                .profileModule(ProfileModule(this, context!!))
                .build()
                .inject(this)
    }
    //endregion

    //region ProfileSetupView
    override fun getFirstName(): Observable<String> = RxTextView.textChanges(firstName)
            .skipInitialValue()
            .map { it.toString() }
            .map { it.trim() }

    override fun getLastName(): Observable<String> = RxTextView.textChanges(lastName)
            .skipInitialValue()
            .map { it.toString() }
            .map { it.trim() }

    override fun getDisplayName(): Observable<String> = RxTextView.textChanges(displayName)
            .skipInitialValue()
            .map { it.toString() }
            .map { it.trim() }

    override fun getImageUrl(): Observable<String> = RxView.clicks(imageUpload)
            .flatMap { _ ->
                imageCropper.startImageCropper(profileImage)
                        .take(1)
                        .map { it.toString() }
            }

    override fun saveRequest(): Observable<Any> = RxView.clicks(saveButton)

    override fun showEmptyFirstNameError() {
        firstName.error = getString(R.string.error_field_required)
        firstName.requestFocus()
    }

    override fun showEmptyLastNameError() {
        lastName.error = getString(R.string.error_field_required)
        lastName.requestFocus()
    }

    override fun showEmptyDisplayNameError() {
        displayName.error = getString(R.string.error_field_required)
        displayName.requestFocus()
    }

    override fun showProgress() {
        progressDialog.show(childFragmentManager, "dialog")
    }

    override fun dismissProgress() {
        progressDialog.dismiss()
    }

    override fun showError(message: String) {
        Snackbar.make(activity?.findViewById(android.R.id.content)!!,
                message, Snackbar.LENGTH_LONG).show()
    }

    //endregion
}