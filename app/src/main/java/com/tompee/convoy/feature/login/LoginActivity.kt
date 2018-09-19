package com.tompee.convoy.feature.login

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.View
import com.tompee.convoy.ConvoyApplication
import com.tompee.convoy.R
import com.tompee.convoy.base.BaseActivity
import com.tompee.convoy.dependency.component.DaggerLoginComponent
import com.tompee.convoy.dependency.module.LoginModule
import com.tompee.convoy.feature.login.adapters.LoginPagerAdapter
import com.tompee.convoy.feature.login.adapters.ProfilePagerAdapter
import com.tompee.convoy.feature.login.adapters.ProgressPagerAdapter
import com.tompee.convoy.feature.login.login.LoginFragment
import com.tompee.convoy.feature.login.profile.ProfileFragment
import com.tompee.convoy.feature.map.MapActivity
import com.tompee.convoy.interactor.model.User
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.activity_login.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject

class LoginActivity : BaseActivity(), LoginView, ViewPager.PageTransformer,
        ViewPager.OnPageChangeListener, LoginFragment.LoginFragmentListener,
        ProfileFragment.ProfileFragmentListener {
    companion object {
        private const val CAMERA_DISK_PERMISSION = 123
    }

    @Inject
    lateinit var loginPagerAdapter: LoginPagerAdapter
    @Inject
    lateinit var progressAdapter: ProgressPagerAdapter
    @Inject
    lateinit var profileAdapter: ProfilePagerAdapter
    @Inject
    lateinit var loginPresenter: LoginPresenter

    private val loginFinishedSubject = BehaviorSubject.create<String>()
    private val saveFinishedSubject = BehaviorSubject.create<User>()

    // region View/Presenter setup
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginPresenter.attachView(this)
    }

    override fun layoutId(): Int = R.layout.activity_login

    override fun setupComponent() {
        val loginComponent = DaggerLoginComponent.builder()
                .loginModule(LoginModule(this))
                .appComponent(ConvoyApplication[this].component)
                .build()
        loginComponent.inject(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        loginPresenter.detachView()
    }

    @AfterPermissionGranted(CAMERA_DISK_PERMISSION)
    private fun checkAndRequestPermission() {
        val perms = arrayOf(Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (!EasyPermissions.hasPermissions(this, *perms)) {
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_location),
                    CAMERA_DISK_PERMISSION, *perms)
        } else {
            viewpager.adapter = profileAdapter
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fragment = loginPagerAdapter.getItem(viewpager.currentItem)
        fragment.onActivityResult(requestCode, resultCode, data)
    }

    override fun transformPage(view: View, position: Float) {
        val pageWidth = view.width
        when {
            position < -1 -> view.alpha = 0f
            position <= 1 -> {
                view.findViewById<View>(R.id.tv_app_name).translationX = -(pageWidth * position)
                view.findViewById<View>(R.id.tv_app_subtitle).translationX = -(pageWidth * position)

                view.findViewById<View>(R.id.userView).translationX = pageWidth * position
                view.findViewById<View>(R.id.tv_user_label).translationX = pageWidth * position
                view.findViewById<View>(R.id.view_user_underline).translationX = pageWidth * position
                view.findViewById<View>(R.id.profileImage).translationX = pageWidth * position

                view.findViewById<View>(R.id.passView).translationX = pageWidth * position
                view.findViewById<View>(R.id.tv_pass_label).translationX = pageWidth * position
                view.findViewById<View>(R.id.view_pass_underline).translationX = pageWidth * position
                view.findViewById<View>(R.id.iv_pass_icon).translationX = pageWidth * position

                view.findViewById<View>(R.id.commandButton).translationX = -(pageWidth * position)
            }
            else -> view.alpha = 0f
        }
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        val x = ((viewpager.width * position + positionOffsetPixels) * computeFactor())
        scrollView.scrollTo(x.toInt() + imageView.width / 3, 0)
    }

    override fun onPageSelected(position: Int) {
    }

    override fun onSwitchPage(type: Int) {
        if (type == LoginFragment.LOGIN) {
            viewpager.currentItem = 1
        } else {
            viewpager.currentItem = 0
        }
    }

    override fun onLoginFinished(email: String) {
        viewpager.setPageTransformer(false, null)
        viewpager.removeOnPageChangeListener(this)
        viewpager.adapter = progressAdapter
        loginFinishedSubject.onNext(email)
    }

    override fun onSaveSuccessful(user: User) {
        viewpager.adapter = progressAdapter
        saveFinishedSubject.onNext(user)
    }

    private fun computeFactor(): Float {
        return (imageView.width / 2 - viewpager.width) / (viewpager.width *
                loginPagerAdapter.count - 1).toFloat()
    }
    // endregion

    // region Observables
    override fun loginEmail(): Observable<String> {
        return loginFinishedSubject
    }

    override fun saveUser(): Observable<User> {
        return saveFinishedSubject
    }
    // endregion

    // region Interface methods
    override fun showLoginScreen() {
        viewpager.setPageTransformer(false, this)
        viewpager.addOnPageChangeListener(this)
        viewpager.adapter = loginPagerAdapter
    }

    override fun showProfileSetupScreen(email: String) {
        viewpager.setPageTransformer(false, null)
        viewpager.removeOnPageChangeListener(this)
        val bundle = Bundle()
        bundle.putString(ProfileFragment.EMAIL, email)
        profileAdapter.getItem(0).arguments = bundle
        checkAndRequestPermission()
    }

    override fun moveToNextActivity(email: String) {
        val intent = Intent(this, MapActivity::class.java)
        intent.putExtra(MapActivity.EMAIL, email)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }
    // endregion
}