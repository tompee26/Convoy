package com.tompee.convoy.feature.search.profile

import android.app.DialogFragment
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import com.jakewharton.rxbinding2.view.RxView
import com.tompee.convoy.ConvoyApplication
import com.tompee.convoy.R
import com.tompee.convoy.base.BaseDialogFragment
import com.tompee.convoy.dependency.component.DaggerSearchComponent
import com.tompee.convoy.dependency.module.SearchModule
import com.tompee.convoy.interactor.model.User
import io.reactivex.Observable
import kotlinx.android.synthetic.main.view_accept_request.*
import kotlinx.android.synthetic.main.view_profile.*
import javax.inject.Inject

class ProfileDialog : BaseDialogFragment(), ProfileDialogMvpView {
    @Inject
    lateinit var presenter: ProfileDialogPresenter

    companion object {
        private const val EMAIL = "email"
        private const val USER_EMAIL = "user_email"

        fun newInstance(userEmail: String, email: String): ProfileDialog {
            val dialog = ProfileDialog()
            dialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.FragmentDialog)
            val bundle = Bundle()
            bundle.putString(EMAIL, email)
            bundle.putString(USER_EMAIL, userEmail)
            dialog.arguments = bundle
            return dialog
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.attachView(this)
        showProgress()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun layoutId(): Int = R.layout.view_profile

    override fun setupComponent() {
        val component = DaggerSearchComponent.builder()
                .appComponent(ConvoyApplication[activity!!].component)
                .searchModule(SearchModule())
                .build()
        component.inject(this)
    }
    // endregion

    // region Observables
    override fun getUserEmail(): String {
        return arguments?.getString(USER_EMAIL)!!
    }

    override fun getTargetEmail(): String {
        return arguments?.getString(EMAIL)!!
    }

    override fun addFriendRequest(): Observable<Pair<String, String>> {
        return RxView.clicks(addFriend).compose { observable ->
            observable.map { Pair(arguments?.getString(USER_EMAIL)!!, arguments?.getString(EMAIL)!!) }
        }
    }

    override fun acceptRequest(): Observable<Pair<String, String>> {
        return RxView.clicks(accept).compose { observable ->
            observable.map { Pair(arguments?.getString(USER_EMAIL)!!, arguments?.getString(EMAIL)!!) }
        }
    }

    override fun rejectRequest(): Observable<Pair<String, String>> {
        return RxView.clicks(reject).compose { observable ->
            observable.map { Pair(arguments?.getString(USER_EMAIL)!!, arguments?.getString(EMAIL)!!) }
        }
    }

    // endregion

    override fun setProfile(user: User, bitmap: Bitmap) {
        profileImage.setImageBitmap(bitmap)
        name.text = "${user.first} ${user.last}"
        displayName.text = user.display
        displayName.tag = user.email // Tag user email
    }

    override fun showAddFriend() {
        addFriend.visibility = View.VISIBLE
        progress.visibility = View.INVISIBLE
        messageView.visibility = View.INVISIBLE
        acceptRequest.visibility = View.INVISIBLE
    }

    override fun showProgress() {
        addFriend.visibility = View.INVISIBLE
        progress.visibility = View.VISIBLE
        messageView.visibility = View.INVISIBLE
        acceptRequest.visibility = View.INVISIBLE
    }

    override fun showCustomMessage(message: String) {
        addFriend.visibility = View.INVISIBLE
        progress.visibility = View.INVISIBLE
        messageView.text = message
        messageView.visibility = View.VISIBLE
        acceptRequest.visibility = View.INVISIBLE
    }

    override fun showAcceptRequest() {
        addFriend.visibility = View.INVISIBLE
        progress.visibility = View.INVISIBLE
        messageView.visibility = View.INVISIBLE
        acceptRequest.visibility = View.VISIBLE
    }
}