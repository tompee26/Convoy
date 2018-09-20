package com.tompee.convoy.feature.login.profile

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.tompee.convoy.Constants
import com.tompee.convoy.ConvoyApplication
import com.tompee.convoy.R
import com.tompee.convoy.base.BaseFragment
import com.tompee.convoy.dependency.component.DaggerLoginComponent
import com.tompee.convoy.dependency.module.LoginModule
import com.tompee.convoy.interactor.model.User
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.fragment_profile.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ProfileFragment : BaseFragment(), ProfileFragmentMvpView {

    lateinit var presenter: ProfileFragmentPresenter

    private lateinit var listener: ProfileFragment.ProfileFragmentListener
    private val imageSubject = BehaviorSubject.create<Uri>()

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
        RxView.clicks(profileImage).subscribe({
            startImageCrop()
        })
    }

    private fun startImageCrop() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(CropImageView.CropShape.OVAL)
                .setMinCropResultSize(Constants.IMAGE_SIZE, Constants.IMAGE_SIZE)
                .setRequestedSize(Constants.IMAGE_SIZE, Constants.IMAGE_SIZE,
                        CropImageView.RequestSizeOptions.RESIZE_INSIDE)
                .setAspectRatio(1, 1)
                .setActivityMenuIconColor(ContextCompat.getColor(context!!, R.color.colorLight))
                .setAllowFlipping(false)
                .setActivityTitle(getString(R.string.profile_label_picture))
                .start(context!!, this)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                val resultUri = result.uri
                profileImage.setImageURI(resultUri)
                imageSubject.onNext(resultUri)
            }
        }
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

    override fun getImage(): Observable<Uri> {
        return imageSubject
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