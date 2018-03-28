package com.tompee.convoy.feature.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.tompee.convoy.R
import com.tompee.convoy.base.BaseActivity
import com.tompee.convoy.dependency.component.DaggerUserComponent
import com.tompee.convoy.dependency.component.DaggerProfileComponent
import com.tompee.convoy.dependency.module.UserModule
import com.tompee.convoy.dependency.module.ProfileModule
import com.tompee.convoy.feature.map.MapActivity
import kotlinx.android.synthetic.main.activity_profile.*
import javax.inject.Inject

class ProfileActivity : BaseActivity(), ProfileMvpView, View.OnClickListener {
    companion object {
        const val EMAIL = "email"
    }

    @Inject
    lateinit var presenter: ProfilePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.attachView(this)
        presenter.start(intent.getStringExtra(EMAIL))
        saveButton.setOnClickListener(this)
    }

    override fun layoutId(): Int = R.layout.activity_profile

    override fun setupComponent() {
        val component = DaggerProfileComponent.builder()
                .userComponent(DaggerUserComponent.builder().userModule(UserModule()).build())
                .profileModule(ProfileModule())
                .build()
        component.inject(this)
    }

    override fun moveToNextActivity(email : String) {
        val intent = Intent(this, MapActivity::class.java)
        intent.putExtra(MapActivity.EMAIL, email)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }

    override fun onClick(view: View) {
        presenter.save(firstName.text.toString(),
                lastName.text.toString(),
                displayName.text.toString(), intent.getStringExtra(EMAIL))
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun showFirstNameError() {
        firstName.error = getString(R.string.error_field_required)
        firstName.requestFocus()
    }

    override fun showLastNameError() {
        lastName.error = getString(R.string.error_field_required)
        lastName.requestFocus()
    }

    override fun showDisplayNameError() {
        displayName.error = getString(R.string.error_field_required)
        displayName.requestFocus()
    }

    override fun setEmail(email: String) {
        emailView.text = email
    }

}