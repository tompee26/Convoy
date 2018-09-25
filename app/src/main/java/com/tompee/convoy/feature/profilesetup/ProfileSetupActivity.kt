package com.tompee.convoy.feature.profilesetup

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.tompee.convoy.ConvoyApplication
import com.tompee.convoy.R
import com.tompee.convoy.base.BaseActivity
import com.tompee.convoy.core.cropper.ImageCropper
import com.tompee.convoy.dependency.component.DaggerNavigatorComponent
import com.tompee.convoy.dependency.component.DaggerProfileComponent
import com.tompee.convoy.dependency.module.NavigatorModule
import com.tompee.convoy.dependency.module.ProfileModule
import com.tompee.convoy.feature.widget.ProgressDialog
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_profile_setup.*
import javax.inject.Inject

class ProfileSetupActivity : BaseActivity(), ProfileSetupView {

    @Inject
    lateinit var profileSetupPresenter: ProfileSetupPresenter

    @Inject
    lateinit var imageCropper: ImageCropper

    private lateinit var progressDialog: ProgressDialog

    //region ProfileSetupActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

    //region BaseActivity
    override fun layoutId(): Int = R.layout.activity_profile_setup

    override fun setupComponent() {
        val navComponent = DaggerNavigatorComponent.builder()
                .navigatorModule(NavigatorModule(this))
                .build()
        DaggerProfileComponent.builder()
                .appComponent(ConvoyApplication[this].component)
                .navigatorComponent(navComponent)
                .profileModule(ProfileModule(this))
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
        progressDialog.show(supportFragmentManager, "dialog")
    }

    override fun dismissProgress() {
        progressDialog.dismiss()
    }

    override fun showError(message: String) {
        Snackbar.make(findViewById(android.R.id.content)!!, message, Snackbar.LENGTH_LONG).show()
    }

    //endregion
}